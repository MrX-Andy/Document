package com.alex.structure.data_structure.datamodel;

import java.util.Iterator;

/**
 * 作者：Alex
 * 时间：2017/11/2216:08
 * 简述：
 */
@SuppressWarnings("unchecked")
public class LinkedListWithIterator<T> implements
        ListWithIteratorInterface<T> {
    @Override
    public Iterator<T> getIterator() {
        return null;
    }

    @Override
    public void add(T newEntry) {

    }

    @Override
    public boolean add(int newPosition, T newEntry) {
        return false;
    }

    @Override
    public T remove(int givenPosition) {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean replace(int givenPosition, T newEntry) {
        return false;
    }

    @Override
    public T getEntry(int givenPosition) {
        return null;
    }

    @Override
    public boolean contains(T anEntry) {
        return false;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public T[] toArray() {
        return (T[]) new Object[0];
    }
}
