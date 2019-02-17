package com.alex.structure.sort.quick

import org.alex.util.LogTrack

/**
 * 作者：Alex
 * 时间：2017/10/4 00:30
 * 简述：
 */
fun main(args: Array<String>) {
    val array = intArrayOf(85, 24, 63, 45, 17, 31, 96, 50)
    quickSort(array, 0, array.size - 1)
    LogTrack.e(array.joinToString(", "))
}

/** 小于等于基准数的，在左边子集； 大于 基准数的， 在右边子集 */
fun quickSort(array: IntArray, left: Int, right: Int) {
    if (left >= right || left < 0 || left >= array.size || right >= array.size) {
        return
    }
    var i = left
    var j = right
    val pivot = array[left]
    while (i < j) {
        while (i < j && array[j] > pivot) {
            j--
        }
        array[i] = array[j]
        while (i < j && array[i] <= pivot) {
            i++
        }
        array[j] = array[i]
    }
    array[i] = pivot
    LogTrack.w(array.joinToString(", "))
    quickSort(array, left, i - 1)
    LogTrack.w(array.joinToString(", "))
    quickSort(array, i + 1, right)
    LogTrack.w(array.joinToString(", "))
}