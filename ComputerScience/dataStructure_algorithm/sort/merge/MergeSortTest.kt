package com.alex.structure.sort.merge

import org.alex.util.ArrayUtil
import org.alex.util.LogTrack

/**
 * 作者：Alex
 * 时间：2017/10/4 23:59
 * 简述：
 */
fun main(args: Array<String>) {
    val array = intArrayOf(8, 4, 9, 5, 7, 1, 3, 6, 2)
    mergeSort(array)
    LogTrack.e(array.joinToString(", "))
}

fun mergeSort(array: IntArray) {
    val arraySize = array.size
    val tmpArray = ArrayUtil.intArrayOf(arraySize, -1)
    var leftMinIndex: Int
    var leftMaxIndex: Int
    var rightMinIndex: Int
    var rightMaxIndex: Int
    var tmpIndex: Int
    var i: Int
    var j: Int
    var span = 1
    while (span < arraySize) {
        tmpIndex = 0
        leftMinIndex = 0
        while (leftMinIndex + span < arraySize) {
            leftMaxIndex = leftMinIndex + span - 1
            rightMinIndex = leftMinIndex + span
            rightMaxIndex = if (rightMinIndex + span - 1 <= arraySize - 1) rightMinIndex + span - 1 else arraySize - 1
            i = leftMinIndex
            j = rightMinIndex
            while (i <= leftMaxIndex && j <= rightMaxIndex) {
                tmpArray[tmpIndex++] = if (array[i] <= array[j]) array[i++] else array[j++]
            }
            while (i <= leftMaxIndex) {
                tmpArray[tmpIndex++] = array[i++]
            }
            while (j <= rightMaxIndex) {
                tmpArray[tmpIndex++] = array[j++]
            }
            leftMinIndex = rightMaxIndex + 1
            LogTrack.w(span.toString() + "    " + leftMinIndex + "    " + tmpArray.joinToString(", "))
        }
        for (m in leftMinIndex..arraySize - 1) {
            tmpArray[tmpIndex++] = array[m]
        }
        for (m in 0..arraySize - 1) {
            array[m] = tmpArray[m]
        }
        span *= 2
    }
}
