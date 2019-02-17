###### LinkedList

> 简单描述

LinkedList 通过 双向 链表实现；实现List接口，允许添加所有元素，包括null；
所有操作都是继续双向链表，可以从链表头 或者 链表尾进行访问；
添加、删除元素的时间复杂度是O(1)；
带索引index 的 添加、删除元素的时间复杂度是O(n)；
查询元素的时间复杂度是O(n)；
LinkedList 不是 线程安全的，如果多线程操作访问同一个 LinkedList，需要手动实现，如添加 synchronized 代码块；

> 节点
```
private static class Node<E> {
    E item;
    Node<E> next;
    Node<E> prev;

    Node(Node<E> prev, E element, Node<E> next) {
        this.item = element;
        this.next = next;
        this.prev = prev;
    }
}
```

> add
```
public boolean add(E e) {
    linkLast(e);
    return true;
}

/**
 * Links e as last element.
 */
void linkLast(E e) {
    final Node<E> l = last;
    final Node<E> newNode = new Node<>(l, e, null);
    last = newNode;
    if (l == null)
        first = newNode;
    else
        l.next = newNode;
    size++;
    modCount++;
}

```