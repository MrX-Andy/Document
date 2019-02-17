package com.alex.structure.data_structure.queue

import org.alex.extension.logW
import org.alex.extension.tryDefault
import org.alex.extension.tryNull
import org.alex.extension.tryOnly
import org.alex.util.LogTrack

/**
 * 作者：Alex
 * 时间：2017/10/9 17:57
 * 简述：
 */
fun main(args: Array<String>) {
    val queue = CircleArrayQueue<String>(5)
    queue.enqueue("AA")
    queue.enqueue("BB")
    queue.enqueue("CC")
    queue.enqueue("DD")
    ;{ queue.enqueueThrow("EE") }.tryNull { it.logW() }
    ;{ queue.enqueueThrow("FF") }.tryNull { it.logW() }
    ;{ queue.enqueueThrow("GG") }.tryNull { it.logW() }
    queue.toList().joinToString(", ").logW()

    queue.dequeue()?.logW()
    queue.dequeue()?.logW()
    queue.dequeue()?.logW()
    queue.dequeue()?.logW()
    queue.dequeue()?.logW()
    queue.dequeue()?.logW()
    queue.dequeue()?.logW()
    queue.dequeue()?.logW()
    ;{ queue.dequeueThrow()?.logW() }.tryNull { it.logW() }
    ;{ queue.dequeueThrow()?.logW() }.tryNull { it.logW() }
    ;{ queue.dequeueThrow()?.logW() }.tryNull { it.logW() }
    ;{ queue.enqueueThrow("11") }.tryNull { it.logW() }
    ;{ queue.enqueueThrow("22") }.tryNull { it.logW() }
    queue.dequeue()?.logW()
    ;{ queue.enqueueThrow("33") }.tryNull { it.logW() }
    ;{ queue.enqueueThrow("44") }.tryNull { it.logW() }
    queue.dequeue()?.logW()
    queue.dequeue()?.logW()
    ;{ queue.enqueueThrow("55") }.tryNull { it.logW() }
    ;{ queue.enqueueThrow("66") }.tryNull { it.logW() }
    ;{ queue.enqueueThrow("77") }.tryNull { it.logW() }
    ;{ queue.enqueueThrow("88") }.tryNull { it.logW() }
    queue.toList().joinToString(", ").logW()
}
