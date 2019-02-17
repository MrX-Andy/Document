#### kotlin 构造函数重载知识

◆ kotlin 代码  
```
class ProviderTableEntity
@JvmOverloads constructor(
        /**match for content provider UriMatcher*/
        val math: Int = UriMatcher.NO_MATCH,
        /**表名字*/
        val tableName: String = "") {
}
```
◆ java代码  
ProviderTableEntity messageTableEntity = new ProviderTableEntity(102, "t_message");  
ProviderTableEntity noneTableEntity = new ProviderTableEntity();    
