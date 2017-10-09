package ru.veryevilzed.tools.dto;

public class LongKeyCollection extends SortedComparableKeyCollection<Long>  {
    public LongKeyCollection(String name) {
        super(name);
    }

    public LongKeyCollection(String name, Long defaultKey) {
        super(name, defaultKey);
    }

    @Override
    public Object parseKey(String key, FileEntity file) {
        if (key == null)
            return this.put(null, file);
        else
            return this.put(Long.parseLong(key), file);
    }
}
