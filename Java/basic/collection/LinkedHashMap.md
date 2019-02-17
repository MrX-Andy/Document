###### LinkedHashMap
基于JDK8，学习
> 简单描述

以前呢，只是听说过，LinkedHashMap 是存取有序，HashMap 是存取无序，这种口诀真是害死人；  
事实上，LinkedHashMap 还可以做LRU的实现， 我简直是FUCK了。  

- LinkedHashMap 是 HashMap 的子类;
- LinkedHashMap 同样不是 线程安全的；
- accessOrder 为 true ，迭代entrySet ，get操作，都是按照LRU操作；  
- accessOrder 为 false ，迭代entrySet ，get操作，都是按照Put的顺序操作；  

> LinkedHashMap 的实现
- [简单示例](LinkedHashMap/LHM_Sample.md)
- [怎么实现存取有序](LinkedHashMap/LHM_AccessOrderByPut.md)
- [怎么实现LRU](LinkedHashMap/LHM_AccessOrderByLRU.md)
 
 > 参考
 - http://blog.csdn.net/ns_code/article/details/37867985
 