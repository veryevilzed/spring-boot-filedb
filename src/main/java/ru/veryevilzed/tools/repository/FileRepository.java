package ru.veryevilzed.tools.repository;


import lombok.extern.slf4j.Slf4j;
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
import java.util.stream.Collectors;

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

        List<File> filesList = Arrays.asList(dir.listFiles());
        filesList.sort((b,a) -> b.getName().compareTo(a.getName()));

        for (File file : filesList ){
            if (file.isDirectory())
                update(file);
            String name = file.getAbsolutePath().replace(rootDirectory.getAbsolutePath()+"/", "");
            Matcher matcher = pattern.matcher(name);
            if (!matcher.matches())
                continue;

            T entry = createFileEntity(file);
            if (files.contains(entry))
                continue;

            for (KeyCollection key : keys.values()) {
                String keyTextValue = matcher.group(key.getName());
                if (keyTextValue != null || key.getDefaultKey() != null || key.isNullable())
                    entry.addKey(key, key.parseKey(keyTextValue, entry));
            }

            files.add(entry);
        }

        for(FileEntity file : new ArrayList<>(files)){
            if (!file.exists()){
                log.trace("Remove file: {}", file);
                files.remove(file);
                for(KeyCollection key : file.getKeys().keySet())
                    key.remove(file.getKeys().get(key), file);
            }else {
                log.trace("Update file: {}", file);
                file.checkForUpdate();
            }
        }
    }

    public synchronized void update() {
        log.debug("Updating file repository {}", this.getClass().getName());
        if (updateLaunchCounter > 0){
            updateLaunchCounter++;
            log.debug("Wait");
            try {
                this.wait();
            }catch (InterruptedException e){
            }finally {
                updateLaunchCounter--;
            }
            return;
        }

        updateLaunchCounter++;

        try {
            update(this.rootDirectory);
        }catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException | InterruptedException e){
            log.error("Update error: {}", e.getMessage());
        }finally {
            notifyAll();
            updateLaunchCounter--;
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
            if (resultSet == null) {
                resultSet = new OrderedSet<>();
            }

            if (request.isUseDefault()){
                request = request.getDefaultRequest();
                resultSet.addAll(get(request.getName()).get(request.getKey(), SortedComparableTypes.Equals));
            }
            results.add(resultSet);

        }
        if (results.size() == 0)
            return null;

//        for(int i=0;i<results.size();i++) {
//            log.trace("Keys:{} {} = {}",  i, requests[i].getName(), results.get(i).stream().map(j -> j.getFile().getName()).collect(Collectors.toList()));
//        }

        for(Set<T> result : results){
            if (res == null) {
                res = new OrderedSet<>(result);
            }else
                res.retainAll(result);
        }



        //log.trace("Result:{}",res.stream().map(i -> i.getFile().getName()).collect(Collectors.toList()));
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
        log.info("Starting file repository. Path:{}", path);
        files = new HashSet<>();
        if (!rootDirectory.exists())
            throw new DirectoryNotExists(path);
        this.pattern = Pattern.compile(pattern);
        this.keys = new HashMap<>();
        for(KeyCollection key : keys)
             this.keys.put(key.getName(), key);
    }
}
