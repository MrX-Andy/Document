### synchronized  
修饰实例方法, 多个线程, 访问同一个实例的, 加锁方法时, 会出现锁的竞争;    
修饰静态方法, 多个线程, 访问类的加锁方法时, 会出现锁的竞争;  
修饰代码块, 多线程访问到同一个代码块时, 会出现竞争的问题;  

类锁  
```
synchronized(A.class) {
    …
}
```
不同线程中访问这些此⽅法时, 都要对该类对象加锁;  
每个类只有一个类锁;  

对象锁  
```
synchronized(A.this) {
    …
}
```
不同的线程访问该⽅法时, 要对访问的对象加锁;  
每个对象有其独立的对象锁, 互不干扰;  

### 实现原理  

同步代码块:  
同步代码块, 是使用 monitorenter 和 monitorexit 指令实现的;  
monitorenter 指令插入到, 同步代码块的开始位置, monitorexit 指令插入到, 同步代码块的结束位置;  
JVM 需要保证每一个 monitorenter 都有一个 monitorexit 与之相对应;  
任何对象都有一个 monitor 与之相关联, 当且一个 monitor 被持有之后, 他将处于锁定状态;  

简单来说在JVM中 monitorenter 和 monitorexit 字节码, 依赖于底层的操作系统的 Mutex Lock (互斥锁) 来实现的;  
但是由于使用 Mutex Lock 需要将当前线程挂起, 并从用户态切换到内核态来执行, 这种切换的代价是非常昂贵的;  

同步方法:  
在字节码层面, 并没有任何特别的指令, 来实现被 synchronized 修饰的方法,  
而是在 Class 文件的方法表中, 将该方法的 access_flags 字段中的 synchronized 标志位置 1,  
表示该方法是同步方法, 并使用调用该方法的对象, 或该方法所属的 Class, 在JVM的内部对象表示 Klass 做为锁对象;  

synchronized 是基于, 进入和退出, 管程(Monitor)对象实现;  
在 JVM 中, 对象在内存中的布局, 分为3块区域: 对象头, 实例变量, 填充数据;  
实例变量:  
存放类的属性数据信息, 父类的属性信息;  
如果是数组的实例部分还包括数组的长度, 这部分内存按4字节对齐;  

填充数据:  
由于虚拟机要求对象起始地址必须是8字节的整数倍;  
填充数据不是必须存在的, 仅仅是为了字节对齐;  

头对象:  
实现 synchronized 的锁对象的基础;  
一般而言，synchronized 使用的锁对象, 是存储在 Java 对象头里的;  
jvm 中采用2个字来存储对象头, 其主要结构是由 Mark Word 和 Class Metadata Address 组成;  
Mark Word  存储对象的 hashCode, 锁信息, 分代年龄, GC 标志等信息;  
Class Metadata Address  类型指针指向对象的类元数据, JVM通过这个指针确定该对象是哪个类的实例;  
synchronized 的对象锁, 锁标识位为10, 其指针指向的是 monitor 对象(也称为管程或监视器锁)的起始地址;  
每个对象都存在着一个 monitor 与之关联;  
对象与其 monitor 之间的关系有存在多种实现方式;  
例如 monitor 可以与对象一起创建销毁, 当线程试图获取对象锁时自动生成, 但当一个 monitor 被某个线程持有后, 它便处于锁定状态;  
在 Java虚拟机(HotSpot)中, monitor 是由 ObjectMonitor 实现的;  
ObjectMonitor 中有两个队列, _WaitSet 和 _EntryList, 用来保存 ObjectWaiter 对象列表;  
_owner 指向持有 ObjectMonitor 对象的线程;  
当多个线程同时访问,一段同步代码时, 首先会进入 _EntryList 集合,   
当线程获取到对象的 monitor 后, 进入 _Owner 区域, 并把 monitor 中的 owner 变量设置为当前线程, 同时 monitor 中的计数器 count 加1;  
若当前线程调用 wait() 方法, 将释放当前持有的 monitor, owner 变量恢复为 null, count 自减1, 同时该线程进入 WaitSet 集合中等待被唤醒;  
若当前线程执行完毕, 也将释放 monitor(锁) 并复位变量的值, 以便其他线程进入获取 monitor(锁);  

由此看来, monitor 对象存在于每个 Java 对象, 的对象头中(存储的指针的指向);  
synchronized 的锁, 便是通过这种方式获取锁的, 也是为什么 Java 中任意对象可以作为锁的原因;  
同时也是 notify/notifyAll/wait 等方法存在于顶级对象 Object 中的原因;  


synchronized 的可重入性  
从互斥锁的设计上来说, 当一个线程, 试图操作一个, 由其他线程持有的,对象锁的,临界资源时, 将会处于阻塞状态;  
但当一个线程,再次请求自己持有的,对象锁, 的临界资源时, 这种情况属于重入锁;  
请求将会成功, 在 java 中 synchronized 是基于原子性的内部锁机制, 是可重入的,   
因此在一个线程调用 synchronized 方法的同时, 在其方法体内部调用该对象另一个 synchronized 方法,   
也就是说, 一个线程, 得到一个对象锁后, 再次请求该对象锁, 是允许的, 这就是 synchronized 的可重入性;  

wait  
wait()总是在一个循环中被调用, 挂起当前线程来等待一个条件的成立,  wait 调用会一直等到其他线程调用 notifyAll()时才返回;  
当一个线程在执行 synchronized 的方法内部, 调用了 wait()后, 该线程会释放该对象的锁,  然后该线程会被添加到, 该对象的等待队列中(waiting queue),  
只要该线程在等待队列中,  就会一直处于闲置状态, 不会被调度执行;  
要注意 wait()方法会强迫线程先进行释放锁操作, 所以在调用 wait()时, 该线程必须已经获得锁, 否则会抛出异常;  
由于 wait()在 synchronized 的方法内部被执行, 锁一定已经获得,  就不会抛出异常了;  

notify  
当一个线程调用一个对象的 notify()方法时, 调度器会从所有处于该对象等待队列(waiting queue)的线程中取出任意一个线程, 将其添加到入口队列( entry queue) 中;  
然后在入口队列中的多个线程就会竞争对象的锁, 得到锁的线程就可以继续执行, 如果等待队列中(waiting queue)没有线程, notify()方法不会产生任何作用;  
notify() 和 notifyAll()工作机制一样, 区别在于 notifyAll()会将等待队列(waiting queue), 中所有的线程都添加到入口队列中(entry queue);  
注意, notifyAll() 比 notify() 更加常用, 因为notify()方法只会唤起一个线程, 且无法指定唤醒哪一个线程,  
所以只有在多个执行相同任务的线程在并发运行时, 我们不关心哪一个线程被唤醒时, 才会使用notify();  

### 参考  
http://cmsblogs.com/?p=2071  
http://www.cnblogs.com/paddix/p/5367116.html  
http://www.cnblogs.com/paddix/p/5405678.html  
https://github.com/farmerjohngit/myblog/issues/12  
https://blog.csdn.net/javazejian/article/details/72828483  
http://www.cnblogs.com/javaminer/p/3889023.html  
https://juejin.im/post/5c37377351882525ec200f9e  
https://blog.csdn.net/lengxiao1993/article/details/52296220  
https://www.jianshu.com/p/f4454164c017  
http://www.open-open.com/lib/view/open1352431526366.html  
