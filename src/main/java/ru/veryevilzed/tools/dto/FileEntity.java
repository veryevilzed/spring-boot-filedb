package ru.veryevilzed.tools.dto;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public abstract class FileEntity {

    @Getter
    final String path;

    @Getter
    final File file;

    @Getter
    long lastModified;

    @Getter
    final Map<KeyCollection, Object> keys;

    public boolean isModified() {
        return file.lastModified() != lastModified;
    }

    public boolean exists() { return file.exists(); }

    public void addKey(KeyCollection keyName, Object keyValue) {
        this.keys.put(keyName, keyValue);
    }

    public void checkForUpdate() {

        if (exists() && isModified()) {
            try{
                log.debug("Update file:{}", this.path);
                update();
            }catch (Exception e){
                log.error("Error update file {}:{}", path, e.getMessage());
            }finally {
                lastModified = file.lastModified();

            }

        }
    }

    protected abstract void update();

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
        lastModified = -1; //file.lastModified();
        keys = new HashMap<>();
    }

}
