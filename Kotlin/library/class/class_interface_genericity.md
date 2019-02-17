### 接口_泛型 和 中间类_泛型
> 概念
```
型变：
分为  变和不变 
变 分为，协变 和逆变

协变 与 逆变：
用子类代替父类，就是协变；
用父类代替子类，就是逆变；

不变：
声明什么类型，就用什么类型
```
> Wrapper
```
interface Wrapper<T> {
    var code: String?
    var message: String?
    var data: T?
}
```

> WrapperBean
```
class WrapperBean<T> : Wrapper<T> {
    override var data: T? = null
    override var code: String? = ""
        get() {
            return if (field == null) "" else field;
        }
        set(value) {
            field = value;
        }
    override var message: String? = ""
        get() {
            return if (field == null) "" else field;
        }
        set(value) {
            field = value;
        }
}
```
