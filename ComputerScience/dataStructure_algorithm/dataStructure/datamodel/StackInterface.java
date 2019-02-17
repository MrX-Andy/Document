package com.alex.structure.data_structure.datamodel;

/**
 * 作者：Alex
 * 时间：2017/11/2215:52
 * 简述：
 */
public interface StackInterface<T> {
    /**
     * Adds a new entry to the top of this stack.
     *
     * @param newEntry an object to be added to the stack
     */
    void push(T newEntry);

    /**
     * Removes and returns this stack’s top entry.
     *
     * @return either the object at the top of the stack or, if the
     * stack is empty before the operation, null
     */
    T pop();

    /**
     * Retrieves this stack’s top entry.
     *
     * @return either the object at the top of the stack or null if
     * the stack is empty
     */
    T peek();

    /**
     * Detects whether this stack is empty.
     *
     * @return true if the stack is empty
     */
    boolean isEmpty();

    /**
     * Removes all entries from this stack
     */
    void clear();
} // end StackInterface
