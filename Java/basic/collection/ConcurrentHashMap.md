ConcurrentHashMap 是由 Segment 数组结构和 HashEntry 数组结构组成;  
Segment 是一种可重入锁(ReentrantLock), 在 ConcurrentHashMap 里扮演锁的角色, HashEntry 则用于存储键值对数据;  
一个 ConcurrentHashMap 里包含一个 Segment 数组, Segment 的结构和 HashMap 类似, 是一种数组和链表结构;  
一个 Segment 里包含一个 HashEntry 数组, 每个 HashEntry 是一个链表结构的元素,   
每个 Segment 守护着一个 HashEntry 数组里的元素, 当对 HashEntry 数组的数据进行修改时, 必须首先获得与它对应的 Segment 锁;  

锁分段技术  
ConcurrentHashMap 由多个 Segment 组成, Segment下包含很多 Node, 就是键值对, 每个 Segment 都有一个锁来实现线程安全,   
当一个线程占用锁访问其中一个段数据的时候, 其他段的数据也能被其他线程访问;  
### 几个常量的解释  
sizeCtl含义  
private transient volatile int sizeCtl;  
负数代表正在进行初始化或扩容操作;  
-1 代表正在初始化;  
-N 表示有 N-1 个线程正在进行扩容操作;  
正数或 0 代表 hash 表还没有被初始化, 这个数值表示初始化或下一次进行扩容的大小, 这一点类似于扩容阈值的概念;  
还后面可以看到, 它的值始终是当前 ConcurrentHashMap 容量的 0.75 倍, 这与 loadfactor 是对应的;  

CAS  
在 ConcurrentHashMap 中, 大量使用了 U.compareAndSwapXXX 的方法, 这个方法是利用一个 CAS 算法实现无锁化的修改值的操作, 他可以大大降低锁代理的性能消耗;   
[扩容函数 transfer](ConcurrentHashMap/fun_transfer.md)  

### spread  
再次hash, hash值均匀分布, 减少hash冲突;    
● 无符号右移  
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
    int hash = spread(key.hashCode());  //  再次hash, hash值均匀分布, 减少hash冲突;    
    int binCount = 0;
    for (Node<K,V>[] tab = table;;) {
        Node<K,V> f; int n, i, fh; K fk; V fv;
        if (tab == null || (n = tab.length) == 0)
            tab = initTable();  //  如果hash表为空, 初始化hash表  initTable;
        else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {  //  如果hash值对应的位置, 没有数据, 直接将value放进去, 结束 putVal;
            if (casTabAt(tab, i, null, new Node<K,V>(hash, key, value)))
                break;                   // no lock when adding to empty bin
        }
        else if ((fh = f.hash) == MOVED)  //  需要扩容  
            tab = helpTransfer(tab, f);
        else if (onlyIfAbsent && fh == hash &&  // check first node
                 ((fk = f.key) == key || fk != null && key.equals(fk)) &&
                 (fv = f.val) != null)      //  key value都存在, 表示重复,  结束 putVal;
            return fv;
        else {
            V oldVal = null;
            synchronized (f) {
                if (tabAt(tab, i) == f) {
                    if (fh >= 0) {  //  如果是链表节点  
                        binCount = 1;
                        for (Node<K,V> e = f;; ++binCount) {
                            K ek;
                            if (e.hash == hash &&
                                ((ek = e.key) == key ||
                                 (ek != null && key.equals(ek)))) {
                                oldVal = e.val;
                                if (!onlyIfAbsent)  //  key相同, 替换原先的value
                                    e.val = value;
                                break;
                            }
                            Node<K,V> pred = e;
                            if ((e = e.next) == null) {  //  在链表尾, 插入新的节点  
                                pred.next = new Node<K,V>(hash, key, value);
                                break;
                            }
                        }
                    }
                    else if (f instanceof TreeBin) {  //  如果是 树节点
                        Node<K,V> p;
                        binCount = 2; 
                        // 插入节点, 并旋转红黑树  
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
            if (binCount != 0) {
                if (binCount >= TREEIFY_THRESHOLD)  //  同一个 hash位置, 链表的长度 >=8  就要树化
                    treeifyBin(tab, i);
                if (oldVal != null)
                    return oldVal;
                break;
            }
        }
    }
    addCount(1L, binCount);  //  统计size, 并且检查是否需要扩容
    return null;
}
```

### initTable  
```
private final Node<K,V>[] initTable() {
    Node<K,V>[] tab; int sc;
    while ((tab = table) == null || tab.length == 0) {   //  hash表为空, 进行初始化  
        if ((sc = sizeCtl) < 0)  //  sizeCtl<0表示其他线程已经在初始化了或者扩容了, 挂起当前线程 
            Thread.yield(); // lost initialization race; just spin
        else if (U.compareAndSetInt(this, SIZECTL, sc, -1)) {  //  CAS操作SIZECTL为-1, 表示初始化状态  
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
        if ((eh = e.hash) == h) {  //  如果该节点就是首节点就返回
            if ((ek = e.key) == key || (ek != null && key.equals(ek)))
                return e.val;
        }
        //  hash值为负值表示正在扩容, 这个时候查的是ForwardingNode的find方法来定位到nextTable来查找, 查找到就返回
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

### 参考  
http://www.importnew.com/22007.html  
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




