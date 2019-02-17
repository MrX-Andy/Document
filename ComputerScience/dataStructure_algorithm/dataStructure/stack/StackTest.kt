package com.alex.structure.data_structure.stack

import org.alex.util.LogTrack

/**
 * 作者：Alex
 * 时间：2017/10/7 16:03
 * 简述：
 */
fun main(args: Array<String>) {

    val stack = WorldStack<String>()
    val array = arrayOf("A", "B", "C", "D", "E", "A1", "B1", "C1", "D1", "E1", "A2", "B2", "C2", "D2", "E2")
    array.forEach {
        stack.push(it)
    }
    LogTrack.w(stack.isEmpty())
    LogTrack.w(stack.peek())
    LogTrack.w(stack.pop())
}