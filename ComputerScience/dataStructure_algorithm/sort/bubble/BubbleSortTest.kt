package com.alex.structure.sort.bubble

import org.alex.util.LogTrack
import kotlin.system.measureNanoTime

/**
 * 作者：Alex
 * 时间：2017/9/20 21:39
 * 简述：11536 ns
 */
fun main(args: Array<String>) {
    val array = intArrayOf(80, 60, 89, 84, 65, 73, 20, 30, 43)
    val time = measureNanoTime {
        bubbleSort(array)
    }
    LogTrack.e(time.toString() + "    " + array.joinToString(","))
}

fun bubbleSort(array: IntArray) {
    var tmp: Int
    for (i in 0..array.size - 1) {
        for (j in 0..array.size - 2 - i) {
            if (array[j] > array[j + 1]) {
                tmp = array[j + 1]
                array[j + 1] = array[j]
                array[j] = tmp
                LogTrack.w(array.joinToString(","))
            }
        }
    }
}
