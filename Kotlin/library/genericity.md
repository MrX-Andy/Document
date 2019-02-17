```
package com.alex.sample

/**
 * 作者：Alex
 * 时间：2017/12/2616:01
 * 简述：
 */


fun main(args: Array<String>) {
    listOf<String>("")

    val inChar: InMutable<CharSequence>? = null
    val inString: InMutable<String>? = null
    val outChar: OutMutable<CharSequence>? = null
    val outString: OutMutable<String>? = null
    val iChar: Mutable<CharSequence>? = null
    val iString: Mutable<String>? = null

    foo(inChar)
    foo(inString)// error

    foo1(outChar)
    foo1(outString)

    foo2(iChar)
    foo2(iString)// error

    foo3(iChar)
    foo3(iString)

    foo4(iChar)
    foo4(iString)// error

}
fun foo(a: InMutable<CharSequence>?) {

}

fun foo1(a: OutMutable<CharSequence>?) {

}

fun foo2(a: Mutable<CharSequence>?) {

}

fun foo3(a: Mutable<out CharSequence>?) {

}

fun foo4(a: Mutable<in CharSequence>?) {

}

interface InMutable<in E> {
    fun foo(e: E)
}

interface OutMutable<out E> {
    fun foo(): E
}

interface Mutable<E> {
    fun foo(e: E)
    fun foo(): E
}

```
// in 逆变  超类  
// out 协变 子类  
// 不变    