package com.alex.structure.data_structure.queue

import com.alex.structure.data_structure.datamodel.Node
import org.alex.util.LogTrack
import java.util.ArrayList

/**
 * 作者：Alex
 * 时间：2017/10/12 22:18
 * 简述：
 */
class ArrayQueue<T> : ArrQueue<T> {

    /**队列容量*/
    private var capacity: Int = 10
    /**队列中元素数量*/
    private var elementCount: Int = 0
    /**队列元素递增个数，如果太小或者为零，队列中元素倍增实现*/
    private var capacityIncrement: Int = 10
    /**队首*/
    private var front = 0
    /**队尾*/
    private var rear = 0
    private var elementData: Array<Any>

    constructor() : this(10, 10)
    constructor(capacity: Int) : this(capacity, 10)
    constructor(capacity: Int, capacityIncrement: Int) {
        this.capacity = capacity
        this.capacityIncrement = capacityIncrement
        elementCount = 0
        elementData = Array(capacity, {})
    }

    override fun size(): Int {
        return elementCount
    }

    /**为空判断*/
    override fun isEmpty(): Boolean {
        return elementCount == 0
    }

    /**入队*/
    override fun enqueue(data: T) {
        if (data == null) return
        if (isFull()) {
            //LogTrack.e("队满 "+elementCount)
            return
        }
        elementCount++
        elementData[rear++] = data
    }


    /**入队， 队满会抛异常*/
    override fun enqueueThrow(data: T) {
        if (data == null) return
        if (isFull()) {
            //LogTrack.e("队满 "+elementCount)
            throw Exception("queue is full")
        }
        elementCount++
        elementData[rear++] = data
    }

    override fun isFull(): Boolean {
        var isFull = false
        if (rear > capacity - 1) {
            isFull = true
        }
        return isFull
    }

    @Suppress("UNCHECKED_CAST")
    /**出队*/
    override fun dequeue(): T? {
        if (isEmpty()) {
//            throw Exception("queue is empty")
            return null
        }
        elementCount--
        return elementData[front++] as T
    }

    @Suppress("UNCHECKED_CAST")
    override fun dequeueThrow(): T? {
        if (isEmpty()) {
            throw Exception("queue is empty")
            //return null
        }
        elementCount--
        return elementData[front++] as T
    }

    /**得到队首元素，并不出队*/
    @Suppress("UNCHECKED_CAST")
    override fun peek(): T? {
        return elementData[front] as T
    }

    /**元素在栈中的位置， 下标从0开始*/
    override fun search(data: T): Int {
        var position = -1
        elementData.forEachIndexed { index, any ->
            if (any == data) {
                position = index
            }
        }
        return position
    }

    /**清空队列*/
    override fun clearQueue() {
        elementCount = 0
        elementData = Array(capacity, {})
        front = 0
        rear = 0
    }

    @Suppress("UNCHECKED_CAST")
    override fun toList(): List<T> {
        val list: ArrayList<T> = ArrayList<T>()
        for (i in front..rear - 1) {
            list.add(elementData[i] as T)
        }
        return list
    }
}