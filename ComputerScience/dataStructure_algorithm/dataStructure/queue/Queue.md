### 队列

队列是一种特殊的线性表, 只允许在 队尾(rear)添加元素, 只允许在 队首(front)删除元素;  
队列中没有元素时, 称为空队列;  
在队列中插入一个队列元素称为入队(enqueue), 从队列中删除一个队列元素称为出队(dequeue)  

### 顺序队列

简单的顺序队列, 用数组存储数据, 
```
[_, _, _, _, _]  front、rear 都在最左边(队首)
[A, _, _, _, _]  front 在 index=0, rear在index= 1
[A, B, _, _, _]  front 在 index=0, rear在index= 2
[A, B, C, _, _]  front 在 index=0, rear在index= 3
[A, B, C, D, _]  front 在 index=0, rear在index= 4
[A, B, C, D, E]  front 在 index=0, rear在index= 5(队满)
[_, B, C, D, E]  front 在 index=1, rear在index= 5(队满)
[_, _, C, D, E]  front 在 index=2, rear在index= 5(队满)
[_, _, _, D, E]  front 在 index=3, rear在index= 5(队满)
[_, _, _, _, E]  front 在 index=4, rear在index= 5(队满)
[_, _, _, _, _]  front 在 index=5, rear在index= 5(队满)
这时候的队满, 其实就是假溢出；
当
```

### 参考  
http://www.cnblogs.com/smyhvae/p/4793339.html  
http://blog.csdn.net/javazejian/article/details/53375004  
