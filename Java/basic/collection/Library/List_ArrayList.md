### ArrayList 

> 简单描述
 
ArrayList 是一个可变数组，实现List接口，允许所有添加元素，包括null；
添加、删除元素的时间复杂度是O(n)；修改元素的时间复杂度是O(1)；
默认容量是10，以1.5倍系数增长；
如果程序设计扩容情况，最好在ArrayList初始化时，给出适当的初始容量；
ArrayList不是 线程安全的，如果多线程操作访问同一个ArrayList，需要手动实现，如添加 synchronized 代码块；
在多线程的情况下可以考虑使用Collections.synchronizedList(List T)函数返回一个线程安全的ArrayList类，    
也可以使用并发包下的CopyOnWriteArrayList类。  

> ArrayList 转换为数组
```
UserEntity entityArray[] = new UserEntity[entityList.size()];
entityArray = entityList.toArray(entityArray);
Arrays.stream(entityArray).forEach(entity->{
    LogTrack.w(entity);
});
```

> ArrayList() 构造函数

```
public ArrayList(int initialCapacity) {
    if (initialCapacity > 0) {
        this.elementData = new Object[initialCapacity];
    } else if (initialCapacity == 0) {
        this.elementData = EMPTY_ELEMENTDATA;
    } else {
        throw new IllegalArgumentException("Illegal Capacity: "+initialCapacity);
    }
}
```
> 默认容量是10

```
/**
 * Default initial capacity.
 */
private static final int DEFAULT_CAPACITY = 10;

public boolean add(E e) {
    ensureCapacityInternal(size + 1);  // Increments modCount!!
    elementData[size++] = e;
    return true;
}

private void ensureCapacityInternal(int minCapacity) {
    if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
        minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
    }
    ensureExplicitCapacity(minCapacity);
}

```

> 1.5倍的系数增长 

int newCapacity = oldCapacity + (oldCapacity >> 1);
```
private void ensureExplicitCapacity(int minCapacity) {
    modCount++;

    // overflow-conscious code
    if (minCapacity - elementData.length > 0)
        grow(minCapacity);
}

private void grow(int minCapacity) {
    // overflow-conscious code
    int oldCapacity = elementData.length;
    int newCapacity = oldCapacity + (oldCapacity >> 1);
    if (newCapacity - minCapacity < 0)
        newCapacity = minCapacity;
    if (newCapacity - MAX_ARRAY_SIZE > 0)
        newCapacity = hugeCapacity(minCapacity);
    // minCapacity is usually close to size, so this is a win:
    elementData = Arrays.copyOf(elementData, newCapacity);
}

```
