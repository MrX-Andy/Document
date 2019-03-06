### 并发编程   
当多个线程访问一个类时, 如果不用考虑这些线程在运行时环境下的调度和交替运行, 并且不需要额外的同步及在调用方代码不必做其他的协调,    
这个类的行为仍然是正确的, 那么这个类就是线程安全的;   

[多线程架构图](basic/concurrent/ImageFiles/mt_001.png)  
[锁-锁的概念](basic/concurrent/concept_lock.md)  
### 示例代码  
[线程安全与线程不安全 demo](basic/concurrent/sample/safe_unsafe.md)   
[简化封装 ThreadUtil](basic/concurrent/ThreadUtil.md)  
[示例-周期执行](basic/concurrent/sample/ses_01.md)   

示例-并发执行, 多任务完成, 反馈;  
[链接](basic/concurrent/sample/es_01.md)   

示例-并发执行, 多任务完成, 反馈 ExecutorCompletionService;  
[链接](basic/concurrent/sample/es_01.md)   

### 类逐个介绍  
[Unsafe CAS 比较并交换](basic/concurrent/Unsafe.md)  

synchronized; object#wait, object#notify 的实现原理;  
[链接](basic/concurrent/synchronized.md)  

[ThreadLocal](basic/concurrent/ThreadLocal.md)   
[Executors](basic/concurrent/Executors.md)  
[ExecutorService](basic/concurrent/ExecutorService.md)  
[ScheduledExecutorService](basic/concurrent/ScheduledExecutorService.md)  
[ThreadPoolExecutor](basic/concurrent/ThreadPoolExecutor.md)  
[ScheduledThreadPoolExecutor](basic/concurrent/ScheduledThreadPoolExecutor.md)  
[Future](basic/concurrent/Future.md)  
[CompletionService](basic/concurrent/CompletionService.md)  
[Timer](basic/concurrent/Timer.md)  
[Semaphore](basic/concurrent/Semaphore.md)  
[ReentrantLock](basic/concurrent/ReentrantLock.md)  
[Exchanger](basic/concurrent/Exchanger.md)  
[Atomic](basic/concurrent/Atomic)  
[CountDownLatch](basic/concurrent/CountDownLatch.md)  
[CyclicBarrier](basic/concurrent/CyclicBarrier.md)  
[LockSupport](basic/concurrent/LockSupport.md)  
Executor ;
ThreadLocalRandom  
LinkedBlockingQueue ;
LinkedBlockingDeque ;
Lock ;
Phaser ;
PriorityBlockingQueue ;
ReadWriteLock ;
SynchronousQueue ;
RecursiveAction ;
RecursiveTask ;
ForkJoinPool ;
ForkJoinTask ;
ForkJoinWorkerThread ;
CompletableFuture ;
CompletionStage ;
CountedCompleter ;
FutureTask ;
RunnableFuture ;
RunnableScheduledFuture ;
ScheduledFuture ;

### 参考  
http://tutorials.jenkov.com/java-util-concurrent/index.html  
https://www.ibm.com/developerworks/cn/java/j-lo-taskschedule/  
http://www.blogjava.net/xylz/archive/2010/07/08/325587.html  
http://wangkuiwu.github.io/2012/08/15/juc-executor01/  
https://blog.csdn.net/tianshi_kco/article/details/52960743  
http://cmsblogs.com/?cat=97  
http://www.cnblogs.com/paddix/p/5374810.html  
https://blog.csdn.net/javazejian  
https://juejin.im/post/5c37377351882525ec200f9e  
https://juejin.im/entry/5c516476f265da61736aab3e  

从CPU Cache出发彻底弄懂volatile/synchronized/cas机制  
https://juejin.im/post/5c6b99e66fb9a049d51a1094  

并发与多线程基础  
https://juejin.im/post/5c6b999d6fb9a049fb443a34  
