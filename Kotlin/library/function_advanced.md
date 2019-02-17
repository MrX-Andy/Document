### 高阶函数
高阶函数，是指把函数作为参数 或者 返回值 的一种概念，

> 实例-1
```
fun main(args: Array<String>) {
    val nameArray = arrayListOf("A", "B", "C", "D", "E", " ", "")
    val pdfPrinter = PdfPrinter()
    nameArray
            .filter({ it == "A" })
            .filter(String::isNotBlank)
            .filter(String::isNotEmpty)
            .forEach(pdfPrinter::print)
}

class PdfPrinter{
    fun print(any: Any){
        any.logW()
    }
}
```
> 实例-2
```
fun main(args: Array<String>) {
    val nameArray = arrayListOf("A", "B", "C", "D", "E", " ", "")
    val newNameArray = nameArray
            .filter({ (it != "") and it.isNotBlank() and it.isNotEmpty() })
            .map({ "新的" + it })
.also { it.logW() }
            .flatMap<String, TmpBean> { arrayListOf(TmpBean(it)) }
            .logW()
}

class TmpBean(var message: String = "") {
    override fun toString(): String {
        return "TmpBean(message=$message)"
    }
}

fun main(args: Array<String>) {
    val nameArray = arrayListOf("A", "B", "C", "D", "E", " ", "")
    /**  reduce 做求和操作*/
    val newNameArray = nameArray
            .filter({ (it != "") and it.isNotBlank() and it.isNotEmpty() })
            .reduce { acc, index ->  acc+index}
            .logW()
}
```
> 实例-3
```
let之内不用做非空判断了
fun main(args: Array<String>) {
    var list = arrayListOf<String>("AA", "BB", "CC", "DD", "EE")
            .flatMap<String, MessageBean>({ mutableListOf<MessageBean>(MessageBean(it)) }) as MutableList?
    list = null
    list?.let {
        it.forEach(Consumer {
            it.logW()
        })
    }
}
```

> 函数闭包
```
fun main(args: Array<String>) {

    val makeFun = makeFun()
    makeFun()
    makeFun()
    makeFun()
    makeFun()
    makeFun()
}


fun makeFun():()->Unit{
    var count =0
    return fun(){
        count++.logW()
    }
}

说明 这个函数在内存中 一直是存在的， 并没有被释放， 除非 调用这个函数的 函数  或者 对象被释放
```
> 函数复合
- 实例-1
```
fun main(args: Array<String>) {
    multi2(add5(3)).logW()
    add5Multi2(3).logW()
    multi2Add5(3).logW()
}

val add5 = { i: Int -> i + 5 }
val multi2 = { i: Int -> i * 2 }
val add5Multi2 = add5 addThen multi2
val multi2Add5 = multi2 addThen add5

/**
 * multi2(add5(3)).logW()
 * P1 是 add5 的入口参数的泛型， P2 是 add5 出口参数的泛型
 * P2 是 multi2 的入口参数的泛型，R 是 multi2 的出口参数的泛型
 *
 * addThen 的意义就是，
 * foo1调用 andThen函数，但是andThen函数的入口参数是一个foo2类型的函数，
 * foo1 调用 andThen函数得到的返回值类型是一个函数 foo3，
 * foo1 调用 andThen函数之后， 整个表达式就变成 函数 foo3，foo3的入口参数类型是P1，返回值类型是R
 *
 */
infix fun <P1, P2, R> Function1<P1, P2>.addThen(function: Function1<P2, R>): Function1<P1, R> {
    return fun(p1: P1): R {
        return function.invoke(this.invoke(p1))
    }
}

那么问题来了，既然我明明知道这3个函数的入口参数和出口参数的类型都是Int， 泛型都用R表示，可以吗？必须可以的
infix fun <R> Function1<R,R>.addThen(function: Function1<R, R>): Function1<R, R> {
    return fun(p1: R): R {
        return function.invoke(this.invoke(p1))
    }
}
```
- 实例-2
```
fun main(args: Array<String>) {
    /**
     * 计算
     * (1+2)*3 = 9
     * (1*2)+3 = 5
     * */
    addMulti(1, 2, 3).logW()
    multiAdd(1, 2, 3).logW()
}

val add = { num0: Int, num1: Int -> num0 + num1 }
val multi = { num0: Int, num1: Int -> num0 * num1 }
val addMulti = add infixFoo multi
val multiAdd = multi infixFoo add

infix fun <P0, P1, P2, P3, R> Function2<P0, P1, P2>.infixFoo(function: Function2<P2, P3, R>): Function3<P0, P1, P3, R> {
    return fun(p0: P0, p1: P1, p3: P3): R {
        return function.invoke(this.invoke(p0, p1), p3)
    }
}
```


> 函数链式调用
```
fun main(args: Array<String>) {
    /**
     * 必须要传 3 组参数
     * */
    log("我在打印日志")
    log("我在打印日志")("日志前缀：")
    log("我在打印日志")("日志前缀：")(false)
}

fun log(text: String)
        = fun(tag: String)
        = fun(isTrack: Boolean) {
    (tag + text).logW(isTrack)
}
```

> 偏函数
```
fun main(args: Array<String>) {
    getText("30").logW()
    val partial1 = ::getCullText.partial1("18")
    partial1("Alex").logW()
}

val getText = ::getDullText.partial0_2("Alex", "110")

fun getCullText(name: String, age: String): String {
    return "name=$name,  age=$age"
}

fun getDullText(name: String, age: String, phone: String): String {
    return "name=$name,  age=$age,  phone=$phone"
}
fun <P0, P1, R> Function2<P0, P1, R>.partial0(p0: P0)
        = fun(p1: P1)
        = this(p0, p1)

fun <P0, P1, R> Function2<P0, P1, R>.partial1(p1: P1)
        = fun(p0: P0)
        = this(p0, p1)

fun <P0, P1, P2, R> Function3<P0, P1, P2, R>.partial0_2(p0: P0, p2: P2)
        = fun(p1: P1)
        = this(p0, p1, p2)

fun <P0, P1, P2, R> Function3<P0, P1, P2, R>.partial0_1(p0: P0, p1: P1)
        = fun(p2: P2)
        = this(p0, p1, p2)

```