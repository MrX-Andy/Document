Node<K,V>[] table;  
ConcurrentHashMap 在 java8 已经摒弃了 Segment 的概念, 而是直接使用 Node 数组+链表+红黑树的数据结构来实现;  
使用 synchronized 和 CAS 控制并发操作;  

### 名词解释  
private transient volatile int sizeCtl;  
负数代表正在进行初始化或扩容操作;  
-1 代表正在初始化;  
-N 表示有 N-1 个线程正在进行扩容操作;  
0 代表 hash 表还没有被初始化;  
当为正数时, 表示初始化或者下一次进行扩容的大小;  

private static final int MAX_RESIZERS = (1 << (32 - RESIZE_STAMP_BITS)) - 1;  
2^15-1，help resize的最大线程数  

private static final int RESIZE_STAMP_SHIFT = 32 - RESIZE_STAMP_BITS;  
32-16=16，sizeCtl中记录size大小的偏移量;  

ForwardingNode  
一个特殊的 Node 节点, hash 值为 -1, 其中存储 nextTable 的引用, 只有 table 发生扩容的时候, ForwardingNode 才会发挥作用;  
作为一个占位符放在 table 中表示当前节点为 null 或则已经被移动;  



CAS  
在 ConcurrentHashMap 中, 大量使用了 U.compareAndSwapXXX 的方法, 这个方法是利用一个 CAS 算法实现无锁化的修改值的操作, 他可以大大降低锁代理的性能消耗;   
### 扩容函数 transfer   
支持多线程进行扩容操作，并没有加锁 ，这样做的目的不仅仅是为了满足concurrent的要求，而是希望利用并发处理去减少扩容带来的时间影响。  
单线程扩容的大体思想就是遍历、复制的过程。首先根据运算得到需要遍历的次数i，然后利用tabAt方法获得i位置的元素：  

如果这个位置为空，就在原table中的i位置放入forwardNode节点，这个也是触发并发扩容的关键点；  
如果这个位置是Node节点（fh>=0），如果它是一个链表的头节点，就构造一个反序链表，把他们分别放在nextTable的i和i+n的位置上  
如果这个位置是TreeBin节点（fh<0），也做一个反序处理，并且判断是否需要untreefi，把处理的结果分别放在nextTable的i和i+n的位置上  
遍历过所有的节点以后就完成了复制工作，这时让nextTable作为新的table，并且更新sizeCtl为新容量的0.75倍，完成扩容。  
多线程遍历节点，处理了一个节点，就把对应点的值set为forward，另一个线程看到forward，就向后继续遍历，再加上给节点上锁的机制，就完成了多线程的控制。
这样交叉就完成了复制工作。而且还很好的解决了线程安全的问题。  


### spread  
再次hash, hash值均匀分布, 减少hash冲突;    
无符号右移  
各个位向右移指定的位数;右移后左边突出的位用零来填充;移出右边的位被丢弃  
```
static final int HASH_BITS = 0x7fffffff; // usable bits of normal node hash   //01111111_11111111_11111111_11111111
static final int spread(int h) {
    //  无符号右移加入高位影响, & HASH_BITS用于把hash值转化为正数, 负数hash是有特别的作用的   
    return (h ^ (h >>> 16)) & HASH_BITS;
}
```
### putVal  
如果没有初始化就先调用initTable（）方法来进行初始化过程  
如果没有hash冲突就直接CAS插入  
如果还在进行扩容操作就先进行扩容  
如果存在hash冲突, 就加锁来保证线程安全, 这里有两种情况, 一种是链表形式就直接遍历到尾端插入, 一种是红黑树就按照红黑树结构插入,   
最后一个如果该链表的数量大于阈值8, 就要先转换成黑红树的结构, break再一次进入循环  
如果添加成功就调用addCount（）方法统计size, 并且检查是否需要扩容  
```
final V putVal(K key, V value, boolean onlyIfAbsent) {
    if (key == null || value == null) throw new NullPointerException();
    //  再次 hash, hash 值均匀分布, 减少 hash 冲突;    
    int hash = spread(key.hashCode());  
    int binCount = 0;
    for (Node<K,V>[] tab = table;;) {
        Node<K,V> f; int n, i, fh; K fk; V fv;
        if (tab == null || (n = tab.length) == 0)
            tab = initTable();  //  如果 hash 表为空, 初始化 hash表  initTable;  
        //   table[i] 为空, 利用 CAS 在 table[i] 头结点直接插入, 
        //  如果插入成功, 退出插入操作;  
        //  如果插入失败, 则有其他节点已经插入, 继续下一步;  
        else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {  
             //   如果 hash 值对应的位置, 没有数据, 用 CAS 将 value 放进去, 插入成功, 结束 putVal;
            if (casTabAt(tab, i, null, new Node<K,V>(hash, key, value)))
                break;                   // no lock when adding to empty bin
        }
        //  需要扩容 , 如果 table[i] 不为空, 且 table[i] 的 hash 值为 -1, 则有其他线程在执行扩容操作, 帮助他们一起扩容, 提高性能  
        else if ((fh = f.hash) == MOVED)  
            tab = helpTransfer(tab, f);
        //  key value都存在, 表示重复,  结束 putVal;  
        else if (onlyIfAbsent && fh == hash &&  // check first node
                 ((fk = f.key) == key || fk != null && key.equals(fk)) &&
                 (fv = f.val) != null)      
            return fv;
        //  如果以上条件都不满足, 也就是存在 hash 冲突, 那就要进行加锁操作, 锁住链表或者红黑树的头结点;  
        else {
            V oldVal = null;
            synchronized (f) {
                if (tabAt(tab, i) == f) {
                    //  fh >= 0, 表示该节点是链表结构, 将该节点插入到链表尾部  
                    if (fh >= 0) {  
                        binCount = 1;
                        for (Node<K,V> e = f;; ++binCount) {
                            K ek;
                            //  key相同, 替换原先的value
                            if (e.hash == hash &&
                                ((ek = e.key) == key ||
                                 (ek != null && key.equals(ek)))) {
                                oldVal = e.val;
                                if (!onlyIfAbsent)  
                                    e.val = value;
                                break;
                            }
                            Node<K,V> pred = e;
                            //  在链表尾, 插入新的节点  
                            if ((e = e.next) == null) {  
                                pred.next = new Node<K,V>(hash, key, value);
                                break;
                            }
                        }
                    }  
                    //  如果是树节点, 这个过程采用同步内置锁实现并发  
                    else if (f instanceof TreeBin) {  
                        Node<K,V> p;
                        binCount = 2; 
                        //  插入节点, 并旋转红黑树  
                        if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key,
                                                       value)) != null) {
                            oldVal = p.val;
                            if (!onlyIfAbsent)
                                p.val = value;
                        }
                    }
                    else if (f instanceof ReservationNode)  //  如果是 预留节点
                        throw new IllegalStateException("Recursive update");
                }
            }
            //  到此时, 已将键值对插入到了合适的位置, 检查链表长度是否超过阈值, 若是, 则转变为红黑树结构
            if (binCount != 0) {
                //  同一个 hash 位置, 链表的长度 >=8  就要树化  
                if (binCount >= TREEIFY_THRESHOLD)  
                    treeifyBin(tab, i);
                if (oldVal != null)
                    return oldVal;
                break;
            }
        }
    }
    //  count+1, 如有必要, 则扩容  
    addCount(1L, binCount);  //  统计size, 并且检查是否需要扩容
    return null;
}
```
1.. 如果待插入的键值对中 key 或 value 为 null, 抛出异常, 结束, 否则执行 2  
2.. 如果 table 为 null, 则进行初始化操作 initTable(), 否则执行3   
3.. 如果 table[i] 为空, 则用 CAS 在 table[i] 头结点直接插入, 
      如果 CAS 执行成功, 退出插入操作, 执行步骤 7;  
      如果 CAS 失败, 则说明有其他节点已经插入, 执行 4;  
4.. 此时判断, hash 值是否为 MOVED(-1), 如果是则说明, 有其他线程在执行扩容操作, 帮助他们一起扩容, 来提高性能, 如果没有在扩容, 那么执行 5;  
5.. 判断 hash 的值, 如果 >=0, 则在链表合适的位置插入, 否则查看 table[i] 是否是红黑树结构, 如果是, 则在红黑树适当位置插入;  
      到此时, 键值对已经顺利插入, 接下来执行 6;  
6.. 如果 table[i] 节点数 binCount 不为 0, 判断它此时的状态, 是否需要转变为红黑树;  
7.. 执行 addCount(1L,  binCount);  

### initTable  
```
private final Node<K,V>[] initTable() {
    Node<K,V>[] tab; int sc;
    while ((tab = table) == null || tab.length == 0) {   //  hash表为空, 进行初始化  
        if ((sc = sizeCtl) < 0)  //  sizeCtl<0表示其他线程已经在初始化了或者扩容了, 挂起当前线程 
            Thread.yield(); 
        //  如果该线程获取了初始化的权利, 则用 CAS 将 sizeCtl 设置为 -1, 表示本线程正在初始化
        else if (U.compareAndSetInt(this, SIZECTL, sc, -1)) {  
            try {
                if ((tab = table) == null || tab.length == 0) {
                    int n = (sc > 0) ? sc : DEFAULT_CAPACITY;
                    @SuppressWarnings("unchecked")
                    Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n];
                    table = tab = nt;
                    sc = n - (n >>> 2);  //  记录下次扩容的大小
                }
            } finally {
                sizeCtl = sc;
            }
            break;
        }
    }
    return tab;
}
```
### helpTransfer  
```
final Node<K,V>[] helpTransfer(Node<K,V>[] tab, Node<K,V> f) {
    Node<K,V>[] nextTab; int sc;
    if (tab != null && (f instanceof ForwardingNode) &&
        (nextTab = ((ForwardingNode<K,V>)f).nextTable) != null) {  //  新的table nextTba已经存在前提下才能帮助扩容
        int rs = resizeStamp(tab.length);
        while (nextTab == nextTable && table == tab &&
               (sc = sizeCtl) < 0) {
            if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 ||
                sc == rs + MAX_RESIZERS || transferIndex <= 0)
                break;
            if (U.compareAndSetInt(this, SIZECTL, sc, sc + 1)) {
                transfer(tab, nextTab);  //  调用扩容方法  
                break;
            }
        }
        return nextTab;
    }
    return table;
}
```
### transfer  
```
private final void transfer(Node<K,V>[] tab, Node<K,V>[] nextTab) {
    int n = tab.length, stride;
    //  每核处理的量小于16, 则强制赋值16
    if ((stride = (NCPU > 1) ? (n >>> 3) / NCPU : n) < MIN_TRANSFER_STRIDE)
        stride = MIN_TRANSFER_STRIDE; // subdivide range
    if (nextTab == null) {            // initiating
        try {
            @SuppressWarnings("unchecked")
            Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n << 1];
            nextTab = nt;
        } catch (Throwable ex) {      // try to cope with OOME
            sizeCtl = Integer.MAX_VALUE;
            return;
        }
        nextTable = nextTab;
        transferIndex = n;
    }
    int nextn = nextTab.length;
    //  连接点指针, 用于标志位（fwd的hash值为-1, fwd.nextTable=nextTab）
    ForwardingNode<K,V> fwd = new ForwardingNode<K,V>(nextTab);
    boolean advance = true;  //  当advance == true时, 表明该节点已经处理过了
    boolean finishing = false; // to ensure sweep before committing nextTab
    for (int i = 0, bound = 0;;) {
        Node<K,V> f; int fh;
        while (advance) {
            int nextIndex, nextBound;
            if (--i >= bound || finishing)
                advance = false;
            else if ((nextIndex = transferIndex) <= 0) {
                i = -1;
                advance = false;
            }
            //  用CAS计算得到的transferIndex  
            else if (U.compareAndSetInt
                     (this, TRANSFERINDEX, nextIndex,
                      nextBound = (nextIndex > stride ?
                                   nextIndex - stride : 0))) {
                bound = nextBound;
                i = nextIndex - 1;
                advance = false;
            }
        }
        if (i < 0 || i >= n || i + n >= nextn) {
            int sc;
            //  已经完成所有节点复制了
            if (finishing) {
                nextTable = null;
                table = nextTab;
                sizeCtl = (n << 1) - (n >>> 1);  //  sizeCtl阈值为原来的1.5倍  
                return;
            }
            //  CAS 更扩容阈值, 在这里面sizectl值减一, 说明新加入一个线程参与到扩容操作
            if (U.compareAndSetInt(this, SIZECTL, sc = sizeCtl, sc - 1)) {
                if ((sc - 2) != resizeStamp(n) << RESIZE_STAMP_SHIFT)
                    return;
                finishing = advance = true;
                i = n; // recheck before commit
            }
        }
        //  遍历的节点为null, 则放入到ForwardingNode 指针节点
        else if ((f = tabAt(tab, i)) == null)
            advance = casTabAt(tab, i, null, fwd);
        //  f.hash == -1 表示遍历到了ForwardingNode节点, 意味着该节点已经处理过了
        //  这里是控制并发扩容的核心
        else if ((fh = f.hash) == MOVED)
            advance = true; // already processed
        else {
            synchronized (f) {
                if (tabAt(tab, i) == f) {
                    Node<K,V> ln, hn;
                    if (fh >= 0) {
                         //  构造两个链表  一个是原链表  另一个是原链表的反序排列
                        int runBit = fh & n;
                        Node<K,V> lastRun = f;
                        for (Node<K,V> p = f.next; p != null; p = p.next) {
                            int b = p.hash & n;
                            if (b != runBit) {
                                runBit = b;
                                lastRun = p;
                            }
                        }
                        if (runBit == 0) {
                            ln = lastRun;
                            hn = null;
                        }
                        else {
                            hn = lastRun;
                            ln = null;
                        }
                        for (Node<K,V> p = f; p != lastRun; p = p.next) {
                            int ph = p.hash; K pk = p.key; V pv = p.val;
                            if ((ph & n) == 0)
                                ln = new Node<K,V>(ph, pk, pv, ln);
                            else
                                hn = new Node<K,V>(ph, pk, pv, hn);
                        }
                        //  在nextTable i 位置处插上链表
                        setTabAt(nextTab, i, ln);
                        //  在nextTable i + n 位置处插上链表
                        setTabAt(nextTab, i + n, hn);
                        //  在table i 位置处插上ForwardingNode 表示该节点已经处理过了
                        setTabAt(tab, i, fwd);
                        //  advance = true 可以执行--i动作, 遍历节点
                        advance = true;
                    }
                    //  如果是TreeBin, 则按照红黑树进行处理, 处理逻辑与上面一致
                    else if (f instanceof TreeBin) {
                        TreeBin<K,V> t = (TreeBin<K,V>)f;
                        TreeNode<K,V> lo = null, loTail = null;
                        TreeNode<K,V> hi = null, hiTail = null;
                        int lc = 0, hc = 0;
                        for (Node<K,V> e = t.first; e != null; e = e.next) {
                            int h = e.hash;
                            TreeNode<K,V> p = new TreeNode<K,V>
                                (h, e.key, e.val, null, null);
                            if ((h & n) == 0) {
                                if ((p.prev = loTail) == null)
                                    lo = p;
                                else
                                    loTail.next = p;
                                loTail = p;
                                ++lc;
                            }
                            else {
                                if ((p.prev = hiTail) == null)
                                    hi = p;
                                else
                                    hiTail.next = p;
                                hiTail = p;
                                ++hc;
                            }
                        }
                        //  扩容后树节点个数若<=6, 将树转链表
                        ln = (lc <= UNTREEIFY_THRESHOLD) ? untreeify(lo) :
                            (hc != 0) ? new TreeBin<K,V>(lo) : t;
                        hn = (hc <= UNTREEIFY_THRESHOLD) ? untreeify(hi) :
                            (lc != 0) ? new TreeBin<K,V>(hi) : t;
                        setTabAt(nextTab, i, ln);
                        setTabAt(nextTab, i + n, hn);
                        setTabAt(tab, i, fwd);
                        advance = true;
                    }
                }
            }
        }
    }
}

```
### treeifyBin  
````
private final void treeifyBin(Node<K,V>[] tab, int index) {
    Node<K,V> b; int n;
    if (tab != null) {
        //  如果整个table的数量小于64, 就扩容至原来的一倍, 不转红黑树了
        //  因为这个阈值扩容可以减少hash冲突, 不必要去转红黑树
        if ((n = tab.length) < MIN_TREEIFY_CAPACITY)
            tryPresize(n << 1);
        else if ((b = tabAt(tab, index)) != null && b.hash >= 0) {
            synchronized (b) {
                if (tabAt(tab, index) == b) {
                    TreeNode<K,V> hd = null, tl = null;
                    for (Node<K,V> e = b; e != null; e = e.next) {
                        TreeNode<K,V> p =
                            new TreeNode<K,V>(e.hash, e.key, e.val,
                                              null, null);
                        if ((p.prev = tl) == null)
                            hd = p;
                        else
                            tl.next = p;
                        tl = p;
                    }
                    //  通过TreeBin对象对TreeNode转换成红黑树
                    setTabAt(tab, index, new TreeBin<K,V>(hd));
                }
            }
        }
    }
}

````

### addCount
```
private final void addCount(long x, int check) {
    CounterCell[] as; long b, s;
    //  更新baseCount, table的数量, counterCells表示元素个数的变化
    if ((as = counterCells) != null ||
        !U.compareAndSetLong(this, BASECOUNT, b = baseCount, s = b + x)) {
        CounterCell a; long v; int m;
        boolean uncontended = true;
        //  如果多个线程都在执行, 则CAS失败, 执行fullAddCount, 全部加入count
        if (as == null || (m = as.length - 1) < 0 ||
            (a = as[ThreadLocalRandom.getProbe() & m]) == null ||
            !(uncontended =
              U.compareAndSetLong(a, CELLVALUE, v = a.value, v + x))) {
            fullAddCount(x, uncontended);
            return;
        }
        if (check <= 1)
            return;
        s = sumCount();
    }
    //  check>=0表示需要进行扩容操作
    if (check >= 0) {
        Node<K,V>[] tab, nt; int n, sc;
        while (s >= (long)(sc = sizeCtl) && (tab = table) != null &&
               (n = tab.length) < MAXIMUM_CAPACITY) {
            int rs = resizeStamp(n);
            if (sc < 0) {
                if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 ||
                    sc == rs + MAX_RESIZERS || (nt = nextTable) == null ||
                    transferIndex <= 0)
                    break;
                if (U.compareAndSetInt(this, SIZECTL, sc, sc + 1))
                    transfer(tab, nt);
            }
            else if (U.compareAndSetInt(this, SIZECTL, sc,
                                         (rs << RESIZE_STAMP_SHIFT) + 2))
                transfer(tab, null);
            s = sumCount();
        }
    }
}

```
### get
```
public V get(Object key) {
    Node<K,V>[] tab; Node<K,V> e, p; int n, eh; K ek;
    int h = spread(key.hashCode());  //  计算两次hash  
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (e = tabAt(tab, (n - 1) & h)) != null) {  //  读取首节点的Node元素
        //  搜索到的节点 key 与传入的 key 相同且不为 null, 直接返回这个节点;  
        if ((eh = e.hash) == h) {  //  如果该节点就是首节点就返回
            if ((ek = e.key) == key || (ek != null && key.equals(ek)))
                return e.val;
        }
        //  hash值为负值表示正在扩容, 这个时候查的是 ForwardingNode 的 find 方法来定位到 nextTable来查找, 查找到就返回
        else if (eh < 0)
            return (p = e.find(h, key)) != null ? p.val : null;
        while ((e = e.next) != null) {  //  既不是首节点也不是ForwardingNode, 那就往下遍历
            if (e.hash == h &&
                ((ek = e.key) == key || (ek != null && key.equals(ek))))
                return e.val;
        }
    }
    return null;
}
```
### 常见问题  
#### 为什么线程同步用的是 synchronized 而不是锁  
为什么是 synchronized, 而不是可重入锁   
1.. 减少内存开销, 假设使用可重入锁来获得同步支持, 那么每个节点都需要通过继承 AQS 来获得同步支持, 但并不是每个节点都需要获得同步支持的,   
只有链表的头节点(红黑树的根节点)需要同步, 这无疑带来了巨大内存浪费;   
2.. 获得 JVM 的支持 可重入锁毕竟是 API 这个级别的, 后续的性能优化空间很小, synchronized 则是 JVM 直接支持的,   
JVM 能够在运行时作出相应的优化措施: 锁粗化, 锁消除, 锁自旋等等;  



### 参考  
https://juejin.im/entry/592a39820ce4630057778b80  
https://my.oschina.net/hosee/blog/639352  
https://my.oschina.net/hosee/blog/675884  
https://www.cnblogs.com/study-everyday/p/6430462.html  

https://www.jianshu.com/p/c0642afe03e0  
https://www.jianshu.com/p/23b84ba9a498  

http://www.importnew.com/26049.html  
http://blog.csdn.net/u010723709/article/details/48007881  
http://blog.csdn.net/fjse51/article/details/55260493  
https://www.cnblogs.com/everSeeker/p/5601861.html   
https://javadoop.com/post/hashmap  
https://www.jianshu.com/p/d10256f0ebea  
https://www.cnblogs.com/wenbochang/p/8484779.html  
https://blog.csdn.net/makeliwei1/article/details/81287855  
https://www.jianshu.com/p/c0642afe03e0  
http://www.jianshu.com/p/e694f1e868ec  
https://my.oschina.net/liuxiaomian/blog/880088  
http://cmsblogs.com/?p=2283  
https://bentang.me/tech/2016/12/01/jdk8-concurrenthashmap-1/  
https://blog.csdn.net/u011392897/article/details/60479937  
https://blog.csdn.net/u010412719/article/details/52145145  




