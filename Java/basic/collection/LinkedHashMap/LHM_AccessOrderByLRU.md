###### accessOrder 为true，LRU实现


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
##### 3. get 操作
```
public V get(Object key) {
    Node<K,V> e;
    if ((e = getNode(hash(key), key)) == null)
        return null;
    if (accessOrder)
        afterNodeAccess(e);
    return e.value;
}
```
> afterNodeAccess 方法的解释

```
void afterNodeAccess(Node<K,V> e) { // move node to last
    LinkedHashMap.Entry<K,V> last;
    /*  按照 访问 afterNodeAccess 的顺序，重排链表
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
 ##### 4. 迭代 entrySet   
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