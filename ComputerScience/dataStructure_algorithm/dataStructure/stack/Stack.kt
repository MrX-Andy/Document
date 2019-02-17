package com.alex.structure.data_structure.stack

/**
 * 作者：Alex
 * 时间：2017/10/6 22:26
 * 简述：
 */
interface Stack<D> {
    /**为空判断*/
    fun isEmpty(): Boolean

    /**入栈*/
    fun push(data: D)

    /**出栈*/
    fun pop(): D?

    /**仅仅是得到栈顶元素，并不做出栈操作*/
    fun peek(): D?

    /**元素在栈中的位置， 下标从0开始*/
    fun search(data: D): Int

}