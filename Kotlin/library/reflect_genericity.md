###### 泛型与反射

> 已知具体class，反射获取 value，修改 value
```
val messageBean = MessageBean("HAHA")
messageBean.message = "消息。。"
/**
 * 假设  MessageBean 有个 var 的属性
 * var message: String? = null
 * */
val messageProperty = MessageBean<String>::message
messageProperty.get(messageBean)
if(messageProperty is KMutableProperty<*>){
    messageProperty.set(messageBean, "新的消息")
}
```
> 执行函数 - public
```
val messageBean = MessageBean("HAHA")
val function = MessageBean<String>::getFormatMessage
/**
 * 执行 getFormatMessage 函数
 * */
function.invoke(messageBean).logW()
```
> 执行函数 - private
```
val messageBean = MessageBean<String>("HAHA")
val messageBeanClass = MessageBean::class
val getFormatMessageFun = messageBeanClass.memberFunctions.find {
    kFunction ->
    kFunction.name == "getFormatMessage"
}
getFormatMessageFun?.javaMethod?.isAccessible = true
getFormatMessageFun?.javaMethod?.invoke(messageBean)?.logW()
```
> 得到一个对象 - 通过构造
- 假设 类 长这样的 （没有 默认 无参构造）
```
class MessageBean<T>(val t: T) : BaseTextBean(), Wrapper<ItemContentBean> {

}

val clazz = t::class
clazz.constructors.forEach {
    it.javaConstructor?.newInstance("hello")?.logE()
}
```

- 假设 类 长 这样的 （有 默认 无参构造）
```
class ItemContentBean {

}
val clazz = ItemContentBean::class
val newT = clazz.createInstance()
```


函数列表
```
/**
 * 迭代出 本类 和 超类 中 所有的 方法， 包括 private ，但 不包括  getter setter
 */
clazz.declaredFunctions.forEach {
    it.logE()
}
/**
 * 迭代出 本类 中 所有的 方法， 包括 private ，但 不包括  getter setter
 */
clazz.declaredMemberFunctions.forEach {
    it.logE()
}
/**
 * 返回在这个类中声明的所有函数，包括在类中声明的所有非静态方法，以及在类中声明的静态方法。
 * */
clazz.functions.forEach {
    it.logE()
}
/**
 * 返回在该类和所有超类中声明的非扩展非静态函数。
 */
clazz.memberFunctions.forEach {
    it.logE()
}
```
> 属性列表
```
/**
 * 返回该类中声明的非扩展属性。
 */
clazz.declaredMemberProperties.forEach {
    it.logE()
}
/**
 * 返回在这个类中声明的所有函数和属性。不包括在超级类型中声明的成员。
 */
clazz.declaredMembers.forEach {
    it.logE()
}
/**
 * 返回该类中声明的非扩展属性及其所有超类。
 */
clazz.memberProperties.forEach {
    it.logE()
}
```

> 继承列表
```
/**
 * 超类 和 接口
 * */
clazz.superclasses.forEach {
    it.logE()
}
clazz.supertypes.forEach {
    it.logE()
}
```

> 函数的参数名
```
val f = ::foo
f.parameters.forEach {
    it.name?.logE()
    it.type.logE()
}
```

