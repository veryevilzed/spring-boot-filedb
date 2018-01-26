package ru.veryevilzed.tools.dto;

import lombok.Data;
import ru.veryevilzed.tools.utils.SortedComparableTypes;

@Data
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
}
