package com.alex.structure.data_structure.datamodel

/**
 * 作者：Alex
 * 时间：2017/10/7 20:26
 * 简述：
 */
open class Node<T>(
        var data: T? = null,
        var pre: Node<T>? = null,
        var next: Node<T>? = null
)

open class BinaryTreeNode<T : Comparable<T>>(
        var data: T,
        var leftChild: BinaryTreeNode<T>? = null,
        var rightChild: BinaryTreeNode<T>? = null
)

open class HuffmanNode(
        var data: String = "",
        var weight: Int,
        var leftChild: HuffmanNode? = null,
        var rightChild: HuffmanNode? = null
) {
    override fun toString(): String {
        return "(data=$data, weight=$weight)"
    }
}

