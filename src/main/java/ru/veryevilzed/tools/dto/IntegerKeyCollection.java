package ru.veryevilzed.tools.dto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IntegerKeyCollection<V extends FileEntity> extends SortedComparableKeyCollection<Integer, V> {

    public IntegerKeyCollection(String name) {
        super(name);
    }

    public IntegerKeyCollection(String name, Integer defaultKey) {
        super(name, defaultKey);
    }

    @Override
    public Object parseKey(String key, V file) {
        if (key == null)
            return this.put(null, file);
        else
            return this.put(Integer.parseInt(key), file);
    }
}
