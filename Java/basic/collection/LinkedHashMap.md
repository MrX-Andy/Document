LinkedHashMap 是 HashMap 的子类;  
LinkedHashMap 同样不是 线程安全的;  
HashMap 的节点是 Node, 有 hash 数值, key, value,next 节点;  
LinkedHashMap 的节点是, Entry, 继承与 Node, 多了 before 和 after 节点;  
LinkedHashMap 维护了一个双向链表, 并持有 head 和 tail节点;  

LinkedHashMap 重写了 newNode, 用来创建节点, 节点是 Entry 类型, 是 Node 节点的子类, 每次新加节点, 都会放在 tail 之后;  
accessOrder 为 true , forEach 的输出, 按照访问顺序;  
accessOrder 为 false, forEach 的输出, 按照插入顺序;  

如果是 accessOrder = true, 在进行 get 操作时, 根据 hash 值, 算出在 hash 数组中的 index, 再得到一个 node 节点,  
然后会回调 LinkedHashMap 的 afterNodeAccess 方法, 把当前节点, 从当前位置扣下来, 放在 tail 之后;  


### accessOrder  
```
LinkedHashMap<String, String> linkedHashMap =   new LinkedHashMap<>(16, 0.75F, true);
linkedHashMap.put("1", "A");
linkedHashMap.put("2", "B");
linkedHashMap.put("3", "C");
linkedHashMap.put("4", "D");
linkedHashMap.put("5", "E");

linkedHashMap.get("3");
linkedHashMap.get("1");
linkedHashMap.get("2");

linkedHashMap.forEach((key, value) -> {
    LogTrack.w(key + "=" + value);
});
```
accessOrder = true  输出:  
```
#lambda 4=D
#lambda 5=E
#lambda 3=C
#lambda 1=A
#lambda 2=B
```
accessOrder = false  输出:  
```
#lambda 1=A
#lambda 2=B
#lambda 3=C
#lambda 4=D
#lambda 5=E
```

我们看一下put操作  
1.. LinkedHashMap 只能调用父类 HashMap 的 public V put(K key, V value);  
2.. final V putVal(int hash, K key, V value, boolean onlyIfAbsent, boolean evict);   
putValue 是 final 类型的方法, 无法重写;  
但是预留 afterNodeAccess 和 afterNodeInsertion 等空实现的方法;  
新插入的节点, 放在双向链表的 tail 处;  

 
### 参考  
http://blog.csdn.net/ns_code/article/details/37867985  


