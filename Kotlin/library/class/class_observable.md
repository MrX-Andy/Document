###### 可观察属性

```
class UserBean {
    var obName: String by Delegates.observable("默认 value") {
        property: KProperty<*>, oldValue: String, newValue: String ->
        ("$property  $oldValue -> $newValue").logW()
    }

}
测试
fun main(args: Array<String>) {

    var bean = UserBean();
    
    bean.obName = "AAA"
    bean.obName = "BBB"
}
```