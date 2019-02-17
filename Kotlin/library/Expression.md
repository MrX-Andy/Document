### 习惯 与 表达式
> 初始化一个数组

```
val arrayOf = intArrayOf(44, 33, 40, 22, 10, 82, 10, 9, 8, 10, 20)
```

> for 循环

```
/* 左右都是 闭区间 */
for (i in 0 .. array.size){
    LogTrack.w(array[i])
}
```

```
/*注意一下， 下面两个 k， 并不同一个东西*/
val k = 9  /* k0 */
for (k in 0..10) {  /* k1 */
    LogTrack.e(k)
}
```

```
var count = 9
for (k in 0..count) {
    if (k == 4) {
        /*虽然 count 被修改了， 但是for循环用的判断条件还是count的初始值 9*/
        count = 3
    }
    LogTrack.e(k)
}
```

> && 、 || 、 and 、 or

- 首先我们假设4个函数
```
private fun foo1() = true
private fun foo2() = return true
private fun foo3() = return false
private fun foo4() = return false
```

- foo2() || foo3()
- foo2() && foo3()

```
事实上这个表达式很容易理解，   把这个 表达式 当成 if 表达式就可以了， 
不执行  true || 之后的语句；
不执行  true && 之后的语句；

要执行  false || 之后的语句；
不执行  false && 之后的语句；
```

> """  """ 表达式

""" 在这里，不用转义字符的"""

> ? 表达式

?:""

javaBean?.foo()  如果javaBean为空， foo方法不会被执行

