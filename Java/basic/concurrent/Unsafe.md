### Unsafe  
CAS:   compare and swap  或者 compare and set 硬件同步原语, 比较并交换;  
synchronized 用的是悲观锁, cas 是乐观锁的一种实现形式, 在多核 CPU 机器上会有比较好的性能;  
CAS 只能保证一个共享变量的操作的原子性(原子性操作+原子性操作≠原子操作), 如果要保持多个共享变量的操作的原子性, 就必须使用锁;  

ABA 解决方案  
ABA 问题可以通过版本号来解决, 每次修改操作都添加一个版本号, 例如刚才的取款操作加个版本号 1, 在存款操作执行后版本号+1, 变为2;  
取款的第二次请求执行时就会判断版本号不是1, 执行失败;  
ABA问题, 原子变量 AtomicStampedReference, AtomicMarkableReference 用于解决 ABA 问题;  


AtomicReference#compareAndSet  
expectedValue: 预期值;  
newValue: 新值;  
当内存值和预期值相等, 把内存值设置为新值, 并返回 true;  
当内存值和预期值不等, 直接并返回 false;  

### compareAndSwapInt  
```
/**
* 比较 obj 的 offset 处内存位置中的值和期望的值, 如果相同则更新, 此更新是不可中断的;  
* 
* @param obj 需要更新的对象
* @param offset obj 中整型 field 的偏移量
* @param expect 希望 field 中存在的值
* @param update 如果期望值 expect 与 field 的当前值相同, 设置 filed 的值为这个新值
* @return 如果 field 的值被更改返回 true
*/
public native boolean compareAndSwapInt(Object obj, long offset, int expect, int update);
```
### 参考  
https://www.jb51.net/article/136718.htm  
https://juejin.im/post/5c7a86d2f265da2d8e7101a1  
http://www.tianxiaobo.com/2018/05/15/Java-中的-CAS-原理分析/  
