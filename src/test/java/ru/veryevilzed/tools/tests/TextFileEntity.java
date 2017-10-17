package ru.veryevilzed.tools.tests;

import lombok.Data;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import ru.veryevilzed.tools.dto.FileEntity;

import java.io.*;


public class TextFileEntity extends FileEntity {


    String text = null;

    public TextFileEntity(File file) {
        super(file);
    }


    @Override
    public void update() {
        text = null;
    }

    @Override
    public boolean hasData() {
        return this.text != null;
    }

    public String getText() {
        if (text == null) {
            try {
                text = FileUtils.readFileToString(this.getFile(), "UTF-8");
            }catch (IOException ignored) {}
        }
        return text;
    }
}
