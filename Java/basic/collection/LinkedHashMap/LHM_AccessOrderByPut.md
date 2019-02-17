###### accessOrder 为false，存取有序


> 我们看一下put操作

##### 1. LinkedHashMap 只能调用父类 HashMap 的 public V put(K key, V value);  
##### 2. final V putVal(int hash, K key, V value, boolean onlyIfAbsent, boolean evict);  // 所以 无法 重写的
```
 final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        Node<K,V>[] tab; Node<K,V> p; int n, i;
        if ((tab = table) == null || (n = tab.length) == 0)
            n = (tab = resize()).length;
        if ((p = tab[i = (n - 1) & hash]) == null)
            tab[i] = newNode(hash, key, value, null);
        else {
           ...
            ...
            if (e != null) { // existing mapping for key
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                /*这个方法，HashMap是空实现的，留给 LinkedHashMap */    
                afterNodeAccess(e);
                return oldValue;
            }
        }
        ++modCount;
        if (++size > threshold)
            resize();
        /*这个方法，HashMap是空实现的，留给 LinkedHashMap */    
        afterNodeInsertion(evict);
        return null;
    }

```

> afterNodeAccess 方法的解释

```
void afterNodeAccess(Node<K,V> e) { // move node to last
    LinkedHashMap.Entry<K,V> last;
    /* 如果 accessOrder 为false， 什么都不做，LinkedHashMap默认 为false；
     * 既然默认什么都不做，怎么就能实现 存取有序呢？参见下文【存入时，关联双向链表】
     * */
    if (accessOrder && (last = tail) != e) {
        LinkedHashMap.Entry<K,V> p =
            (LinkedHashMap.Entry<K,V>)e, b = p.before, a = p.after;
        p.after = null;
        if (b == null)
            head = a;
        else
            b.after = a;
        if (a != null)
            a.before = b;
        else
            last = b;
        if (last == null)
            head = p;
        else {
            p.before = last;
            last.after = p;
        }
        tail = p;
        ++modCount;
    }
}
```

> 存入时，关联双向链表

在看 HashMap 的 putVal 操作；  

```
final V putVal(int hash, K key, V value, boolean onlyIfAbsent, boolean evict) {
    Node<K,V>[] tab; Node<K,V> p; int n, i;
    if ((tab = table) == null || (n = tab.length) == 0)
        n = (tab = resize()).length;
    if ((p = tab[i = (n - 1) & hash]) == null)
        /*newNode 会被 LinkedHashMap 重写*/
        tab[i] = newNode(hash, key, value, null);
    else {
         ...
        else {
            for (int binCount = 0; ; ++binCount) {
                if ((e = p.next) == null) {
                    /*newNode 会被 LinkedHashMap 重写*/
                    p.next = newNode(hash, key, value, null);
                    if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                        treeifyBin(tab, hash);
                    break;
                }
            ...
        }
    }           
}
```
> LinkedHashMap newNode 操作
```
Node<K,V> newNode(int hash, K key, V value, Node<K,V> e) {
    LinkedHashMap.Entry<K,V> p =
        new LinkedHashMap.Entry<K,V>(hash, key, value, e);
    linkNodeLast(p);
    return p;
}
```
> 尾加节点
```
private void linkNodeLast(LinkedHashMap.Entry<K,V> p) {
    LinkedHashMap.Entry<K,V> last = tail;
    tail = p;
    if (last == null)
        head = p;
    else {
        p.before = last;
        last.after = p;
    }
}
```
##### 3. 迭代 entrySet   
只干了一件事，之前顺序存入双向链表的，head ->.tail 取出来，就可以了 
```
public void forEach(BiConsumer<? super K, ? super V> action) {
    if (action == null)
        throw new NullPointerException();
    int mc = modCount;
    for (LinkedHashMap.Entry<K,V> e = head; e != null; e = e.after)
        action.accept(e.key, e.value);
    if (modCount != mc)
        throw new ConcurrentModificationException();
}
```