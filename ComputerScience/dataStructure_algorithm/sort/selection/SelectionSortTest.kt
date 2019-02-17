package com.alex.structure.sort.selection

import org.alex.util.LogTrack

/**
 * 作者：Alex
 * 时间：2017/9/23 00:37
 * 简述：
 */

fun main(args: Array<String>) {
    val array = intArrayOf(20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10)
    selectionSort(array)
    LogTrack.e(array.joinToString(", "))
}

@Suppress("LoopToCallChain")
fun selectionSort(array: IntArray) {
    for (i in 0..array.size - 1) {
        var x = i
        for (j in i..array.size - 1) {
            if (array[x] > array[j]) {
                x = j
            }
        }
        val tmp = array[x]
        array[x] = array[i]
        array[i] = tmp
    }
}


