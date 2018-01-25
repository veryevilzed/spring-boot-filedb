package ru.veryevilzed.tools.tests;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import ru.veryevilzed.tools.dto.FileEntity;


public class TextFileEntity extends FileEntity<String> {

    public TextFileEntity(File file) {
        super(file);
    }

    public String getText() {
        if (data == null) {
            try {
                data = FileUtils.readFileToString(this.getFile(), "UTF-8");
            } catch (IOException ignored) { /**/ }
        }
        return data;
    }
}
