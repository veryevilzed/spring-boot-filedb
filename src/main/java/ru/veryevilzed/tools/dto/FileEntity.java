package ru.veryevilzed.tools.dto;

import lombok.Data;

import java.io.File;

@Data
public class FileEntity {
    String path;
    File file;
    long timeModified;

    public FileEntity(File file) {
        this.path = file.getAbsolutePath();
        this.timeModified = file.lastModified();
    }

}
