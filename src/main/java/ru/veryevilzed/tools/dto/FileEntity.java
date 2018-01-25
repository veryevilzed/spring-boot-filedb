package ru.veryevilzed.tools.dto;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public abstract class FileEntity<T> {

    protected final File file;

    protected long lastModified = -1;

    protected final Map<KeyCollection, Object> keys = new HashMap<>();

    protected T data;

    public FileEntity(File file) {
        this.file = file;
    }

    /**
     * Фаил изменился
     */
    public boolean isModified() {
        return file.lastModified() != lastModified;
    }

    /**
     * Контейнер с данными заполнен
     */
    public boolean exists() {
        return hasData() || file.exists();
    }

    /**
     * Метод для аказания что есть DATA в контейнере
     */
    public boolean hasData() {
        return data != null;
    }

    /**
     * Добавить ключ к даному инстансу
     *
     * @param keyName  ключ
     * @param keyValue значение
     */
    public void addKey(KeyCollection keyName, Object keyValue) {
        keys.put(keyName, keyValue);
    }

    /**
     * Проверить на обновление
     */
    public void checkForUpdate() {

        if (exists() && isModified()) {
            invalidate();
            lastModified = file.lastModified();
        }
    }

    /**
     * абстрактный метод обновления данных
     */
    public void invalidate() {
        data = null;
    }

    @Override
    public int hashCode() {
        return file.getAbsolutePath().hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if ((obj instanceof FileEntity)) {
            FileEntity fileEntity = (FileEntity) obj;
            return file != null
                   && fileEntity.getFile() != null
                   && file.getAbsolutePath().equals(fileEntity.getFile().getAbsolutePath());
        }
        return false;
    }

    @Override
    public String toString() {
        return "FileEntity(path=" + file.getPath() + ", lastModified=" + lastModified + ")";
    }

    public String getPath() {
        return file.getAbsolutePath();
    }

    public File getFile() {
        return file;
    }

    public long getLastModified() {
        return lastModified;
    }

    public Map<KeyCollection, Object> getKeys() {
        return keys;
    }
}
