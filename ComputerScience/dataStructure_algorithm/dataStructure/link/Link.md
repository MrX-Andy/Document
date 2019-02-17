### 链表
链表分为：单链表、双向链表  
指针的访问只能通过头结点或者尾节点进行访问；  
当删除节点，要先处理好节点的pre和next指针的指向，再删除节点、回收内存资源；  
◆ 单链表  
这种链表只有 头结点，每个节点有数据元素、指向下一个节点的指针元素；  
一般情况下，头结点并没有 数据元素，只有指针元素；  
指针元素初始值为空，只要有当新节点添加进来，老节点的next指针指向新节点的地址；  
```
{   first:
    data = null,    next->
}
{   node0:
    data = haha,    next->last
}
{   last:
    data = null,   next->
}
```

◆ 双向链表  
这种链表，既有头节点、也有尾节点；  
每个节点，既有pre指针、也有next指针；  
访问节点，可以通过头节点或者尾节点；  
```
{   first:
    data = null,    pre->,    next->
}
{   node0:
    data = haha,    pre->first,    next->last
}
{   last:
    data = null,   pre->node0,    next->
}
```
### 参考  
http://blog.csdn.net/juanqinyang/article/details/51351619  
http://www.cnblogs.com/whgk/p/6589920.html  

