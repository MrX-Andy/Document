package com.alex.structure.data_structure.queue

import org.alex.extension.logW

/**
 * 作者：Alex
 * 时间：2017/10/9 17:57
 * 简述：
 */
fun main(args: Array<String>) {
    val queue = ArrayQueue<String>()
    queue.enqueue("AA")
    queue.enqueue("BB")
    queue.enqueue("CC")
    queue.peek()?.logW()
}