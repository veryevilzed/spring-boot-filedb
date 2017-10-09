package ru.veryevilzed.tools.dto;

public class StringKeyCollection extends SortedComparableKeyCollection<String>  {
    public StringKeyCollection(String name) {
        super(name);
    }


    @Override
    public void parseKey(String key, FileEntity file) {
        if (key == null)
            this.put(null, file);
        else
            this.put(key, file);
    }
}