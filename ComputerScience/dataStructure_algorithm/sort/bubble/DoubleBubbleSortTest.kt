package com.alex.structure.sort.bubble

import org.alex.util.LogTrack
import kotlin.system.measureNanoTime

/**
 * 作者：Alex
 * 时间：2017/10/5 21:12
 * 简述：3845 ns
 */
fun main(args: Array<String>) {
    val array = intArrayOf(80, 60, 89, 84, 65, 73, 20, 30, 43)
    doubleBubbleSort(array)
    val time = measureNanoTime {
        doubleBubbleSort(array)
    }
    LogTrack.e(time.toString() + "    " + array.joinToString(","))
}

fun doubleBubbleSort(array: IntArray) {
    var i = 0
    var left = 0
    var right = array.size - 1
    var tmp: Int
    while (i <= right) {
        for (j in left..right - 1) {
            if (array[j] > array[j + 1]) {
                tmp = array[j + 1]
                array[j + 1] = array[j]
                array[j] = tmp
                LogTrack.w("→  " + array.joinToString(","))
            }
        }
        right--
        for (j in right - 1 downTo left + 1) {
            if (array[j] < array[j - 1]) {
                tmp = array[j]
                array[j] = array[j - 1]
                array[j - 1] = tmp
                LogTrack.w("←  " + array.joinToString(","))
            }
        }
        left++
        i++
    }

}
