package ru.veryevilzed.tools.repository;


import lombok.extern.slf4j.Slf4j;
import ru.veryevilzed.tools.dto.FileEntity;
import ru.veryevilzed.tools.dto.KeyCollection;
import ru.veryevilzed.tools.dto.KeyRequest;
import ru.veryevilzed.tools.exceptions.DirectoryNotExists;
import ru.veryevilzed.tools.utils.SortedComparableTypes;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Абстрактный репозиторий файлов
 */
@Slf4j
public abstract class FileRepository {

    private final File rootDirectory;
    private final Pattern pattern;
    private final Map<String, KeyCollection> keys;

    private void update(File dir) {
        for (File file : dir.listFiles()){
            if (file.isDirectory())
                update(file);
            String name = file.getAbsolutePath().replace(rootDirectory.getAbsolutePath()+"/", "");
            FileEntity entry = new FileEntity(file);

            Matcher matcher = pattern.matcher(name);
            if (matcher.matches()) {
                for (KeyCollection key : keys.values()) {
                    String keyTextValue = matcher.group(key.getName());
                    if (keyTextValue != null || key.getDefaultKey() != null || key.isNullable())
                        key.parseKey(keyTextValue, entry);
                }
            }

        }
    }

    public void update() {
        update(this.rootDirectory);
    }

    public KeyCollection get(String key) {
        return keys.get(key);
    }

    public Set<FileEntity> get(KeyRequest... requests) {
        Set<FileEntity> res = null;
        List<Set<FileEntity>> results = new ArrayList<>();

        for(KeyRequest request : requests) {
            Set<FileEntity> resultSet = get(request.getName()).get(request.getKey(), request.getType());
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

        for(Set<FileEntity> result : results){
            if (res == null)
                res = new HashSet<>(result);
            else
                res.retainAll(result);
        }

        return res;
    }


    public FileRepository(String path, String pattern, KeyCollection[] keys) {
        this.rootDirectory = new File(path);
        if (!rootDirectory.exists())
            throw new DirectoryNotExists(path);
        this.pattern = Pattern.compile(pattern);
        this.keys = new HashMap<>();
        for(KeyCollection key : keys)
             this.keys.put(key.getName(), key);
    }
}
