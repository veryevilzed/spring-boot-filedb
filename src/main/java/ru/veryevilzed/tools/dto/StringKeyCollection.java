package ru.veryevilzed.tools.dto;

public class StringKeyCollection<V extends FileEntity> extends SortedComparableKeyCollection<String, V>  {
    public StringKeyCollection(String name) {
        super(name);
    }


    @Override
    public Object parseKey(String key, V file) {
        if (key == null)
            return this.put(null, file);
        else
            return this.put(key, file);
    }
}