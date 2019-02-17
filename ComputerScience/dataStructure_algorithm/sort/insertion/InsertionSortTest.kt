package com.alex.structure.sort.insertion

import org.alex.util.LogTrack

/**
 * 作者：Alex
 * 时间：2017/9/23 01:04
 * 简述：
 */
fun main(args: Array<String>) {
    val array = intArrayOf(20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10)
    insertionSort(array)
    LogTrack.e(array.joinToString(", "))
}

fun insertionSort(array: IntArray) {
    var pivot: Int
    var j: Int
    for (i in 1..array.size - 1) {
        pivot = array[i];
        j = i;
        while ((j > 0) && (array[j - 1] > pivot)) {
            array[j] = array[--j]
        }
        array[j] = pivot
    }
}

