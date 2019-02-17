###### 类定义

> 接口定义

```
public interface DBEntity extends Serializable{

}
```

> data class 定义

data class 必须要一个 参数？ 怎么搞出来一个 不需要参数的 data class ？

``` 
@Entity(tableName = DBConfig.t_user.tableName)
data class UserDBEntity constructor(
        var id: Int = 0,
        var username: String = "",
        var phone: String = "110120130140",
        var userAge: String = "10"
) : DBEntity {
    constructor() : this(username = "")
}
```

> [构造函数与init代码块问题](class_constructor_init.md)

> [自定义getter和setter](class_getter_setter.md)

> [单例怎么写](class_singleton.md)



