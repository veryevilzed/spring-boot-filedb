package ru.veryevilzed.tools.repository;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import ru.veryevilzed.tools.dto.FileEntity;
import ru.veryevilzed.tools.dto.KeyCollection;
import ru.veryevilzed.tools.dto.KeyRequest;
import ru.veryevilzed.tools.exceptions.DirectoryNotExists;
import ru.veryevilzed.tools.utils.SortedComparableTypes;

/**
 * Абстрактный репозиторий файлов
 */
@Slf4j
public abstract class FileRepository<T extends FileEntity> {

    private final File rootDirectory;
    private final Pattern pattern;
    private final Map<String, KeyCollection> keys = new HashMap<>();
    private final Set<T> files = new HashSet<>();;

    protected abstract T createFileEntity(File file);

    private int updateLaunchCounter = 0;

    public FileRepository(String path, String pattern, KeyCollection[] keys) {
        this.rootDirectory = new File(path);
        log.info("Initializing file repository at {}", path);
        if (!rootDirectory.exists()) {
            throw new DirectoryNotExists(path);
        }
        this.pattern = Pattern.compile(pattern);
        for (KeyCollection key : keys) {
            this.keys.put(key.getName(), key);
        }
    }

    private synchronized void update(File dir) {

        List<File> filesList = Arrays.asList(dir.listFiles());
        filesList.sort((b, a) -> b.getName().compareTo(a.getName()));

        for (File file : filesList) {
            if (file.isDirectory()) {
                update(file);
            }
            String name = file.getAbsolutePath().replace(rootDirectory.getAbsolutePath() + "/", "");
            Matcher matcher = pattern.matcher(name);
            if (!matcher.matches()) {
                continue;
            }

            T entry = createFileEntity(file);
            if (files.contains(entry)) {
                continue;
            }

            for (KeyCollection key : keys.values()) {
                String keyTextValue = matcher.group(key.getName());
                if (keyTextValue != null || key.getDefaultKey() != null || key.isNullable()) {
                    entry.addKey(key, key.parseKey(keyTextValue, entry));
                }
            }

            files.add(entry);
        }

        for (FileEntity<T> file : new ArrayList<>(files)) {
            if (!file.exists()) {
                log.trace("File removed: {}", file);
                files.remove(file);
                for (KeyCollection key : file.getKeys().keySet()) {
                    key.remove(file.getKeys().get(key), file);
                }
            } else {
                log.trace("File updated: {}", file);
                file.checkForUpdate();
            }
        }
    }

    public synchronized void update() {
        log.debug("Updating file repository {}", this.getClass().getName());
        if (updateLaunchCounter > 0) {
            updateLaunchCounter++;
            log.debug("Wait");
            try {
                this.wait();
            } catch (InterruptedException e) {
            } finally {
                updateLaunchCounter--;
            }
            return;
        }

        updateLaunchCounter++;

        update(this.rootDirectory);
        notifyAll();
        updateLaunchCounter--;
    }

    public KeyCollection get(String key) {
        return keys.get(key);
    }

    /**
     * See a note before the return statement !!!
     */
    @SuppressWarnings("unchecked")
    public T get(KeyRequest... requests) {

        List<Set<T>> results = new ArrayList<>();

        // Perform selection according to each of the requests, saving the results in separate sets
        for (KeyRequest request : requests) {
            LinkedHashSet<T> resultSet = get(request.getName()).get(request.getKey(), request.getType());
            if (resultSet == null) {
                resultSet = new LinkedHashSet<>();
            }

            if (request.isUseDefault()) {
                request = request.getDefaultRequest();
                resultSet.addAll(get(request.getName()).get(request.getKey(), SortedComparableTypes.EqualTo));
            }
            results.add(resultSet);

        }
        if (results.isEmpty()) {
            return null;
        }

//        for(int i=0;i<results.size();i++) {
//            log.trace("Keys:{} {} = {}",  i, requests[i].getName(), results.get(i).stream().map(j -> j.getFile().getName()).collect(Collectors.toList()));
//        }

        // Intersect all sets sequentially
        LinkedHashSet<T> res = null;
        for (Set<T> result : results) {
            if (res == null) {
                res = new LinkedHashSet<>(result);
            } else {
                res.retainAll(result);
            }
        }
        if (res.isEmpty()) {
            return null;
        }

        //log.trace("Result:{}",res.stream().map(i -> i.getFile().getName()).collect(Collectors.toList()));

        // Initialize each resource if it hasn't been initialized yet
        for (T result : res) {
            if (!result.exists() && !result.hasData()) {
                this.update();
                return get(requests);
            }
        }

        // Note: each set retains the order of added items, so ultimately the result (single returned element)
        // might be different depending on "requests" parameters order, since sets are being intersected sequentially.
        //
        // For example, "requests" parameter contains three KeyRequest items.
        // They produce sets [a, b, c], [b, a, c] and [c, b, a]. Intersection will result in
        // [a, b, c] ∩ [b, a, c] ∩ [c, b, a] =  [a, b, c].
        //
        // If first and second params in "requests" will swap their order, the result will be different -
        // [b, a, c] ∩ [a, b, c] ∩ [c, b, a] =  [b, a, c].
        // We use LinkedHasSet, which retains the added elements order!
        // But here only the first element is returned, so in first case it will be [a] and in second one it is [b].
        // Conclusion: "requests" parameters order MATTERS!!!
        return res.iterator().next();
    }
}
