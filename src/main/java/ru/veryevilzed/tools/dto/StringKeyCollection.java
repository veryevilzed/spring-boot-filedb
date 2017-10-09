package ru.veryevilzed.tools.dto;

public class StringKeyCollection extends SortedComparableKeyCollection<String>  {
    public StringKeyCollection(String name) {
        super(name);
    }


    @Override
    public Object parseKey(String key, FileEntity file) {
        if (key == null)
            return this.put(null, file);
        else
            return this.put(key, file);
    }
}