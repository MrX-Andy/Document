###### 属性代理
> 属性代理-val
```
class UserBean {
    /*只有 name 属性被读取的时候才会被初始化*/
    val name by lazy {
        "Alex"
    }

    val nickname by UserBeanDelegate()
}

class UserBeanDelegate {
    /*只要 实现了 getValue函数 就能给其他函数作为代理*/
    inline operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return "我是代理出来的 nickName"
    }
}
```

> 属性代理-var
```
class UserBean {
    /*只有 name 属性被读取的时候才会被初始化*/
    val name by lazy {
        "Alex"
    }

    var desc: String by SafeStringDelegate()
}

class SafeStringDelegate {
    var value: String? = null
    /*只要 实现了 getValue函数 就能给其他函数作为代理*/
    inline operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return value ?: ""
    }

    /*只要 实现了 getValue函数 就能给其他函数作为代理*/
    inline operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        this.value = value
    }
}
```
```
fun main(args: Array<String>) {
    val userBean = UserBean()
    userBean.name.logW()
    userBean.desc.logW()
    userBean.desc = "描述什么的"
    userBean.desc.logW()
}
```

