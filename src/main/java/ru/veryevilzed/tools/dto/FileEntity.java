package ru.veryevilzed.tools.dto;

import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Data
public class FileEntity {

    final String path;
    final File file;
    long lastModified;

    final Map<KeyCollection, Object> keys;

    public boolean isModified() { return file.lastModified() != lastModified; }

    public boolean exists() { return file.exists(); }

    public void addKey(KeyCollection keyName, Object keyValue) {
        this.keys.put(keyName, keyValue);
    }

    public void update() {
        if (exists())
            lastModified = file.lastModified();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(path).build();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FileEntity){
            FileEntity e = (FileEntity)obj;
            return new EqualsBuilder().append(path, e.path).build();
        }else
            return false;
    }



    public FileEntity(File file) {
        this.file = file;
        path = file.getAbsolutePath();
        lastModified = file.lastModified();
        keys = new HashMap<>();
    }

}
