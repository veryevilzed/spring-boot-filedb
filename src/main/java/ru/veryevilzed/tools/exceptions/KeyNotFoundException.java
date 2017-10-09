package ru.veryevilzed.tools.exceptions;

public class KeyNotFoundException extends Exception {
    public KeyNotFoundException(Object key) {
        super(String.format("Key (%s) not found", key));
    }
}
