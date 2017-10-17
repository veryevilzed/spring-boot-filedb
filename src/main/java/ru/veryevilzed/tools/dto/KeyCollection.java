package ru.veryevilzed.tools.dto;

import ru.veryevilzed.tools.utils.OrderedSet;
import ru.veryevilzed.tools.utils.SortedComparableTypes;

import java.util.List;
import java.util.Set;

public interface KeyCollection<V> {
    String getName();

    Object getDefaultKey();

    boolean isNullable();

    void remove(Object key);
    void remove(Object key, FileEntity entry);

    Object parseKey(String key, V file);
    OrderedSet<V> get(Object key, SortedComparableTypes type);

}
