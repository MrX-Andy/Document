package com.alex.structure.data_structure.queue

import org.alex.extension.logW
import org.alex.extension.tryNull

/**
 * 作者：Alex
 * 时间：2017/10/1316:21
 * 简述：
 */
fun main(args: Array<String>) {
    val queue = LinkQueue<String>()
    queue.enqueue("AA")
    queue.enqueue("BB")
    queue.toList().joinToString(", ").logW()
    queue.size().logW()
    ;{ queue.dequeueThrow()?.logW() }.tryNull { it.logW() }
    ;{ queue.dequeueThrow()?.logW() }.tryNull { it.logW() }
    ;{ queue.dequeueThrow()?.logW() }.tryNull { it.logW() }
    ;{ queue.dequeueThrow()?.logW() }.tryNull { it.logW() }
    queue.enqueue("CC")
    queue.enqueue("DD")
    queue.enqueue("EE")
    ;{ queue.dequeueThrow()?.logW() }.tryNull { it.logW() }
    ;{ queue.dequeueThrow()?.logW() }.tryNull { it.logW() }
    ;{ queue.dequeueThrow()?.logW() }.tryNull { it.logW() }

}