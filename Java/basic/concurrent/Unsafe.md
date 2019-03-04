### Unsafe  
cas:   compare and swap  硬件同步原语、比较并交换  
区别于 synchronized 同步锁的一种乐观锁，使用这些类在多核CPU的机器上会有比较好的性能；  
CAS有3个操作数，内存值V，一个旧值（期望操作前的值）A，要修改的新值B。当且仅当旧值A和内存值V相同时，将内存值V修改为B，否则什么都不做；  
对于并发控制而言，锁是一种悲观策略，会阻塞线程执行。而无锁是一种乐观策略；  

CAS只能保证一个共享变量的操作的原子性（原子性操作+原子性操作≠原子操作），如果要保持多个共享变量的操作的原子性，就必须使用锁。    

### ABA解决方案  
ABA问题可以通过版本号来解决，每次修改操作都添加一个版本号。例如刚才的取款操作加个版本号 1，在存款操作执行后版本号+1，变为2。  
取款的第二次请求执行时就会判断版本号不是1，执行失败。  
ABA问题，原子变量AtomicStampedReference，AtomicMarkableReference用于解决ABA问题。  


### compareAndSwapInt  
```
/**
* 比较obj的offset处内存位置中的值和期望的值，如果相同则更新。此更新是不可中断的。
* 
* @param obj 需要更新的对象
* @param offset obj中整型field的偏移量
* @param expect 希望field中存在的值
* @param update 如果期望值expect与field的当前值相同，设置filed的值为这个新值
* @return 如果field的值被更改返回true
*/
public native boolean compareAndSwapInt(Object obj, long offset, int expect, int update);
```
### 参考  
https://www.jb51.net/article/136718.htm  
https://juejin.im/post/5c7a86d2f265da2d8e7101a1  
