### 关于Dao的注意事项

> 解决泛型问题
 
假设需要引入泛型限制
- 函数必须加上注解进行修饰
- @Query 注解 最好写成  @Query("select *")
- delete 的返回值 可以是 int  void 

> insert

一般情况下， insert 返回的是long 类型， kotlin并不支持long类型， 因为TA只有Long类型，如果 直接 返回Long 会crash的；
1. 返回 void 也是可以的
2. 入口参数用多个 id， 返回值是 List< Long> ， 也是可以的

```
interface Dao<E> {

    @Query("select *")
    List<E> findEntityList();

    @Query("select *")
    E findEntity(String id);

    @Delete
    int deleteEntity(String id);

    @Delete
    void clearEntity();
    @Insert
    void insertEntity(E entity);

}
```