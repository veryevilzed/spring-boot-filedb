package ru.veryevilzed.tools.exceptions;

public class DirectoryNotExists extends RuntimeException {

    public DirectoryNotExists(String path) {
        super(String.format("Directory %s not found", path));
    }
}
