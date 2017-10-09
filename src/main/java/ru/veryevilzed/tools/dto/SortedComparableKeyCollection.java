package ru.veryevilzed.tools.dto;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.veryevilzed.tools.exceptions.KeyNotFoundException;
import ru.veryevilzed.tools.utils.SortedComparableList;
import ru.veryevilzed.tools.utils.SortedComparableTypes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
public abstract class SortedComparableKeyCollection<T extends Comparable<T>> implements KeyCollection {


    private Map<T, Set<FileEntity>> map;

    @Getter
    final String name;

    @Getter
    final boolean nullable;

    private SortedComparableList<T> keys;

    private final T defaultKey;

    public Object getDefaultKey() { return defaultKey; }

    public T put(T key, FileEntity value) {
        if (key == null)
            key = defaultKey;

        if (key == null && !nullable)
            return null;

        keys.add(key);
        if (!map.containsKey(key))
            map.put(key, new HashSet<>());
        map.get(key).add(value);
        return key;
    }



    public Set<FileEntity> get(Object key, SortedComparableTypes type) {
        T obj = (T)key;
        return this.get(obj, type);
    }

    private Set<FileEntity> get(T key, SortedComparableTypes type) {
        if ((type == SortedComparableTypes.Equals || type == SortedComparableTypes.GreaterThanEqual || type == SortedComparableTypes.LessThanEqual) && this.map.containsKey(key))
            return this.map.get(key);

        T _key;
        try {
            _key = keys.get(key, type);
        }catch (KeyNotFoundException e){
            return null;
        }

        Set<FileEntity> res = this.map.get(_key);
        if (res == null)
            return new HashSet<>();
        return res;
    }


    public void remove(Object key, FileEntity entry) {
        this.map.get(key).remove(entry);
        if (map.get(key).size() == 0) {
            map.remove(key);
            if (keys.contains(key))
                keys.remove(key);
        }
    }


    public Set<FileEntity> remove(Object key) {
        if (keys.contains(key))
            keys.remove(key);
        return this.map.remove(key);
    }


    public SortedComparableKeyCollection(String name) {
        this.name = name;
        this.defaultKey = null;
        this.nullable = false;
        map = new HashMap<>();
        keys = new SortedComparableList<>();
    }

    public SortedComparableKeyCollection(String name, T defaultKey) {
        this.name = name;
        this.defaultKey = defaultKey;
        if (this.defaultKey == null)
            this.nullable = true;
        else
            this.nullable = false;
        map = new HashMap<>();
        keys = new SortedComparableList<>();
    }


}
