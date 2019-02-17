###### 内部类 与 匿名内部类
> 内部类
```
class UserBean(val name: String, var age: String = "18") {

    /** 静态内部类*/
    class Inner0 {
    /**
     * 可以直接 访问 外部类 的属性 以及 函数
     */
    }

    /**非静态内部类*/
    inner class Inner1 {
    /**
     * 可以直接 访问 外部类 的属性 以及 函数
     */

    }
}
```
> 匿名内部类

```
.subscribe(object : LiteObserver<String>() {
    override fun onNext(result: String?) {
        result?.logW()
    }
})
```