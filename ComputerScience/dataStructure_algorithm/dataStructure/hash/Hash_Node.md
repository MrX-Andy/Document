###### 关于哈希表的基本知识

> 哈希表箱子的结构

哈希表需要 哈希值、key、value等元素；  
红黑树，需要 左孩子、右孩子、父节点等指针；  
LinkedHashMap （LRU算法）需要 前驱指针、后驱指针；  

```
class Node{
    Node leftChildNode;   
    Node rightChildNode;   
    Node parentNode;   
    Node nextNode;   
    Node preNode;   
    Object key;
    int hashCode;
    Object value;
}
```¡

> 装载因子

数据项 和 表长 的比例，叫装载因子（loadFactor = itemCount / arraySize）；  
装载因子越大，哈希表越满，越容易出现冲突，性能就会越低，越小越浪费内存，一般装载因子是0.75比较合适；  
当装载因子大于0.75时，就会扩容，一般是2倍扩容；  




  
