package org.alex.ktlibrary

/**
 * 作者：Alex
 * 时间：2017/6/27  11:56
 */
infix fun <P1, IP, R> ((P1) -> IP).andThen(f: (IP) -> R): (P1) -> R = forwardCompose(f)

infix fun <IP, R> (() -> IP).andThen(f: (IP) -> R): () -> R = forwardCompose(f)

infix fun <P1, IP, R> ((P1) -> IP).forwardCompose(f: (IP) -> R): (P1) -> R {
    return { p1: P1 -> f(this(p1)) }
}

infix fun <IP, R> (() -> IP).forwardCompose(f: (IP) -> R): () -> R {
    return { f(this()) }
}

infix fun <IP, R, P1> ((IP) -> R).compose(f: (P1) -> IP): (P1) -> R {
    return { p1: P1 -> this(f(p1)) }
}
