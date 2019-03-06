[Collection 系列图](basic/collection/ImageFiles/Collection_001.png)  
[Map系列图](basic/collection/ImageFiles/Collection_002.png)  
[Collection 汇总图](basic/collection/ImageFiles/Collection_003.jpg)  

简单描述  
Map<K, V>  
Java 中存储键值对的数据类型都实现了这个接口, 表示"映射表";  
支持的两个核心操作是 get(Object key) 以及 put(K key, V value);  
分别用来获取键对应的值以及向映射表中插入键值对;  

Set<E>  
无序, 不可重复的集合, 查询效率低, 删除和插入效率高;  

List<E>  
有序, 可以重复的集合, 查询效率高, 删除和插入效率低;  

Queue<E>  
Java集合框架中的队列接口, 代表了"先进先出"队列, 支持add(E element), remove()等操作;

Stack<E>  
Java集合框架中表示堆栈的数据类型, 堆栈是一种"后进先出"的数据结构, 支持 push(E item), pop()等操作;  
### 集合框架的用法比较  
Tree 不允许 key为空;  
Concurrent 涉及 key, key不允许为空; 涉及value, value不允许为空;  

HashMap  
键值对, 一组key, value元素;  
key, value 都可以为空(允许多个不同的key, value为空);  
key相同, value会被覆盖;  
迭代时, 按照key的hashCode顺序;  

Hashtable
键值对, 一组key, value元素;  
key 和 value, 均不可以为空( hashtable.put(null,"AA");     hashtable.put("AA",null);  都会抛异常);    

TreeMap  
键值对, 一组key, value元素;  
只允许value为空(treeMap.put(null, "AA"); 会抛异常);  
迭代时, 按照key的自然语言升序;  

LinkedHashMap  
键值对, 一组key, value元素;  
key, value 都可以为空(允许多个不同的key, value为空;);  
key相同, value会被覆盖;  
迭代时, 可以按照存入时的顺序, 也可以按照LRU算法;  

HashSet
不是键值对, 只有一个元素;  
允许放入 null;  
放入重复元素, 会被覆盖;  
迭代时, 按照hashCode顺序;  

LinkedHashSet  
不是键值对, 只有一个元素;  
允许放入 null;  
放入重复元素, 会被覆盖;  
迭代时, 按照存入的顺序;  

TreeSet  
不是键值对, 只有一个元素;  
不能放入 null(treeSet.add(null); 会抛异常);  
迭代时, 按照自然语言升序;  

ConcurrentHashMap
键值对, 一组key, value元素;  
key 和 value, 均不可以为空( concurrentHashMap.put(null,"AA");     concurrentHashMap.put("AA",null);  都会抛异常);  


### 集合框架源码  
[ArrayList](basic/collection/ArrayList.md)  
[LinkedList](basic/collection/LinkedList.md)  
[HashMap](basic/collection/HashMap.md)  
[LinkedHashMap](basic/collection/LinkedHashMap.md)    
[TreeMap](basic/collection/TreeMap.md)    
LinkedTreeMap  
LinkedHashTreeMap  
[ConcurrentHashMap](basic/collection/ConcurrentHashMap.md)  
ConcurrentSkipListMap  
IdentityHashMap  
WeakHashMap  
WeakClassHashMap  
[LruCache](basic/collection/lrucache.md)  

Hashtable  
IdentityHashtable  

TreeSet  
//  RegularEnumSet  
//  JumboEnumSet  
HashSet  
LinkedHashSet  
CopyOnWriteArrayList  
很适合处理处理，读取频繁，但很少有写操作;  
CopyOnWriteArraySet  
ConcurrentSkipListSet  
BitSet  

Stack  
ArrayDeque  
PriorityQueue  
ConcurrentLinkedDeque  

### 其他数据结构  
参考  
https://stackoverflow.com/questions/2670982/using-pairs-or-2-tuples-in-java   
https://www.javatuples.org  
https://github.com/javatuples/javatuples  
```
// https://mvnrepository.com/artifact/org.eclipse.collections/eclipse-collections
    implementation group: 'org.eclipse.collections', name: 'eclipse-collections', version: '10.0.0.M2'
// https://mvnrepository.com/artifact/org.eclipse.collections/eclipse-collections-api
    implementation group: 'org.eclipse.collections', name: 'eclipse-collections-api', version: '10.0.0.M2'
// https://mvnrepository.com/artifact/org.apache.commons/commons-collections4
    implementation group: 'org.apache.commons', name: 'commons-collections4', version: '4.3'
// https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
    implementation group: 'org.apache.commons', name: 'commons-lang3', version: '3.8.1'
// https://mvnrepository.com/artifact/org.apache.commons/commons-compress
    implementation group: 'org.apache.commons', name: 'commons-compress', version: '1.18'
// https://mvnrepository.com/artifact/org.apache.commons/commons-math3
    implementation group: 'org.apache.commons', name: 'commons-math3', version: '3.6.1'
```
### 参考  
https://m.th7.cn/show/4/201706/1185867.html#  
http://blog.csdn.net/seu_calvin/article/details/52653711  
http://blog.csdn.net/zhangerqing/article/details/8193118  
http://www.cnblogs.com/beatIteWeNerverGiveUp/p/5709841.html  
http://blog.csdn.net/paincupid/article/details/47746341  
https://juejin.im/post/593e5364ac502e006c0c7690  
http://blog.csdn.net/tsyj810883979/article/details/6891540  
http://blog.csdn.net/tsyj810883979/article/details/6892575  
http://blog.csdn.net/tsyj810883979/article/details/6897043  

