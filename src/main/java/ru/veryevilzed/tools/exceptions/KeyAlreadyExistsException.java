package ru.veryevilzed.tools.exceptions;

public class KeyAlreadyExistsException extends RuntimeException {
    public KeyAlreadyExistsException(Object key) {
        super(String.format("Key %s already exists", key.toString()));
    }
}
