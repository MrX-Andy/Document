package com.alex.structure.data_structure.queue

import com.alex.structure.data_structure.datamodel.Node
import org.alex.util.LogTrack
import java.lang.reflect.Executable

/**
 * 作者：Alex
 * 时间：2017/10/12 22:18
 * 简述：
 */
class LinkQueue<T> : LinQueue<T> {


    private var frontNode = Node<T>(null, null, null)
    private var rearNode = Node<T>(null, null, null)
    /**队列中元素数量*/
    private var elementCount: Int = 0

    constructor() {
        frontNode.next = rearNode
        rearNode.pre = frontNode

    }

    override fun size() = elementCount

    override fun isEmpty() = (elementCount == 0)

    override fun enqueue(data: T) {
        val newNode = Node(data, null, null)
        rearNode.pre?.next = newNode
        newNode.pre = rearNode.pre
        newNode.next = rearNode
        rearNode.pre = newNode
        elementCount++
    }

    override fun dequeue(): T? {
        if (isEmpty()) {
            return null
        }
        val data = frontNode.next?.data
        frontNode = frontNode.next!!
        elementCount--
        return data
    }

    override fun dequeueThrow(): T? {
        if (isEmpty()) {
            throw Exception("queue is empty")
        }
        val data = frontNode.next?.data
        frontNode = frontNode.next!!
        elementCount--
        return data
    }

    override fun peek(): T? = frontNode.data

    override fun search(data: T): Int {
        var node = frontNode
        var index = -1
        while (node.next != null) {
            index++
            if (node.data == data) {
                return index
            }
            node = node.next!!
        }
        return -1
    }

    override fun clearQueue() {
    }

    @Suppress("UNCHECKED_CAST", "SENSELESS_COMPARISON")
    override fun toList(): List<T> {
        var node = frontNode
        val list = ArrayList<T>()
        while (node.next != null) {
            val data = node.data
            if (data != null) {
                list.add(data as T)
            }
            node = node.next!!
        }
        return list
    }

}