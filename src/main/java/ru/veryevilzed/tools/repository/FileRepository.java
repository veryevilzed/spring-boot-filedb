package ru.veryevilzed.tools.repository;


import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import ru.veryevilzed.tools.dto.FileEntity;
import ru.veryevilzed.tools.dto.KeyCollection;
import ru.veryevilzed.tools.dto.KeyRequest;
import ru.veryevilzed.tools.exceptions.DirectoryNotExists;
import ru.veryevilzed.tools.utils.OrderedSet;
import ru.veryevilzed.tools.utils.SortedComparableTypes;

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

    private int updateLaunchCounter = 0;

    @SuppressWarnings({"ConstantConditions", "unchecked", "SuspiciousMethodCalls"})
    private synchronized void update(File dir)
            throws NoSuchMethodException,
            InstantiationException,
            InvocationTargetException,
            IllegalAccessException,
            InterruptedException {

        if (updateLaunchCounter > 0){
            updateLaunchCounter++;
            this.wait();
            updateLaunchCounter--;
            return;
        }

        updateLaunchCounter++;

        for (File file : dir.listFiles()){
            if (file.isDirectory())
                update(file);
            String name = file.getAbsolutePath().replace(rootDirectory.getAbsolutePath()+"/", "");
            T entry = createFileEntity(file);
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
                    key.remove(file.getKeys().get(key), file);
            }else
                file.checkForUpdate();
        }
        this.notifyAll();
        updateLaunchCounter--;
    }

    public void update() {
        try {
            update(this.rootDirectory);
        }catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException | InterruptedException e){
            log.error("Update error: {}", e.getMessage());
        }
    }

    public KeyCollection get(String key) {
        return keys.get(key);
    }

    @SuppressWarnings("unchecked")
    public T get(KeyRequest... requests) {
        OrderedSet<T> res = null;
        List<Set<T>> results = new ArrayList<>();

        for(KeyRequest request : requests) {
            OrderedSet<T> resultSet = get(request.getName()).get(request.getKey(), request.getType());
            if ((resultSet == null || resultSet.isEmpty())  && request.isUseDefault()){
                request = request.getDefaultRequest();
                resultSet = get(request.getName()).get(request.getKey(), SortedComparableTypes.Equals);
            }
            if (resultSet == null) {
                resultSet = new OrderedSet<>();
            }
            results.add(resultSet);

        }
        if (results.size() == 0)
            return null;

        for(Set<T> result : results){
            if (res == null) {
                res = new OrderedSet<>(result);
            }else
                res.retainAll(result);
        }

        for(T result : res)
            if (!result.exists() && !result.hasData()){
                this.update();
                return get(requests);
            }

        if (res.size() == 0)
            return null;
        return res.iterator().next();
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
