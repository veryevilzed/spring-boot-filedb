package ru.veryevilzed.tools.repository;


import lombok.extern.slf4j.Slf4j;
import ru.veryevilzed.tools.dto.FileEntity;
import ru.veryevilzed.tools.dto.KeyCollection;
import ru.veryevilzed.tools.dto.KeyRequest;
import ru.veryevilzed.tools.exceptions.DirectoryNotExists;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Абстрактный репозиторий файлов
 */
@Slf4j
public abstract class FileRepository<T extends FileEntity> {

    private final File rootDirectory;
    private final Pattern pattern;
    private final Map<String, KeyCollection> keys;
    private final Set<T> files;

    private Class<T> clazz;

    protected abstract T createFileEntity(File file);

    private void update(File dir) throws NoSuchMethodException, InstantiationException, InvocationTargetException, IllegalAccessException {
        for (File file : dir.listFiles()){
            if (file.isDirectory())
                update(file);
            String name = file.getAbsolutePath().replace(rootDirectory.getAbsolutePath()+"/", "");
            T entry = createFileEntity(file); //  T(file);
            if (files.contains(entry))
                continue;

            Matcher matcher = pattern.matcher(name);
            if (matcher.matches()) {
                for (KeyCollection key : keys.values()) {
                    String keyTextValue = matcher.group(key.getName());
                    if (keyTextValue != null || key.getDefaultKey() != null || key.isNullable())
                        entry.addKey(key, key.parseKey(keyTextValue, entry));
                }
            }
            files.add(entry);
        }

        for(FileEntity file : new ArrayList<>(files)){
            if (!file.exists()){
                files.remove(file);
                for(KeyCollection key : file.getKeys().keySet())
                    key.remove(file.getKeys().get(key));
            }else
                file.checkForUpdate();
        }
    }

    public void update() {
        try {
            update(this.rootDirectory);
        }catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e){
            log.error("Update error: {}", e.getMessage());
        }
    }

    public KeyCollection get(String key) {
        return keys.get(key);
    }

    public Set<T> get(KeyRequest... requests) {
        Set<T> res = null;
        List<Set<T>> results = new ArrayList<>();

        for(KeyRequest request : requests) {
            Set<T> resultSet = get(request.getName()).get(request.getKey(), request.getType());
            if (resultSet == null && request.isUseDefault()){
                request = request.getDefaultRequest();
                resultSet = get(request.getName()).get(request.getKey(), request.getType());
            }

            if (resultSet == null)
                resultSet = new HashSet<>();

            results.add(resultSet);

        }

        if (results.size() == 0)
            return new HashSet<>();

        for(Set<T> result : results){
            if (res == null)
                res = new HashSet<>(result);
            else
                res.retainAll(result);
        }

        return res;
    }


    public FileRepository(String path, String pattern, KeyCollection[] keys) {
        this.rootDirectory = new File(path);

        files = new HashSet<>();
        if (!rootDirectory.exists())
            throw new DirectoryNotExists(path);
        this.pattern = Pattern.compile(pattern);
        this.keys = new HashMap<>();
        for(KeyCollection key : keys)
             this.keys.put(key.getName(), key);
    }
}
