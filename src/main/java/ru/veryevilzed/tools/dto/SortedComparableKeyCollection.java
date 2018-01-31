package ru.veryevilzed.tools.dto;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import lombok.Getter;
import ru.veryevilzed.tools.utils.SortedComparableList;
import ru.veryevilzed.tools.utils.SortedComparableTypes;

public abstract class SortedComparableKeyCollection<T extends Comparable<T>, V> implements KeyCollection<V> {

    private Map<T, LinkedHashSet<V>> map;

    @Getter
    final String name;

    @Getter
    final boolean nullable;

    private SortedComparableList<T> keys;

    private final T defaultKey;

    public Object getDefaultKey() {
        return defaultKey;
    }

    public T put(T key, V value) {
        if (key == null) {
            key = defaultKey;
        }

        if (key == null && !nullable) {
            return null;
        }

        keys.add(key);
        if (!map.containsKey(key)) {
            map.put(key, new LinkedHashSet<>());
        }
        map.get(key).add(value);
        return key;
    }

    @Override
    public String toString() {
        return "[" + name + " " + map +"]";
    }

    @SuppressWarnings("unchecked")
    public LinkedHashSet<V> get(Object key, SortedComparableTypes type) {
        if (type == SortedComparableTypes.EqualTo) {
            if (this.map.containsKey(key)) {
                return this.map.get(key);
            } else {
                return new LinkedHashSet<>();
            }
        }

        LinkedHashSet<V> res = new LinkedHashSet<>();
        for (T _key : keys.getAll((T) key, type)) {
            res.addAll(this.map.get(_key));
        }
        return res;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public void remove(Object key, FileEntity entry) {
        this.map.get(key).remove(entry);
        if (map.get(key).size() == 0) {
            this.remove(key);
        }
    }

    public void remove(Object key) {
        if (keys.contains(key)) {
            keys.remove((Object) key);
        }
        this.map.remove(key);
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
        if (this.defaultKey == null) {
            this.nullable = true;
        } else {
            this.nullable = false;
        }
        map = new HashMap<>();
        keys = new SortedComparableList<>();
    }
}
