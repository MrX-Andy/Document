package com.alex.structure.search.binarysearch

import org.alex.util.LogTrack

/**
 * 作者：Alex
 * 时间：2017/9/20 23:28
 * 简述：
 */
fun main(args: Array<String>) {
    val intArrayOf = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 12, 13, 14, 15, 16, 17)
//    intArrayOf.joinToString(",")
    LogTrack.e(intArrayOf.joinToString(", "))
    val key = binarySearch(9, intArrayOf)
    LogTrack.e("key = " + key)
}

fun binarySearch(key: Int, array: IntArray): Int {
    var errorIndex: Int = -1
    if (array.size == 1) {
        return if (key == array[0]) 0 else errorIndex
    }
    var minIndex: Int = 0
    var maxIndex = array.size - 1
    while (minIndex <= maxIndex) {
        errorIndex = ((minIndex + maxIndex) * 0.5F).toInt()
        if (array[errorIndex] == key) {
            return errorIndex
        }
        if (array[errorIndex] < key) {
            minIndex = errorIndex + 1
        }
        if (array[errorIndex] > key) {
            maxIndex = errorIndex - 1
        }
    }
    return errorIndex
}
