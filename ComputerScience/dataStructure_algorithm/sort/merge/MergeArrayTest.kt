package com.alex.structure.sort.merge

import org.alex.util.ArrayUtil
import org.alex.util.LogTrack

/**
 * 作者：Alex
 * 时间：2017/10/5 21:01
 * 简述：
 */
fun main(args: Array<String>) {
    val array0 = intArrayOf(5, 9, 10, 11, 20)
    val array1 = intArrayOf(4, 7, 21)
    val array = ArrayUtil.intArrayOf(array0.size + array1.size, -1)
    mergeArray(array0, array1, array)
    LogTrack.e(array.joinToString(", "))

}

fun mergeArray(array0: IntArray, array1: IntArray, array: IntArray) {
    val array0Size = array0.size
    val array1Size = array1.size
    var array0Index = 0
    var array1Index = 0
    var arrayIndex = 0
    while (array0Index < array0Size && array1Index < array1Size) {
        array[arrayIndex++] = if (array0[array0Index] < array1[array1Index]) array0[array0Index++] else array1[array1Index++]
    }
    while (array0Index < array0Size) {
        array[arrayIndex++] = array0[array0Index++]
    }
    while (array1Index < array1Size) {
        array[arrayIndex++] = array1[array1Index++]
    }
}
