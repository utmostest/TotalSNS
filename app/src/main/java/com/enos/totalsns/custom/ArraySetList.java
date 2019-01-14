package com.enos.totalsns.custom;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.function.Predicate;

public class ArraySetList<E> extends ArrayList<E> {

    private HashSet<E> set = new HashSet<>();
    private Comparator<E> comparator = null;

    public ArraySetList() {
        super();
    }

    public ArraySetList(Collection c) {
        super();
        this.addAll(c);
    }

    public ArraySetList(Comparator<E> comparator) {
        super();
        this.comparator = comparator;
    }

    public ArraySetList(Collection<? extends E> c, Comparator<E> comparator) {
        super();
        this.comparator = comparator;
        this.addAll(c);
    }

    public void setComparator(Comparator<E> comparator) {
        this.comparator = comparator;
        Collections.sort(this, this.comparator);
    }

    @Override
    public boolean add(E element) {
        if (set.add(element)) {
            boolean result = super.add(element);
            if (comparator != null) Collections.sort(this, comparator);
            return result;
        }
        return false;
    }

    @Override
    public void add(int index, E element) {
        if (comparator != null) {
            this.add(element);
        } else if (set.add(element)) {
            super.add(index, element);
        }
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends E> c) {
        boolean hasInsert = false;
        ArrayList<E> list = new ArrayList<E>();
        for (E element : c) {
            if (set.add(element)) {
                hasInsert = true;
                list.add(element);
            }
        }
        if (hasInsert) {
            boolean result = super.addAll(list);
            if (comparator != null) Collections.sort(this, comparator);
            return result;
        }
        return false;
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends E> c) {
        boolean hasInsert = false;
        ArrayList<E> list = new ArrayList<>();
        for (E element : c) {
            if (set.add(element)) {
                hasInsert = true;
                list.add(element);
            }
        }
        if (hasInsert) {
            if (comparator != null) {
                this.addAll(c);
            } else return super.addAll(index, list);
        }
        return false;
    }

    @Override
    public E remove(int index) {
        E o = super.remove(index);
        set.remove(o);
        return o;
    }

    @Override
    public boolean remove(@Nullable Object o) {
        if (set.contains(o)) {
            set.remove(o);
            return super.remove(o);
        }
        return false;
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        for (int i = fromIndex; i < toIndex; i++) {
            set.remove(get(i));
        }
        super.removeRange(fromIndex, toIndex);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        set.removeAll(c);
        return super.removeAll(c);
    }

    @Override
    public boolean removeIf(@NonNull Predicate<? super E> filter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            set.removeIf(filter);
            return super.removeIf(filter);
        }
        return false;
    }

    @Override
    public void clear() {
        super.clear();
        set.clear();
    }
}
