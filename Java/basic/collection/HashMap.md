简单描述  
HashMap 基于 数组, 链表, 红黑树;
HashMap 同时允许 key, value 都可以为空;
HashMap 不是 线程安全的, 可以用synchronize 实现线程安全, 在高并发下, 应该用 ConcurrentHashMap, 建议不要再使用 Hashtable了;
HashMap 的默认容量是16, 

HashMap 的实现  
HashMap的实现使用了数组, 链表, 红黑树;  
数组的每个元素, 称之为箱子, 箱子里存放桶;  
桶的数据结构有 2 种可能: 链表, 红黑树;  
HashMap使用key的hashCode来寻找存储位置;  
不同的key可能具有相同的hashCode, 这时候就出现哈希冲突了, 也叫做哈希碰撞, 为了解决哈希冲突,   
常见办法有开放地址方法, 链地址方法, HashMap的实现上选取了链地址方法;  
也就是将哈希值一样的, 但是equals是false的, entry保存在同一个数组项里面,   
每一个数组项当做一个桶, 桶里面装的entry的key的hashCode是一样的;

### 几个常量的解释
❀ bucket(桶)  和 bin(箱子)  
约定前面的数组结构的每一个单元格称为桶;  
约定桶后面跟随的每一个数据称为箱子;  

❀ size(个数)  
size表示HashMap中存放KV的数量(为链表和树中的KV的总和);  

❀ capacity(容量)  
capacity就是指HashMap中桶的数量;默认值为16;一般第一次扩容时会扩容到64;  
之后是2倍, 容量都是2的幂;  

❀ loadFactor(装载因子)  
装载因子用来衡量HashMap满的程度;loadFactor的默认值为0.75f;  
计算HashMap的实时装载因子的方法为: size/capacity, 而不是占用桶的数量去除以capacity;  

❀ threshold  
threshold表示当HashMap的元素的个数大于threshold时会执行resize操作;   
threshold=capacity*loadFactor  
1.. 默认容量  
```
/**
 * The default initial capacity - MUST be a power of two.
 */
static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16
```
默认初始化的容量为16, 必须是2的幂;  
2.. 最大容量  
```
/**
 * The maximum capacity, used if a higher value is implicitly specified
 * by either of the constructors with arguments.
 * MUST be a power of two <= 1<<30.
 */
static final int MAXIMUM_CAPACITY = 1 << 30;
```
最大容量是2^30  
3.. 装载因子  
```
/**
 * The load factor used when none specified in constructor.
 */
static final float DEFAULT_LOAD_FACTOR = 0.75f;
```
默认的装载因子是0.75  
4.. 由链表转换成树的阈值TREEIFY_THRESHOLD  
```
/**
 * The bin count threshold for using a tree rather than list for a
 * bin.  Bins are converted to trees when adding an element to a
 * bin with at least this many nodes. The value must be greater
 * than 2 and should be at least 8 to mesh with assumptions in
 * tree removal about conversion back to plain bins upon
 * shrinkage.
 */
static final int TREEIFY_THRESHOLD = 8;
```
一个桶中bin(箱子)的存储方式由链表转换成树的阈值;  
即当桶中bin的数量超过TREEIFY_THRESHOLD时使用树来代替链表;  
默认值是8;  
5.. 由树转换成链表的阈值UNTREEIFY_THRESHOLD  
```
/**
 * The bin count threshold for untreeifying a (split) bin during a
 * resize operation. Should be less than TREEIFY_THRESHOLD, and at
 * most 6 to mesh with shrinkage detection under removal.
 */
static final int UNTREEIFY_THRESHOLD = 6;
```
一个桶中bin(箱子)的存储方式由树转换成链表的阈值;  
当执行resize操作时, 当桶中bin的数量少于UNTREEIFY_THRESHOLD时使用链表来代替树;  
默认值是6;   
6.. 表扩容的阈值MIN_TREEIFY_CAPACITY  
```
/**
 * The smallest table capacity for which bins may be treeified.
 * (Otherwise the table is resized if too many nodes in a bin.)
 * Should be at least 4 * TREEIFY_THRESHOLD to avoid conflicts
 * between resizing and treeification thresholds.
 */
static final int MIN_TREEIFY_CAPACITY = 64;
```
当桶中的bin被树化时, 最小的hash表容量;  
当HashMap中箱子的数量大于 TREEIFY_THRESHOLD , 小于 MIN_TREEIFY_CAPACITY 时, 会执行resize扩容操作;  
当HashMap中箱子的数量大于 MIN_TREEIFY_CAPACITY 时,  如果某个桶中的箱子数量大于8会被树化;   
当HashMap中箱子数量 size  * 0.75 大于 threshold 的时候, 就会扩容;  
threshold默认初始值 是 64,   默认装载因子是0.75;  

### Put 操作
结论  
hash数组中存放很多桶,  默认桶的个数是16;  
相同hashCode的箱子被放在一个桶内, 用链表存储;  
当箱子的个数大于等于8,  箱子就会被树化;  
当箱子的个数大于等于 桶的容量*装载因子 时,  hash数组就会被扩容;  
扩容时, 会引起一个桶内, 箱子数量减少;  
当箱子的数量小于6的时候,  就会从数变成链表;  


如果在创建HashMap实例时没有给定capacity, loadFactor则默认值分别是16和0.75。 
当好多bin被映射到同一个桶时, 如果这个桶中bin的数量小于TREEIFY_THRESHOLD当然不会转化成树形结构存储;  
如果这个桶中bin的数量大于了 TREEIFY_THRESHOLD , 但是capacity小于MIN_TREEIFY_CAPACITY 则依然使用链表结构进行存储, 此时会对HashMap进行扩容;  
如果capacity大于了MIN_TREEIFY_CAPACITY , 则会进行树化。  

put操作的过程  
put方法分两种情况, bucket是以链表形式存储的还是以树形结构存储的。  
如果是key已存在则修改旧值, 并返回旧值,   
如果key不存在, 则执行插入操作, 返回null。  
如果是插入操作还要modCount++。  
如果是链表存储时, 如果插入元素之后超过了TREEIFY_THRESHOLD, 还要进行树化操作。   
put操作, 当发生碰撞时, 如果是使用链表处理冲突, 执行的尾插法。  
这个跟ConcurrentHashMap不同, ConcurrentHashMap执行的是头插法。因为, 其HashEntry的next是final的。   
put操作的基本流程:    
(1)通过hash值得到所在bucket的下标, 如果为null, 表示没有发生碰撞, 则直接put   
(2)如果发生了碰撞, 则解决发生碰撞的实现方式: 链表还是树。   
(3)如果能够找到该key的结点, 则执行更新操作, 无需对modCount增1。   
(4)如果没有找到该key的结点, 则执行插入操作, 需要对modCount增1。   
(5)在执行插入操作时, 如果bucket中bin的数量超过TREEIFY_THRESHOLD, 则要树化。   
(6)在执行插入操作之后, 如果数组size超过了threshold, 这要扩容;  
### 怎么计算 hash 表位置  
```
public V put(K key, V value) {
    return putVal(hash(key), key, value, false, true);
}
static final int hash(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}

final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
               boolean evict) {
    Node<K,V>[] tab; Node<K,V> p; int n, i;
    if ((tab = table) == null || (n = tab.length) == 0)
        n = (tab = resize()).length;
    if ((p = tab[i = (n - 1) & hash]) == null)
        tab[i] = newNode(hash, key, value, null);
    }
}        
```
总体来讲就是, 
1.. 计算 h = key.hashCode();  
2.. h >>> 16;  无符号项右位移 16位,  右边的低位被丢弃, 左边的高位补 0;  
3.. 再 把之前的 hashCode 异或 上 位移之后的结果值;  
4.. 取当前的hash数组的长度 n, 当前的 index = (n-1) & hash;  

### 扩容   
因为 hash 值的计算规则是固定的, 所以在扩容的时候, 不需要再一次计算 hash 值;  
而是需要 把老的 hash数组的元素, 迁移到新的 hash 数组;  
hash 数组的索引值, 还是 (length -1) & hash 值;  
扩容过程中判断, hash & length 是 0 还是 1, 如果是 0 , 则待在原来的位置, 如果是 1 , 移动到新的位置;  


### 参考  
http://yikun.github.io/2015/04/01/Java-HashMap工作原理及实现/  

HashMap类的注释翻译  
http://blog.csdn.net/fan2012huan/article/details/510859243  

HashMap中capacity, loadFactor, threshold, size等概念的解释  
http://blog.csdn.net/fan2012huan/article/details/51087722  

HashMap的扩容及树化过程  
http://blog.csdn.net/fan2012huan/article/details/51088211  

HashMap源码注解 之 常量定义(一)  
http://blog.csdn.net/fan2012huan/article/details/51094454  

HashMap源码注解 之 成员变量(二)  
http://blog.csdn.net/fan2012huan/article/details/51094525  

HashMap源码注解 之 内部数据结构 Node (三)  
http://blog.csdn.net/fan2012huan/article/details/51097264  

HashMap源码注解 之 静态工具方法hash(), tableSizeFor()(四)  
http://blog.csdn.net/fan2012huan/article/details/51097331  

HashMap源码注解 之 get()方法(五)  
http://blog.csdn.net/fan2012huan/article/details/51130576  

HashMap源码注解 之 put()方法(六)  
http://blog.csdn.net/fan2012huan/article/details/51233378  

HashMap源码注解 之 resize()方法(七)  
http://blog.csdn.net/fan2012huan/article/details/51233540  

http://www.jianshu.com/p/aa017a3ddc40  
http://www.jianshu.com/p/e2f75c8cce01  
http://blog.csdn.net/lianhuazy167/article/details/66967698  

hash 索引计算  
https://www.cnblogs.com/zhengwang/p/8136164.html  
