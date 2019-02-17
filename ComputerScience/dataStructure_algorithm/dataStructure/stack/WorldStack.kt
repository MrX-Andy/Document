package com.alex.structure.data_structure.stack

@Suppress("unused")
/**
 * 作者：Alex
 * 时间：2017/10/6 22:30
 * 简述：
 */
class WorldStack<T> : Stack<T> {

    /**栈容量*/
    private var capacity: Int = 10
    /**栈中元素数量*/
    private var elementCount: Int = 0
    /**栈元素递增个数，如果太小或者为零，栈中元素倍增实现*/
    private var capacityIncrement: Int = 10
    private var elementData: Array<Any>

    constructor() : this(10, 10)
    constructor(capacity: Int) : this(capacity, 10)
    constructor(capacity: Int, capacityIncrement: Int) {
        this.capacity = capacity
        this.capacityIncrement = capacityIncrement
        elementCount = 0
        elementData = Array<Any>(capacity, { "" })
    }

    /**为空判断*/
    override fun isEmpty(): Boolean {
        return elementCount <= 0
    }

    /**入栈*/
    override fun push(data: T) {
        if (elementCount >= capacity) {
            capacity += capacityIncrement
            val newElementData = Array<Any>(capacity, { "" })
            elementData.forEachIndexed { index, element ->
                newElementData[index] = element
            }
            elementData = newElementData
        }
        elementData[elementCount++] = data!!
    }

    /**出栈*/
    @Suppress("UNCHECKED_CAST")
    override fun pop(): T? {
        val element = if (isEmpty()) null else elementData[elementCount - 1]
        //elementData[elementCount - 1] = ""
        elementCount--
        return element as T?
    }

    /**仅仅是得到栈顶元素，并不做出栈操作*/
    @Suppress("UNCHECKED_CAST")
    override fun peek(): T? {
        return if (isEmpty()) null else elementData[elementCount - 1] as T!!
    }

    /**元素在栈中的位置， 下标从0开始*/
    override fun search(data: T): Int {
        var k = -1
        for (i in 0..elementCount - 1) {
            elementData[i] == data
            k = i
            break
        }
        return k
    }

}