### ObjectBox   
[入门](library/start.md)  
假设有对应关系  
配置对应关系  
```

一对一的对应关系， OrderId 和 UserId 就是一对一
@Relation
Customer customer;
这里，  @Relation 不能加在 非包装类上， 也就是说， 必须给实体类加

一对多的对应关系， OrderId 和 ProductId 就是一对多
@Relation(idProperty = "customerId")
List<Order> orders;

```
Build - Make Project  
如果，你做完对应关系，不再一次 Build，是不行的，之后会自动生成代码。  

### APi  
```
Box
/**
 * 有多少条记录
 */
public long count();
BoxStore
close
/**
 * 只要关闭了， 一般的正常操作 就废了
 */
close();
deleteAllFiles
/**
 * 删除 所有数据， 但是 删除前， 必须先关闭 boxStore.close();
 */
deleteAllFiles();
find();
/**
 * 找到对应的记录数据
 */
public List<T> find(Property property, long value);
此时此刻，我最关心property是啥??  value 是啥??
List<OrderBean> findOrderBeanList = orderBeanBox.find(OrderBean_.__ID_PROPERTY, 1);
List<OrderBean> findOrderBeanList = orderBeanBox.find(OrderBean_.id, 1);
这 OrderBean_.__ID_PROPERTY 和 OrderBean_.id是 自动生成的，不需要关心 和 修改


/**
 * 生成一个数据库操作对象 Box 
 */
public <T> Box<T> boxFor(Class<T> entityClass);
getAll
/**
 * 将游标移动到 第一条，并迭代出所有的 记录
 */
public List<T> getAll(); 
getAll2
/**
 * 从当前位置开始，迭代出所有的 记录
 */
public List<T> getAll2();
put
/**
 * 如果是新的记录， 此处等于插入操作，javabean的id，传0，就可以了； 
 *     如果 注解中 已经标记使用后端给的 id， 就使用吧
 * 如果不是新的记录，此处等于更新操作 
 */
public long put(T entity);
```
```
插入
BoxStore boxStore = App.getApp().getBoxStore(); Box<OrderListBean> box = boxStore.boxFor(OrderListBean.class); List<OrderListBean> listBeen4Insert = new ArrayList<>(); for (int i = 0; i < 5; i++) {     OrderListBean bean = new OrderListBean();     bean.setCreateTime(System.currentTimeMillis() + "");     bean.setCustomer("Alex " + i);     listBeen4Insert.add(bean); } box.put(listBeen4Insert);
查询
QueryBuilder<OrderListBean> queryBuilder = box.query(); Query<OrderListBean> query = queryBuilder.build(); List<OrderListBean> orderListBeen = query.find(); for (int i = 0; i < orderListBeen.size(); i++) {     OrderListBean orderListBean = orderListBeen.get(i);     LogTrack.e(orderListBean); }
分页查询
List<OrderBean> queryOrderBeenList = orderBeanBox.query().build().find(1, 2);
for (int i = 0; queryOrderBeenList != null && i < queryOrderBeenList.size(); i++) {
	OrderBean orderBean = queryOrderBeenList.get(i);
	LogTrack.e(orderBean);
}
更新
就是先找到，在改变， 在put
OrderBean orderBean = findOrderBeanList.get(i);
orderBean.setCreateTime("'2018-1223-1029");
orderBeanBox.put(orderBean);
```
```
关于注解
@Entity 
加在 类 上的注解
@Id
有以下需要注意的点：
0 代表对象还没被持久化，一个新的需要被持久化的对象的 Id 应为 0
如果需要使用服务器端已经存在的 Id，需要这样标记@Id(assignable = true)，这样就不会检查插入对象时对象的 Id
@Property
需要指定特殊的存储在数据库中时的名称，使用注解
@Transient
不需要持久化该属性
@Index
可以生成索引，加快查询速度。

@Relation
使用 @Relation 注解可以标注关联关系。
```
```


一对一

customId 属性会自动生成。
 
@Entity
public class Order {
    @Id long id;
    long customerId;
    @Relation
    Customer customer;
}
@Entity
public class Customer {
    @Id long id;
}
一对多

一对多的时候，只能修饰 List。
 
@Entity
public class Customer {
    @Id long id;
    // References the ID property in the *Order* entity
    @Relation(idProperty = "customerId")
    List<Order> orders;
}
@Entity
public class Order {
    @Id long id;
    long customerId;
    @Relation Customer customer;
}
```
```

错误
错误 1
Error:Execution failed for task ':app:objectbox'.
> Can't replace method in D:\WorkSpace\Studuo4Alex\ObjectBox\app\src\main\java\com\alex\objectbox\model\UserBean.java:75 with generated version.
                    If you would like to keep it, it should be explicitly marked with @Keep annotation.
                    Otherwise please mark it with @Generated annotation

错误：任务'：app：objectbox'的执行失败。
>无法替换D：\ WorkSpace \ Studuo4Alex \ ObjectBox \ app \ src \ main \ java \ com \ alex \ objectbox \ model \ UserBean.java：75中生成的版本。
                     如果你想保留它，它应该明确标记@Keep注释。
                     否则，请使用@Generated注释标记
```

### 参考  
http://greenrobot.org/objectbox/documentation/data-observers-reactive-extensions/  
http://greenrobot.org/objectbox/documentation/how-to-get-started/  
http://greenrobot.org/objectbox/documentation/entity-annotations/  
https://loshine.me/2017/02/07/android-db-object-box/  
https://github.com/greenrobot/ObjectBoxExamples  
https://loshine.me/2017/02/07/android-db-object-box/?utm_source=tuicool&utm_medium=referral  
