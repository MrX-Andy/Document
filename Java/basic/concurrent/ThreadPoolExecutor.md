### ThreadPoolExecutor
ThreadPoolExecutor extends AbstractExecutorService  
AbstractExecutorService implements ExecutorService  

为什么要引入线程池呢?  
如果并发的线程数量很多,  并且每个线程执行时间很短就结束了, 这样频繁创建线程就会大大降低系统的效率, 因为频繁创建线程和销毁线程需要耗时;  

合理利用线程池能够带来的好处:  
1.. 降低资源消耗, 通过重复利用已创建的线程降低线程创建和销毁造成的消耗;  
2.. 提高响应速度, 当新的任务到达时, 可以不需要等待线程创建, 就能立即执行;  
3.. 提高线程的可管理性, 线程是稀缺资源, 如果无限制的创建, 不仅会消耗系统资源, 还会降低系统的稳定性, 使用线程池可以进行统一的分配, 调优和监控;  


❀ 构造函数;  参数介绍;  线程池实现原理;  
[链接](tpe_mechanism.md)   


### ThreadPoolExecutor#API  
ThreadPoolExecutor 的重要常量  
```
private final BlockingQueue<Runnable> workQueue;  //  任务队列, 用来存放等待执行的任务  
//  线程池的主要状态锁, 对线程池状态, 比如线程池大小 , runState 等的改变都要使用这个锁  
private final ReentrantLock mainLock = new ReentrantLock();  
private final HashSet<Worker> workers = new HashSet<Worker>();  //  正在被执行的Worker任务集    
private volatile long  keepAliveTime;    // 线程空闲时间     
private volatile boolean allowCoreThreadTimeOut;   //是否允许为核心线程设置存活时间  
private volatile int   corePoolSize;  // 核心池的大小  
private volatile int   maximumPoolSize;   // 线程池最大能容忍的线程数  
private volatile int   poolSize;  // 线程池中当前的线程数  
private volatile RejectedExecutionHandler handler;  //  任务拒绝策略  
private volatile ThreadFactory threadFactory;   //  线程工厂，用来创建线程  
private int largestPoolSize;   //  用来记录线程池中曾经出现过的最大线程数   
private long completedTaskCount;   //  用来记录已经执行完毕的任务个数  
private int largestPoolSize;  //  用于记录线程池曾经达到过的最大并发量, 理论上小于等于 maximumPoolSize  
private long completedTaskCount;   //  线程池运行到当前完成的任务数总和  
```
ThreadPoolExecutor 的重要方法    
```
// 基于完成执行给定 Runnable 所调用的方法。
protected void afterExecute(Runnable r, Throwable t)
// 如果在保持活动时间内没有任务到达，新任务到达时正在替换（如果需要），则设置控制核心线程是超时还是终止的策略。
void allowCoreThreadTimeOut(boolean value)
// 如果此池允许核心线程超时和终止，如果在 keepAlive 时间内没有任务到达，新任务到达时正在替换（如果需要），则返回 true。
boolean allowsCoreThreadTimeOut()
// 请求关闭、发生超时或者当前线程中断，无论哪一个首先发生之后，都将导致阻塞，直到所有任务完成执行。
boolean awaitTermination(long timeout, TimeUnit unit)
// 在执行给定线程中的给定 Runnable 之前调用的方法。
protected void beforeExecute(Thread t, Runnable r)
// 在将来某个时间执行给定任务。
void execute(Runnable command)
// 当不再引用此执行程序时，调用 shutdown。
protected void finalize()
// 返回主动执行任务的近似线程数。
int getActiveCount()
// 返回已完成执行的近似任务总数。
long getCompletedTaskCount()
// 返回核心线程数。
int getCorePoolSize()
// 返回线程保持活动的时间，该时间就是超过核心池大小的线程可以在终止前保持空闲的时间值。
long getKeepAliveTime(TimeUnit unit)
// 返回曾经同时位于池中的最大线程数。
int getLargestPoolSize()
// 返回允许的最大线程数。
int getMaximumPoolSize()
// 返回池中的当前线程数。
int getPoolSize()
// 返回此执行程序使用的任务队列。
BlockingQueue<Runnable> getQueue()
// 返回用于未执行任务的当前处理程序。
RejectedExecutionHandler getRejectedExecutionHandler()
// 返回曾计划执行的近似任务总数。
long getTaskCount()
// 返回用于创建新线程的线程工厂。
ThreadFactory getThreadFactory()
// 如果此执行程序已关闭，则返回 true。
boolean isShutdown()
// 如果关闭后所有任务都已完成，则返回 true。
boolean isTerminated()
// 如果此执行程序处于在 shutdown 或 shutdownNow 之后正在终止但尚未完全终止的过程中，则返回 true。
boolean isTerminating()
// 启动所有核心线程，使其处于等待工作的空闲状态。
int prestartAllCoreThreads()
// 启动一个核心线程，使其处于等待工作的空闲状态。
boolean prestartCoreThread()
// 尝试从工作队列移除所有已取消的 Future 任务。
void purge()
// 从执行程序的内部队列中移除此任务（如果存在），从而如果尚未开始，则其不再运行。
boolean remove(Runnable task)
// 设置核心线程数。
void setCorePoolSize(int corePoolSize)
// 设置线程在终止前可以保持空闲的时间限制。
void setKeepAliveTime(long time, TimeUnit unit)
// 设置允许的最大线程数。
void setMaximumPoolSize(int maximumPoolSize)
// 设置用于未执行任务的新处理程序。
void setRejectedExecutionHandler(RejectedExecutionHandler handler)
// 设置用于创建新线程的线程工厂。
void setThreadFactory(ThreadFactory threadFactory)
// 按过去执行已提交任务的顺序发起一个有序的关闭，但是不接受新任务。
void shutdown()
// 尝试停止所有的活动执行任务、暂停等待任务的处理，并返回等待执行的任务列表。
List<Runnable> shutdownNow()
// 当 Executor 已经终止时调用的方法。
protected void terminated()
```
#### BlockingQueue  
插入  offer  特殊值;  
插入  put     阻塞;  
插入  offer(e, time, unit)  超时;   

移除  poll   特殊值;  
移除  take  阻塞;  
移除  poll(time, unit)  阻塞;  



### SynchronousQueue#Api  
SynchronousQueue 是无界的，是一种无缓冲的等待队列，但是由于该Queue本身的特性，在某次添加元素后必须等待其他线程取走后才能继续添加；  
可以认为SynchronousQueue是一个缓存值为1的阻塞队列，但是 isEmpty()方法永远返回是true，  
remainingCapacity() 方法永远返回是0，remove()和removeAll() 方法永远返回是false，iterator()方法永远返回空，peek()方法永远返回null。    
声明一个SynchronousQueue有两种不同的方式，它们之间有着不太一样的行为。  
公平模式和非公平模式的区别:   
如果采用公平模式， SynchronousQueue会采用公平锁，并配合一个FIFO队列来阻塞多余的生产者和消费者，从而体系整体的公平策略；    
如果是非公平模式，SynchronousQueue默认为非公平模式，SynchronousQueue采用非公平锁，同时配合一个LIFO队列来管理多余的生产者和消费者，  
    如果生产者和消费者的处理速度有差距，则很容易出现饥渴的情况，即可能有某些生产者或者是消费者的数据永远都得不到处理。  
SynchronousQueue是这样 一种阻塞队列，其中每个 put 必须等待一个 take，反之亦然。同步队列没有任何内部容量，甚至连一个队列的容量都没有。  
Fifo通常可以支持更大的吞吐量，但Lifo可以更大程度的保持线程的本地化。  
 
1.. 它一种阻塞队列，其中每个 put 必须等待一个 take，反之亦然。同步队列没有任何内部容量，甚至连一个队列的容量都没有。    
2.. 它是线程安全的，是阻塞的。  
3.. 不允许使用 null 元素。  
4.. 公平排序策略是指调用put的线程之间，或take的线程之间。  

API 比较特别  
```
1.. iterator() 永远返回空, 因为里面没东西;  
2.. peek() 永远返回null;  
3.. put() 往queue放进去一个element以后就一直wait直到有其他thread进来把这个element取走。  
4.. offer() 往queue里放一个element后立即返回，如果碰巧这个element被另一个thread取走了，offer方法返回true，认为offer成功；否则返回false。   
    offer(2000, TimeUnit.SECONDS) 往queue里放一个element但是等待指定的时间后才返回，返回的逻辑和offer()方法一样。    
5.. take() 取出并且remove掉queue里的element（认为是在queue里的。。。），取不到东西他会一直等。  
6.. poll() 取出并且remove掉queue里的element（认为是在queue里的。。。），只有到碰巧另外一个线程正在往queue里offer数据或者put数据的时候，该方法才会取到东西。否则立即返回null。  
    poll(2000, TimeUnit.SECONDS) 等待指定的时间然后取出并且remove掉queue里的element,其实就是再等其他的thread来往里塞。  
7.. isEmpty()永远是true。  
8.. remainingCapacity() 永远是0。   
9.. remove()和removeAll() 永远是false。  
```
### 参考  
http://www.importnew.com/19011.html  
https://blog.csdn.net/tianshi_kco/article/details/53026179    
https://blog.csdn.net/qq_25806863/article/details/71126867  
https://www.cnblogs.com/trust-freedom/p/6594270.html  
http://www.cnblogs.com/trust-freedom/p/6681948.html  
http://www.cnblogs.com/trust-freedom/p/6693601.html  
https://hk.saowen.com/a/fd724624ba3693d3d28aa23bb45df5ed10ae1949e523d4c8516c7fd148949a57  
http://ifeve.com/java-synchronousqueue  

