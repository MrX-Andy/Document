### 实体类的注意事项

关于 kotlin 的 data class  
 
- data class 必须 提供 无参的构造函数
- 构造函数 需要加上 @ignore 注解
- 如果某个Field需要忽略， 也是使用@Ignore注解标识
- @PrimaryKey(autoGenerate = true)  只能修饰 Int 类型 的 数据


```
@Entity(tableName = DBConfig.t_user.tableName)
data class UserDBEntity @Ignore constructor(
        @ColumnInfo(name = "id")
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,

        @ColumnInfo(name = "user_name")
        var username: String = "",

        @ColumnInfo(name = "phone")
        var phone: String = "110120130140",

        @ColumnInfo(name = "user_age")
        var userAge: String = "10"
) : DBEntity {
    constructor() : this(username = "")
}
```

一对一，一对多  
放弃你的想法吧， 这个需要自己实现

