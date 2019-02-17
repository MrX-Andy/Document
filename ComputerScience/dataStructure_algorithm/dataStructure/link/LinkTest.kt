package com.alex.structure.data_structure.link

import org.alex.extension.logW
import java.util.*

/**
 * 作者：Alex
 * 时间：2017/10/7 20:32
 * 简述：
 */
fun main(args: Array<String>) {
    LinkedList<String>()
    val singleLinkList = SingleLinkList<String>()
    val doubleLinkList = DoubleLinkList<String>()
    val cycleLinkList = CycleLinkList<String>()
    cycleLinkList.add("AA")
    cycleLinkList.add("BB")
    cycleLinkList.add("CC")
    cycleLinkList.add("DD")
    cycleLinkList.add("EE")
    cycleLinkList.remove("DD")
    cycleLinkList.toList().joinToString(", ").logW()
    cycleLinkList.indexOf("CC").logW()
    cycleLinkList.indexOf("EE").logW()
}