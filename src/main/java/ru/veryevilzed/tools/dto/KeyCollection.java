package ru.veryevilzed.tools.dto;

import ru.veryevilzed.tools.utils.SortedComparableTypes;

import java.util.Set;

public interface KeyCollection {
    String getName();

    Object getDefaultKey();

    boolean isNullable();

    void remove(Object key, FileEntity entry);

    Set<FileEntity> remove(Object key);

    Object parseKey(String key, FileEntity file);
    Set<FileEntity> get(Object key, SortedComparableTypes type);

}
