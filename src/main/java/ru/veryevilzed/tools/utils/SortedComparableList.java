package ru.veryevilzed.tools.utils;

import ru.veryevilzed.tools.exceptions.KeyNotFoundException;

import java.util.ArrayList;
import java.util.Comparator;

public class SortedComparableList<V extends Comparable<V>> extends ArrayList<V> {





    @Override
    public boolean contains(Object o) {
        V obj;
        try {
            obj = ((V) o);
        }catch (ClassCastException ignored){
            return false;
        }

        return this.stream().filter(i -> i == null ? i == obj : i.compareTo(obj) == 0).findFirst().orElseGet(null) != null;
    }

    public V get(V key, SortedComparableTypes type, V def) {
        try{
            return get(key, type);
        }catch (KeyNotFoundException ignore) {
            return def;
        }
    }

    public V get(V key, SortedComparableTypes type) throws KeyNotFoundException {
        V res;
        switch (type){
            default:
            case Equals:
                if (key == null)
                    res = this.stream().filter(i -> i == key).findFirst().orElse(null);
                else
                    res = this.stream().filter(key::equals).findFirst().orElse(null);
                break;
            case LessThan:
                res = this.stream().filter(i -> i.compareTo(key) < 0).sorted(Comparator.reverseOrder()).findFirst().orElse(null);
                break;
            case LessThanEqual:
                res = this.stream().filter(i -> i.compareTo(key) <= 0).sorted(Comparator.reverseOrder()).findFirst().orElse(null);
                break;
            case GreaterThan:
                res = this.stream().filter(i -> i.compareTo(key) > 0).sorted(Comparable::compareTo).findFirst().orElse(null);
                break;
            case GreaterThanEqual:
                res = this.stream().filter(i -> i.compareTo(key) >= 0).sorted(Comparable::compareTo).findFirst().orElse(null);
                break;
        }

        if (res == null)
            throw new KeyNotFoundException(key);
        return res;
    }


    public SortedComparableList() {
        super();
    }
}
