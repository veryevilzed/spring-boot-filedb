package ru.veryevilzed.tools.dto;

import java.util.LinkedHashSet;

import ru.veryevilzed.tools.utils.SortedComparableTypes;

public interface KeyCollection<V> {

    String getName();

    Object getDefaultKey();

    boolean isNullable();

    void remove(Object key);
    void remove(Object key, FileEntity entry);

    Object parseKey(String key, V file);
    LinkedHashSet<V> get(Object key, SortedComparableTypes type);
}
