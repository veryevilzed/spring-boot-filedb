package ru.veryevilzed.tools.dto;

import lombok.Data;
import ru.veryevilzed.tools.utils.SortedComparableTypes;

@Data
public class KeyRequest {

    final String name;
    final Object key;
    final SortedComparableTypes type;
    final Object defaultKey;
    final boolean useDefault;

    public KeyRequest(String name, Object key, SortedComparableTypes type) {
        this.name = name;
        this.key = key;
        this.type = type;
        this.defaultKey = null;
        useDefault = false;
    }

    public KeyRequest(String name, Object key, SortedComparableTypes type, Object defaultKey) {
        this.name = name;
        this.key = key;
        this.type = type;
        this.defaultKey = defaultKey;
        useDefault = true;
    }

    public KeyRequest getDefaultRequest() {
        return new KeyRequest(name, defaultKey, type);
    }
}
