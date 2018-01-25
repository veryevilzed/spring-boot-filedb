package ru.veryevilzed.tools.dto;

import ru.veryevilzed.tools.utils.SortedComparableTypes;

public class KeyRequest {

    private final String name;
    private final Object key;
    private final SortedComparableTypes type;
    private final Object defaultKey;
    private final boolean useDefault;

    public KeyRequest(String name, Object key, SortedComparableTypes type) {
        this.name = name;
        this.key = key;
        this.type = type;
        this.defaultKey = null;
        this.useDefault = false;
    }

    public KeyRequest(String name, Object key, SortedComparableTypes type, Object defaultKey) {
        this.name = name;
        this.key = key;
        this.type = type;
        this.defaultKey = defaultKey;
        this.useDefault = true;
    }

    public KeyRequest getDefaultRequest() {
        return new KeyRequest(name, defaultKey, type);
    }

    public String getName() {
        return name;
    }

    public Object getKey() {
        return key;
    }

    public SortedComparableTypes getType() {
        return type;
    }

    public Object getDefaultKey() {
        return defaultKey;
    }

    public boolean isUseDefault() {
        return useDefault;
    }
}
