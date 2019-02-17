### 参数列表    

corePoolSize:  
核心池的大小;
默认情况下, 在创建了线程池后, 线程池中的线程数为0;  
线程池中并没有任何线程, 而是等待有任务到来才创建线程去执行任务;  
除非调用了 preStartAllCoreThreads 或者 preStartCoreThread 方法, 来创建线程;  
当有任务来之后, 就会创建新的, 线程去执行任务, 当线程池中的线程数目达到 corePoolSize 后, 就会把任务放到阻塞队列当中;   
核心线程在 allowCoreThreadTimeout 为true 时, 会超时退出, 默认情况下不会退出;  

maximumPoolSize:  
线程池最大线程数, 它表示在线程池中最多能创建多少个线程;  

keepAliveTime:  
默认情况下, 存活时间只会控制, 非核心线程, 即空闲超时, 就会退出 并 被清理掉;  
默认情况下, 核心线程在超时之后, 仍然处于空闲等待状态, 而不会停止工作;  

表示线程没有任务执行时最多保持多久时间会终止;  
即当线程池中的线程数大于 corePoolSize 时, 如果一个线程空闲的时间达到 keepAliveTime, 则会终止, 直到线程池中的线程数不超过 corePoolSize;  
但是如果调用了allowCoreThreadTimeOut(boolean)方法, 在线程池中的线程数不大于 corePoolSize时, keepAliveTime 参数也会起作用, 直到线程池中的线程数为 0;  

unit:  
参数 keepAliveTime的时间单位, 有7种取值, 在 TimeUnit 类中有7种静态属性:
```
TimeUnit.DAYS;               //  天  
TimeUnit.HOURS;             //  小时  
TimeUnit.MINUTES;           //  分钟  
TimeUnit.SECONDS;           //  秒  
TimeUnit.MILLISECONDS;      //  毫秒  
TimeUnit.MICROSECONDS;      //  微妙  
TimeUnit.NANOSECONDS;       //  纳秒  
```

### workQueue:  
一个阻塞队列, 用来存储等待执行的任务, 这个参数的选择也很重要, 会对线程池的运行过程产生重大影响;  
一般来说, 这里的阻塞队列有以下几种选择:  
ArrayBlockingQueue:  基于数组的先进先出队列, 此队列创建时必须指定大小;  
DelayQueue: 
LinkedBlockingQueue:  基于链表的先进先出队列, 默认队列大小是 Integer.MAX_VALUE;  
SynchronousQueue:  内部仅允许容纳一个元素, 这个队列比较特殊, 它不会保存提交的任务, 而是将直接新建一个线程来执行新来的任务;  
PriorityBlockingQueue   
一般使用 LinkedBlockingQueue 和 SynchronousQueue, 线程池的排队策略与 BlockingQueue 有关;  


❀ 直接切换:  
常用的队列是 SynchronousQueue (同步队列), 这种队列内部不会存储元素;  
每一次插入操作都会先进入阻塞状态, 一直等到另一个线程执行了删除操作, 然后该插入操作才会执行;  
同样地, 每一次删除操作也都会先进入阻塞状态, 一直等到另一个线程执行了插入操作, 然后该删除操作才会执行;  

当提交一个任务到包含这种 SynchronousQueue 队列的线程池以后,  
线程池会去检测, 核心池是否有可用的空闲线程, 来执行该任务, 如果没有就直接新建一个线程, 来执行该任务, 而不是将该任务先暂存在队列中;  
这种策略适合用来处理, 多个有相互依赖关系的任务, 因为该策略可以避免, 这些任务因一个没有及时处理, 而导致依赖于该任务, 的其他任务也不能及时处理而造成的锁定效果;  
因为这种策略的目的是要让几乎每一个新提交的任务都能得到立即处理, 所以这种策略通常要求最大线程数 maximumPoolSizes 是无界的(即: Integer.MAX_VALUE);   
静态工厂方法 Executors.newCachedThreadPool() 使用了这个队列;  

❀ 使用无界队列:  
也就是不预设队列的容量, 基于链表的阻塞队列;  
使用无界队列, 将使得线程池中能够创建的最大线程数, 等于核心线程数 corePoolSize, 这样线程池的 maximumPoolSize 的数值起不到任何作用;  
如果向这种线程池中提交一个新任务时发现所有核心线程都处于运行状态;  
那么该任务将被放入无界队列中等待处理;  
当要处理的多个任务之间, 没有任何相互依赖关系时, 就适合使用这种队列策略, 来处理这些任务;   
静态工厂方法 Executors.newFixedThreadPool() 使用了这个队列;  

❀ 使用有界队列:  
基于数组的阻塞队列;  
线程池使用有界队列能够降低资源的消耗, 但这也使得线程池对线程的调控变得更加困难;  
因为队列容量和线程池容量都是有限的值, 要想使线程处理任务的吞吐量能够在一个相对合理的范围内,   
同时又能使线程调配的难度相对较低, 并且又尽可能节省系统资源的消耗, 那么就需要合理地调配这两个数值;  
通常来说, 设置较大的队列容量和较小的线程池容量, 能够降低系统资源的消耗, 但却会降低线程处理任务的吞吐量;  
如果发现提交的任务经常频繁地发生阻塞的情况, 那么你就可以考虑增大线程池的容量, 可以通过调用 setMaximumPoolSize() 方法来重新设定线程池的容量.  
而设置较小的队列容量时, 通常需要将线程池的容量设置大一点, 这种情况下, CPU的使用率会相对较高,   
当然如果线程池的容量设置过大的话, 可能会有非常非常多的线程, 来同时处理提交来的多个任务,   
并发数过大时, 线程之间的调度将会是个非常严峻的问题, 这反而有可能降低任务处理的吞吐量;  

threadFactory:  
线程工厂, 主要用来创建线程;  

handler:  
表示当拒绝处理任务时的策略, 有以下四种取值:  
ThreadPoolExecutor.AbortPolicy: 当任务添加到线程池中被拒绝时, 它将抛出 RejectedExecutionException 异常;  
ThreadPoolExecutor.DiscardPolicy: 当任务添加到线程池中被拒绝时, 线程池将丢弃被拒绝的任务, 但是不抛出异常;  
ThreadPoolExecutor.DiscardOldestPolicy: 当任务添加到线程池中被拒绝时, 丢弃队列最前面的任务, 然后重新尝试执行任务(重复此过程);  
ThreadPoolExecutor.CallerRunsPolicy: 当任务添加到线程池中被拒绝时, 交给当前线程池处理;  

### 参数讲解  
处理任务的优先级为: 核心线程 corePoolSize, 阻塞队列 workQueue, 最大线程 maximumPoolSize;  
如果三者都满了, 使用 handler 处理被拒绝的任务;  
1.. 如果线程数量 <= 核心线程数, 那么直接创建新的线程, 来执行任务, 不会放入阻塞队列中;  
      核心线程在 allowCoreThreadTimeout 为true 时, 会超时退出, 默认情况下不会退出;  
2.. 如果线程数量 > 核心线程数, 但是 <= 最大线程数, 并且阻塞队列是 LinkedBlockingDeque 的时候, 超过核心线程数量, 的任务, 会放在阻塞队列中排队;  
3.. 如果线程数量 > 核心线程数, 但是 <= 最大线程数, 并且阻塞队列是 SynchronousQueue 的时候, 线程池会创建, 新的线程, 来执行任务, 这些任务也不会被放在阻塞队列中;  
      这些线程属于非核心线程, 在任务完成后, 闲置时间, 达到了超时时间, 就会被清除;  
4.. 如果线程数量 > 核心线程数, 并且 > 最大线程数, 当阻塞队列是 LinkedBlockingDeque, 会将超过核心线程数量, 的任务, 放在阻塞队列中排队;   
      当阻塞队列 LinkedBlockingDeque 没有大小限制时, 线程池的最大线程数设置是无效的, 他的线程数最多不会超过核心线程数;  
      当阻塞队列 LinkedBlockingDeque 有大小限制时, 当阻塞队列塞满时, 新增的任务, 会直接创建新的线程, 来执行任务, 当创建的线程数量超过, 最大线程数量时会抛异常;   
5.. 如果线程数量 > 核心线程数, 并且 > 最大线程数, 当阻塞队列是 SynchronousQueue 时, 会因为线程池拒绝添加任务而抛出异常;  


参数经验值:  

corePoolSize = 每秒需要多少个线程处理?  
threadcount = tasks/(1/taskcost) =tasks*taskcout = (500~1000)*0.1 = 50~100 个线程。corePoolSize设置应该大于50   
根据 8020 原则，如果 80% 的每秒任务数小于 800, 那么 corePoolSize设置为 80 即可   
如果是 CPU 密集型任务, 就需要尽量压榨  CPU, 参考值可以设为  NCPU+1;  
如果是 IO 密集型任务, 参考值可以设置为  2*NCPU;  
其中 NCPU 的指的是 CPU 的核心数, 可以使用 Runtime.getRuntime().availableProcessors() 来获取;  

maxPoolSize = ?  
maxPoolSize = (max(tasks)- queueCapacity)/(1/taskcost)   
计算可得 maxPoolSize = (1000-80)/10 = 92   

keepAliveTime = ?  
如果要执行的任务相对较多, 并且每个任务执行的时间比较短, 那么可以为该参数设置一个相对较大的数值, 以提高线程的利用率;  
如果任务比较少, 超时时间应该相对较短, 通过超时停止的机制来降低系统线程资源的开销;  

### 线程的复用 + 超时清理;  
对于线程池中的线程, 无论是核心线程, 还是非核心线程, 在run方法中, 执行完 runnable任务的时候, 都会到 阻塞队列中, 取任务;  
如果是非核心线程, 是阻塞式 + 超时, 取任务, 如果取不到任务, 非核心线程 runWork 方法运行结束, 线程结束;  
如果是核心线程, 也是阻塞式取任务, 但是如果取不到任务, 核心线程会一直等待, 直到有 Runnable 任务插入, 并唤醒等待中的线程, 然后核心线程拿到新的任务, 去执行;  
也就是说, 核心线程会一直在线程池, 有任务就执行, 没有任务就等待, 直到被新的任务唤醒;  
如果, 任务较多, 会启用阻塞队列 和 非核心线程;  

线程池的实现思路是, 线程池里的每个线程, 执行完任务后不立刻退出, 而是去检查阻塞队列中, 是否还有线程任务需要执行,   
如果在 keepAliveTime 里等不到新的任务了, 那么线程就会退出;  
线程池在执行 Runnable 任务的时候, 并不单纯把 Runnable 任务, 交给一个新建的 Thread, 而是把它封装成 Worker 任务;  
Worker 作为 Runnable 的代理, 所以看 Worker 的run 方法:  
```
while (task != null || (task = getTask()) != null)  
```
在运行完 firstTask 之后, 并不会立刻结束, 而是会调用 getTask 获取新的任务, 也就是从阻塞队列中, 取出来新的任务, 继续执行;  
取任务的过程是阻塞式的, 如果在 keepAliveTime 内没有新的任务, 则会退出;  
keepAliveTime 的实现机制, 是 workQueue 提供的:  
```
workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS);  
```
例如 LinkedBlockQueue 是通过 Condition#awaitNanos 实现的:  
```
//  private final Condition notEmpty = takeLock.newCondition();
notEmpty.awaitNanos(nanos);  
```

### blockingQueue#take  
阻塞取消息;  

### 线程池实现原理  

1.. 线程池状态  
RUNNING (运行状态):  
能接受新提交的任务, 并且也能处理阻塞队列中的任务;  
SHUTDOWN (关闭状态):  
在线程池处于 RUNNING 状态时, 调用 shutdown()方法会使线程池进入到该状态;   
当然, finalize() 方法在执行过程中或许也会隐式地进入该状态;  
不再接受新提交的任务, 但却可以继续处理阻塞队列中已保存的任务;  
STOP :  
在线程池处于 RUNNING 或 SHUTDOWN 状态时, 调用 shutdownNow() 方法会使线程池进入到该状态;  
不能接受新提交的任务, 也不能处理阻塞队列中已保存的任务, 并且会中断正在处理中的任务;  
TIDYING (清理状态):  
当线程池处于 SHUTDOWN 状态时, 如果此后线程池内没有线程了, 并且阻塞队列内也没有待执行的任务了, 线程池就会进入到该状态;  
当线程池处于 STOP 状态时, 如果此后线程池内没有线程了, 线程池就会进入到该状态;  
线程池进入该状态后会调用 terminated() 方法, 会让该线程池进入TERMINATED 状态;  
所有的任务都已终止了, workerCount (有效线程数)为0;  
TERMINATED:  
terminated() 方法执行完后就进入该状态;  

### ThreadPoolExecutor#execute  
```
public void execute(Runnable command) {
        if (command == null)
            throw new NullPointerException();
        int c = ctl.get();
        //  如果线程池 数量, 小于核心池数量;  
        if (workerCountOf(c) < corePoolSize) {
            // 如果新添加一个任务, 线程池数量依然小于核心池的数量, 则整个 execute 调用结束;  
            
            //  如果新添加一个任务, 线程池数量 >= 核心池数量;  
            //  如果此时线程池已经关闭;  
            //  则继续往下执行;  
            if (addWorker(command, true))
                return;
            //  再次获取 ctl的值
            c = ctl.get();
        }
        //  如果线程池处于RUNNING(运行)状态并且线程池内的阻塞队列 workQueue未满; 
        if (isRunning(c) && workQueue.offer(command)) {
            //  再次获取 ctl的值
            int recheck = ctl.get();
            //  再次判断线程池此时的运行状态, 如果发现线程池未处于 RUNNING(运行)状态;
            //  由于先前已将任务 command加入到阻塞队列 workQueue中了, 所以需要, 将该任务从 workQueue 中移除;
            //  一般来说, 该移除操作都能顺利进行, 所以一旦移除成功, 就再调用 handler的 rejectedExecution()方法;  
            //  根据该 handler 定义的拒绝策略, 对该任务进行处理;  
            //  当然, 默认的拒绝策略是 AbortPolicy, 也就是直接抛出 RejectedExecutionException 异常, 同时也结束了整个 execute()方法的执行;
            if (! isRunning(recheck) && remove(command))
                reject(command);
            //  再次计算线程池内的有效线程数 workerCount, 一旦发现该数量变为0  
            //  就将线程池内的线程数上限值设置为最大线程数 maximumPoolSize;  
            //  然后, 只是创建一个线程而不去启动它, 并结束整个 execute()方法的执行; 
            else if (workerCountOf(recheck) == 0)
                addWorker(null, false);
        }
        //  如果线程池未处于RUNNING(运行)状态;  
        
        //  虽然处于RUNNING(运行)状态, 但线程池内的阻塞队列 workQueue已满, 但有效线程数还未达到本次设定的最大线程数, 那么就会尝试创建并启动一个线程来执行任务;  
        //  如果线程的创建或启动失败, 则同样会触发 handler的 rejectedExecution()方法来拒绝该任务, 并结束掉该 execute()方法;  
        else if (!addWorker(command, false))
            reject(command);
    }
```
### ThreadPoolExecutor#addWorker  
```
private boolean addWorker(Runnable firstTask, boolean core) {
    //  retry 是个无限循环;  
    //  当线程池处于 RUNNING (运行)状态时, 只有在线程池中的, 有效线程数被成功加一以后, 才会退出该循环而去执行后边的代码;  
    //  也就是说, 当线程池在 RUNNING (运行)状态下退出该 retry 循环时, 
    //  线程池中的有效线程数, 一定少于此次设定的最大线程数(可能是 corePoolSize 或 maximumPoolSize);
    retry:
    for (;;) {
        int c = ctl.get();
        int rs = runStateOf(c);

        //  线程池满足如下条件中的任意一种时, 就会直接结束该方法, 并且返回 false; 
        //  表示没有创建新线程, 新提交的任务也没有被执行;  
        //  处于 STOP, TYDING 或 TERMINATD 状态;
        //   处于 SHUTDOWN 状态, 并且参数 firstTask != null
        //  处于 SHUTDOWN 状态, 并且阻塞队列 workQueue为空
        // Check if queue empty only if necessary.
        if (rs >= SHUTDOWN &&
            ! (rs == SHUTDOWN &&
               firstTask == null &&
               ! workQueue.isEmpty()))
            return false;

        for (;;) {
            //  如果线程池内的有效线程数大于或等于了理论上的最大容量 CAPACITY , 或者实际设定的最大容量,  就返回 false直接结束该方法;  
            //  这样同样没有创建新线程, 新提交的任务也同样未被执行;  
            int wc = workerCountOf(c);
            if (wc >= CAPACITY ||
                wc >= (core ? corePoolSize : maximumPoolSize))
                return false;
            //  有效线程数加一
            if (compareAndIncrementWorkerCount(c))
                break retry;
            c = ctl.get();  // Re-read ctl
            if (runStateOf(c) != rs)
                continue retry;
            // else CAS failed due to workerCount change; retry inner loop
        }
    }

    //  走到这里说明工作线程数增加成功  
    boolean workerStarted = false;
    boolean workerAdded = false;
    Worker w = null;
    try {
        //  根据参数 firstTask来创建 Worker对象 w.
        w = new Worker(firstTask);
        //  用 w创建线程对象 t.
        final Thread t = w.thread;
        if (t != null) {
            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                // Recheck while holding lock.
                // Back out on ThreadFactory failure or if
                // shut down before lock acquired.
                int rs = runStateOf(ctl.get());

                if (rs < SHUTDOWN ||
                    (rs == SHUTDOWN && firstTask == null)) {
                    if (t.isAlive()) // precheck that t is startable
                        throw new IllegalThreadStateException();
                    workers.add(w);
                    int s = workers.size();
                    if (s > largestPoolSize)
                        largestPoolSize = s;
                    workerAdded = true;
                }
            } finally {
                mainLock.unlock();
            }
            if (workerAdded) {
                //  启动线程 t. 由于 t指向 w.thread所引用的对象, 所以相当于启动的是 w.thread所引用的线程对象.
                //  而 w是 Runnable 的实现类, w.thread 是以 w作为 Runnable参数所创建的一个线程对象, 
                //  所以启动 w.thread所引用的线程对象, 也就是要执行 w 的 run()方法.   
                t.start();
                workerStarted = true;
            }
        }
    } finally {
        if (! workerStarted)
            addWorkerFailed(w);
    }
    return workerStarted;
}
```
### ThreadPoolExecutor#runWorker  
```
final void runWorker(Worker w) {
    Thread wt = Thread.currentThread();
    Runnable task = w.firstTask;
    w.firstTask = null;
    w.unlock(); // allow interrupts
    boolean completedAbruptly = true;
    try {
        //  由前边可知, task 就是 w.firstTask, 如果 task为 null, 那么就不进入该 while循环, 也就不运行该 task
        //  如果 task不为 null, 那么就执行 getTask()方法. 而getTask()方法是个无限循环;  
        //  会从阻塞队列 workQueue中不断取出任务来执行, 当阻塞队列 workQueue 中所有的任务都被取完之后, 就结束下面的while循环;  
        while (task != null || (task = getTask()) != null) {
            w.lock();
            // If pool is stopping, ensure thread is interrupted;
            // if not, ensure thread is not interrupted.  This
            // requires a recheck in second case to deal with
            // shutdownNow race while clearing interrupt
            if ((runStateAtLeast(ctl.get(), STOP) ||
                 (Thread.interrupted() &&
                  runStateAtLeast(ctl.get(), STOP))) &&
                !wt.isInterrupted())
                wt.interrupt();
            try {
                beforeExecute(wt, task);
                Throwable thrown = null;
                try {
                    //  执行从阻塞队列 workQueue中取出的任务.
                    task.run();
                } catch (RuntimeException x) {
                    thrown = x; throw x;
                } catch (Error x) {
                    thrown = x; throw x;
                } catch (Throwable x) {
                    thrown = x; throw new Error(x);
                } finally {
                    afterExecute(task, thrown);
                }
            } finally {
                //  将 task 置为 null, 这样使得 while循环是否继续执行的判断, 就只能依赖于判断;  
                //  第二个条件, 也就是 (task = getTask()) != null 这个条件, 是否满足.
                //  置空 task，准备 getTask 下一个任务
                task = null;
                //  累加完成的任务数, 加一;  
                w.completedTasks++;
                // 释放掉 worker 的独占锁
                w.unlock();
            }
        }
        completedAbruptly = false;
    } finally {
        //  到这里，需要执行线程关闭
        //  说明 getTask 返回 null, 也就是说, 这个 worker 的使命结束了, 执行关闭
        
        processWorkerExit(w, completedAbruptly);
    }
}
```
runnable 任务执行结束, 置空 runnable 任务, 结束 runTask 函数;  

### ThreadPoolExecutor#getTask  
```
private Runnable getTask() {
    boolean timedOut = false; // Did the last poll() time out?

    for (;;) {
        int c = ctl.get();
        int rs = runStateOf(c);

        //  如果线程池已停止, 或者线程池被关闭并且线程池内的阻塞队列为空, 则结束该方法并返回 null.
        // Check if queue empty only if necessary.
        if (rs >= SHUTDOWN && (rs >= STOP || workQueue.isEmpty())) {
            decrementWorkerCount();
            return null;
        }

        int wc = workerCountOf(c);

        //  如果 allowCoreThreadTimeOut 这个字段设置为 true, 也就是允许核心线程受超时机制的控制, 直接设置 timed 为 true;  
        //  反之, 则再看当前线程池中的, 有效线程数是否已经超过了核心线程数, 也就是是否存在非核心线程;  
        //  如果存在非核心线程, 那么也会设置 timed 为true, 
        //  如果 wc <= corePoolSize, 线程池中的有效线程数少于核心线程数, 并且 allowCoreThreadTimeOut 为 false, 那么就设置 timed 为 false;  
         
        // Are workers subject to culling?
        boolean timed = allowCoreThreadTimeOut || wc > corePoolSize;

        //  当线程池处于 RUNNING (运行)状态, 但阻塞队列内已经没有任务(为空)时, 
        //  将导致有线程接下来会一直处于空闲状态;  
        //  如果空闲的是核心线程, 并且设置核心线程不受超时机制的影响, 默认情况下就是这个设置;  
        //  那么这些核心线程, 将一直在线程池中处于空闲状态, 等待着新任务的到来;  
        //  只要线程池处于 RUNNING(运行)状态, 那么, 这些空闲的核心线程将一直在池子中而不会被销毁;  
        //  如果空闲的是非核心线程, 或者 设置了核心线程受超时机制的限制, 那么当空闲达到超时时间时, 那么会进入下面的 if
        //  通过返回 null 结束掉该 getTask()方法, 也最终结束掉 runWorker()方法;  
        
        
        //  从阻塞队列中取出队首的那个任务, 设置给 r. 如果空闲线程等待超时, 或者该队列已经为空, 则 r为 null.
        // 如果阻塞队列不为空并且未发生超时的情况, 那么取出的任务就不为 null, 就直接返回该任务对象; 
        if ((wc > maximumPoolSize || (timed && timedOut))
            && (wc > 1 || workQueue.isEmpty())) {
            if (compareAndDecrementWorkerCount(c))
                return null;
            continue;
        }

        try {
            Runnable r = timed ?
                workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) :
                workQueue.take();
            if (r != null)
                return r;
            timedOut = true;
        } catch (InterruptedException retry) {
            timedOut = false;
        }
    }
}
```
addWorker(null, false);  
这句代码的含义是 "只是创建一个线程而不去启动它";  

### 示例代码  
https://gudong.name/2017/05/03/thread-pool-intro.html  


###  参考  
http://wangkuiwu.github.io/2012/08/15/juc-executor05/    
https://www.cnblogs.com/trust-freedom/p/6681948.html   
https://blog.csdn.net/qq_25806863/article/details/71126867  
https://blog.csdn.net/a12345555555/article/details/77944107  
https://www.cnblogs.com/waytobestcoder/p/5323130.html  
https://singleant.iteye.com/blog/1423931  
https://blog.csdn.net/cleverGump/article/details/50688008  
https://blog.csdn.net/fuyuwei2015/article/details/72758179  
https://www.zhihu.com/question/38128980  
https://cloud.tencent.com/developer/article/1124439  

BlockingQueue  
https://blog.csdn.net/vernonzheng/article/details/8247564  



