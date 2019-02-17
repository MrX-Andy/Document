package com.alex.structure.sort.shell

import org.alex.util.LogTrack

/**
 * 作者：Alex
 * 时间：2017/9/23 20:10
 * 简述：
 */

fun main(args: Array<String>) {
    val array = intArrayOf(20, 19, 18, 17, 16, 15, 14, 13, 12, 11)
    shellSort(array)
    LogTrack.e(array.joinToString(",  "))
}

fun shellSort(array: IntArray) {
    var gap = array.size / 2
    var tmp: Int
    while (gap >= 1) {
        for (i in 0..gap - 1) {
            for (j in i..array.size - 1 step gap) {
                for (k in i..array.size - 1 step gap) {
                    if (k + gap <= array.size - 1 && array[k + gap] < array[k]) {
                        tmp = array[k + gap]
                        array[k + gap] = array[k]
                        array[k] = tmp
                    }
                }
            }
        }
        gap /= 2
    }
}
