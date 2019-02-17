package com.alex.structure.data_structure.link

import com.alex.structure.data_structure.datamodel.Node
import java.util.*

/**
 * 作者：Alex
 * 时间：2017/10/7 20:30
 * 简述：
 */
class CycleLinkList<T> {
    private var size: Int = 0
    private var first: Node<T> = Node<T>(null, null, null)
    private var last: Node<T> = Node<T>(null, null, null)

    init {
        first.next = last
        first.pre = last
        last.pre = first
        last.next = first
    }

    fun add(e: T): Boolean {
        linkBefore(e)
        return true
    }

    private fun linkBefore(e: T) {
        size++
        val newNode = Node<T>(e, null, null)
        last.pre?.next = newNode
        newNode.pre = last.pre
        newNode.next = last
        last.pre = newNode
    }

    fun remove(e: T) {
        var node: Node<T>? = first
        while (e != node?.next?.data) {
            node = node?.next
        }
        if (e == node?.next?.data) {
            size--
            var targetNode = node?.next
            node?.next = targetNode?.next
            targetNode?.next?.pre = node
            targetNode = null
        }
    }

    fun indexOf(e: T): Int {
        var index = 0
        var node: Node<T>? = first.next ?: return -1
        while (e != node?.data) {
            index++
            node = node?.next
        }
        if (e == node?.data) {
            return index
        }
        return -1
    }

    fun toList(): List<T> {
        val list: ArrayList<T> = ArrayList<T>()
        var node: Node<T>? = first.next ?: return list
        while (node?.next != null && node.data != null) {
            list.add(node.data!!)
            node = node.next
        }
        return list
    }
}