package ru.veryevilzed.tools.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ru.veryevilzed.tools.exceptions.KeyNotFoundException;

public class SortedComparableList<V extends Comparable<V>> extends ArrayList<V> {

    @Override
    public boolean contains(Object o) {
        V obj;
        try {
            obj = ((V) o);
        } catch (ClassCastException ignored) {
            return false;
        }

        return this.stream().filter(i -> i == null ? i == obj : i.compareTo(obj) == 0).findFirst().orElseGet(null) != null;
    }

    public List<V> getAll(V key, SortedComparableTypes type, V def) {
        List<V> res = getAll(key, type);
        if (res.isEmpty()) {
            return Collections.singletonList(def);
        }
        return res;
    }

    public List<V> getAll(V key, SortedComparableTypes type) {
        List<V> res = new ArrayList<>();

        switch (type) {
            default:
            case EqualTo:
                if (key == null) {
                    res.add(this.stream().filter(Objects::isNull).findFirst().orElse(null));
                } else {
                    res.add(this.stream().filter(key::equals).findFirst().orElse(null));
                }
                break;
            case LessThan:
                res.addAll(this.stream().filter(i -> i.compareTo(key) < 0).sorted(Comparator.reverseOrder()).collect(Collectors.toList()));
                break;
            case LessThanOrEqualTo:
                res.addAll(this.stream().filter(i -> i.compareTo(key) <= 0).sorted(Comparator.reverseOrder()).collect(Collectors.toList()));
                break;
            case GreaterThan:
                res.addAll(this.stream().filter(i -> i.compareTo(key) > 0).sorted(Comparable::compareTo).collect(Collectors.toList()));
                break;
            case GreaterThanOrEqualTo:
                res.addAll(this.stream().filter(i -> i.compareTo(key) >= 0).sorted(Comparable::compareTo).collect(Collectors.toList()));
                break;
        }
        return res;
    }

    public V get(V key, SortedComparableTypes type, V def) {
        try {
            return get(key, type);
        } catch (KeyNotFoundException ignore) {
            return def;
        }
    }

    public V get(V key, SortedComparableTypes type) throws KeyNotFoundException {
        V res;
        switch (type) {
            default:
            case EqualTo:
                if (key == null) {
                    res = this.stream().filter(Objects::isNull).findFirst().orElse(null);
                } else {
                    res = this.stream().filter(key::equals).findFirst().orElse(null);
                }
                break;
            case LessThan:
                res = this.stream().filter(i -> i.compareTo(key) < 0).sorted(Comparator.reverseOrder()).findFirst().orElse(null);
                break;
            case LessThanOrEqualTo:
                res = this.stream().filter(i -> i.compareTo(key) <= 0).sorted(Comparator.reverseOrder()).findFirst().orElse(null);
                break;
            case GreaterThan:
                res = this.stream().filter(i -> i.compareTo(key) > 0).sorted(Comparable::compareTo).findFirst().orElse(null);
                break;
            case GreaterThanOrEqualTo:
                res = this.stream().filter(i -> i.compareTo(key) >= 0).sorted(Comparable::compareTo).findFirst().orElse(null);
                break;
        }

        if (res == null) {
            throw new KeyNotFoundException(key);
        }
        return res;
    }

    @Override
    public boolean add(V v) {
        if (!super.contains(v)) {
            return super.add(v);
        }
        return false;
    }

    @Override
    public V remove(int index) {
        throw new RuntimeException("WARNING!!!!");
    }

    public SortedComparableList() {
        super();
    }
}
