package com.alex.structure.data_structure.datamodel;

import java.util.Iterator;

/**
 * 作者：Alex
 * 时间：2017/11/2216:04
 * 简述：
 */
public interface ListWithIteratorInterface<T> extends ListInterface<T> {
    Iterator<T> getIterator();
}