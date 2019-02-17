#### 函数与参数

> 可变参数
```
fun <T> asList(vararg array:T):List<T>{
    var result = ArrayList<T>()
    for(t in array){
        result.add(t)
    }
    return result;
}
```
```
override fun insertUser(vararg entity: UserDBEntity) {
    dao.insertEntity(*entity)
}

fun insertEntity(vararg entity: UserDBEntity): List<Long>
```

> 简化函数
```
fun max(left: Int, right: Int): Int {
    return Math.max(left, right);
}

fun min(left: Int, right: Int) = Math.min(left, right);
```
> 泛型函数
```
fun main(args: Array<String>) {
 val gson = GsonBuilder().create()
    val (_, userAge) = gson.fromJson<UserBean>("""
            ｜ {
            ｜"userName":"Alex",
            ｜"userAge":25
            ｜}""".trimMargin("｜"))
    print( "_" +userAge)
}
inline fun <reified T: Any> Gson.fromJson(json:String): T = this.fromJson(json, T::class.java)
```
> 扩展函数、中缀函数
```
声明....
infix fun Int.add(x: Int): Int {  //中缀函数
    return this + x;
}

fun Int.sub(x: Int): Int {  //扩展函数
    return this + x;
}
使用...
"${1 add 2 }".logW()
"${1.add(2) }".logW()
"${1.sub(3)}".logW()
```
> 函数类型
```
/*
*  函数类型
* 相当于java中
* name 是一个匿名内部类的对象
* 这个匿名内部类 是一个 函数，函数的名字是Name
* 最后一条语句是函数的返回值
* */
val name: (Int) -> Int = {
    it.toString().length;
    it.toString().length;
}
val kk = fun (value: String): Int {
    return value.length
}
val hj = fun (x : Int) = x * 10
val lam = {
    x: Int, y: Int ->
    x + y
    x + y
}


测试
fun main(args: Array<String>) {
    lam(2,3).logW()
    lam.invoke(2,3).logW()
}
```


> 函数组合
```
/**
 * compose函数
 * 入口参数是两个函数
 *     f 函数 实现从 A 类型 到B 类型转换
 *     g函数 实现从 B类型 到C  类型转换
 * 返回值 是一个函数
 *     入口参数是A类型， 出口参数是C类型
 */
fun <A, B, C> compose(f: (A) -> B, g: (B) -> C): (A) -> C = { g(f(it)) }
测试
fun main(args: Array<String>) {

    compose(int2String, string2Double).invoke(1).logW()
    compose<Int, String, Double>(int2String, string2Double).invoke(2).logW()
    
}

val int2String: (Int) -> String = {
    it.toString()
}

val string2Double: (String) -> Double = {
    it.toDouble()
}
LogTrack  warn  com.alex.smaple [ (Zest.kt:13)#main ] 1.0
LogTrack  warn  com.alex.smaple [ (Zest.kt:14)#main ] 2.0
```
> 命名参数
```
fun reformat(vararg str: String,
             name: Int,
             normalizeCase: Boolean = true,
             upperCaseFirstLetter: Boolean = true,
             divideByCamelHumps: Boolean = false,
             wordSeparator: Char = ' ') {
}
测试
reformat("A", "A2", name = 1, divideByCamelHumps = true)
```

> 内联函数
```
inline fun foo()
函数的引入可以减少程序的目标代码，实现程序代码的共享。
函数调用需要时间和空间开销，调用函数实际上将程序执行流程转移到被调函数中，被调函数的代码执行完后，再返回到调用的地方。这种调用操作要求调用前保护好现场并记忆执行的地址，返回后恢复现场，并按原来保存的地址继续执行。对于较长的函数这种开销可以忽略不计，但对于一些函数体代码很短，又被频繁调用的函数，就不能忽视这种开销。引入内联函数正是为了解决这个问题，提高程序的运行效率。
在程序编译时，编译器将程序中出现的内联函数的调用表达式用内联函数的函数体来进行替换。由于在编译时将内联函数体中的代码替代到程序中，因此会增加目标程序代码量，进而增加空间开销，而在时间开销上不象函数调用时那么大，可见它是以目标代码的增加为代价来换取时间的节省。

inline函数是提高运行时间效率，但却增加了空间开销。
即inline函数目的是：为了提高函数的执行效率(速度)。
非内联函数调用有栈内存创建和释放的开销
在C中可以用宏代码提高执行效率，宏代码不是函数但使用起来像函数，编译器用复制宏代码的方式取代函数调用，省去了参数压栈、生成汇编语言的CALL调用、返回参数、执行return等过程，从而提高速度。
```