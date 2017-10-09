package ru.veryevilzed.tools.dto;

import ru.veryevilzed.tools.utils.SortedComparableTypes;

import java.util.Set;

public interface KeyCollection<V> {
    String getName();

    Object getDefaultKey();

    boolean isNullable();

    void remove(Object key, V entry);

    Set<V> remove(Object key);

    Object parseKey(String key, V file);
    Set<V> get(Object key, SortedComparableTypes type);

}
