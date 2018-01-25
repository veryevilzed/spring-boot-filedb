package ru.veryevilzed.tools.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Сет сохранающий порядок
 */
public class OrderedSet<T> extends ArrayList<T> implements Set<T>, List<T> {

    public OrderedSet() {
        super();
    }

    public OrderedSet(Collection<T> collection) {
        super();
        this.addAll(collection);
    }

    @Override
    public void add(int index, T element) {
        if (!this.contains(element)) {
            super.add(index, element);
        }
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean res = false;
        for (T t : c) {
            if (!res && !this.contains(t)) {
                res = true;
            }
            this.add(t);
        }
        return res;
    }

    @Override
    public boolean add(T t) {
        if (!this.contains(t)) {
            return super.add(t);
        }
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        boolean res = false;
        for (T t : c) {
            if (!res && !this.contains(t)) {
                res = true;
            }
            this.add(index, t);
        }
        return res;
    }

    @Override
    public T set(int index, T element) {
        if (!this.contains(element)) {
            return super.set(index, element);
        }
        return element;
    }
}
