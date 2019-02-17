package com.alex.structure.data_structure.queue

/**
 * 作者：Alex
 * 时间：2017/10/10 22:10
 * 简述：
 */
interface Queue<D> {
    fun size(): Int

    /**为空判断*/
    fun isEmpty(): Boolean

    /**入队*/
    fun enqueue(data: D)

    /**出队*/
    fun dequeue(): D?

    /**得到队首元素，并不出队*/
    fun peek(): D?

    /**元素在栈中的位置， 下标从0开始*/
    fun search(data: D): Int

    /**清空队列*/
    fun clearQueue()

    fun toList(): List<D>

}

interface ArrQueue<D> : Queue<D> {
    /**出队*/
    fun dequeueThrow(): D?

    fun enqueueThrow(data: D)
    fun isFull(): Boolean

}

interface LinQueue<D> : Queue<D> {
    /**出队*/
    fun dequeueThrow(): D?

}