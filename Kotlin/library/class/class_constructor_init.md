###### 构造函数 与 init代码块

> 构造函数

```
open class Node<T>(var data: T) {
    var next: Node<T>? = null
    var pre: Node<T>? = null
}
class LinkListNode(data: String) : Node<String>(data) {
    
}
```

```
class UserBean(name: String) {
    /**
     * UserBean(name: String) 这个就是 主构造函数；
     * init{} 初始化 代码块 一定是在 柱构造函数 之后执行的；
     * constructor(name: String, cool: Boolean) : this(name)  这个是 此构造函数， 一定是在 init{} 代码块 之后 执行的
     * */
    var age: Int = 0

    var name: String = ""

    constructor(name: String, cool: Boolean) : this(name) {
        "$name, $cool".logW()
    }

    init {
        name.logW()
    }
}
```
```
class UserBean2() {
    /**
     * UserBean2() 这个就是 主构造函数；
     * init{} 初始化 代码块 一定是在 柱构造函数 之后执行的；
     * constructor(name: String, cool: Boolean) : this()  这个是 此构造函数， 一定是在 init{} 代码块 之后 执行的
     * */
    var age: Int = 0

    var name: String = ""

    constructor(name: String, cool: Boolean) : this() {
        "$name, $cool".logW()
    }

    init {
        name.logW()
    }
}
```

> 构造函数 相互调用

```
constructor(capacity: Int) : this(capacity, 10)
constructor(capacity: Int, capacityIncrement: Int) {
    this.capacity = capacity
    this.capacityIncrement = capacityIncrement
}
```
