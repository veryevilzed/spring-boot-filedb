package ru.veryevilzed.tools.dto;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@ToString(exclude = {"keys","file"})
public abstract class FileEntity {

    @Getter
    final String path;

    @Getter
    final File file;

    @Getter
    long lastModified;

    @Getter
    final Map<KeyCollection, Object> keys;

    /**
     * Фаил изменился
     * @return
     */
    public boolean isModified() {
        return file.lastModified() != lastModified;
    }


    /**
     * Фаил существует
     * @return
     */
    public boolean exists() { return file.exists(); }

    /**
     * Метод для перекрытия и аказания что есть DATA в контейнере
     */
    public boolean hasData() { return false; }

    /**
     * Добавить ключ к даному инстансу
     * @param keyName ключ
     * @param keyValue значение
     */
    public void addKey(KeyCollection keyName, Object keyValue) {
        this.keys.put(keyName, keyValue);
    }

    /**
     * Проверить на обновление
     */
    public void checkForUpdate() {

        if (exists() && isModified()) {
            try{
                update();
            }catch (Exception e){
                log.error("Error update file {}:{}", path, e.getMessage());
            }finally {
                lastModified = file.lastModified();

            }

        }
    }

    /**
     * абстрактный метод обновления данных
     */
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
        lastModified = -1;
        keys = new HashMap<>();
    }

}
