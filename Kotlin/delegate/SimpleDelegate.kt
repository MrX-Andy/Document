package org.alex.delegate

import kotlin.reflect.KProperty

/**
 * 作者：Alex
 * 时间：2017/6/24 22:00
 * 简述：
 */
class StringDelegate {
    var value: String? = null
    /*只要 实现了 getValue函数 就能给其他函数作为代理*/
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String = value ?: ""

    /*只要 实现了 getValue函数 就能给其他函数作为代理*/
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        this.value = value
    }
}

