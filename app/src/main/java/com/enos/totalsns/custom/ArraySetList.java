package com.enos.totalsns.custom;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Predicate;

public class ArraySetList<E> extends ArrayList<E> {

    private HashSet<Object> set = new HashSet<>();

    public ArraySetList(Collection c) {
        super();
        addAll(c);
    }

    public ArraySetList() {
        super();
    }


    @Override
    public boolean add(E element) {
        if (set.add(element)) {
            return super.add(element);
        }
        return false;
    }

    @Override
    public void add(int index, E element) {
        if (set.add(element)) {
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
            return super.addAll(list);
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
            return super.addAll(index, list);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean removeIf(@NonNull Predicate<? super E> filter) {
        return super.removeIf(filter);
    }

    @Override
    public void clear() {
        super.clear();
        set.clear();
    }
}
