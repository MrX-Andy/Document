### OKHttp3线程池模型  
同一个 host 默认只能同时执行5个,  最多可以同时执行64个;  
如果有超限, 会加入 readyAsyncCalls 队列中, 排队等待;  
    如果有任务执行结束, 会继续回调, 继续判断当前执行的任务, 把排队的任务 拉到 runningAsyncCalls , 并执行任务;  
如果无超限, 则会加入  runningAsyncCalls 并执行任务;  
同步请求  
```
synchronized void executed(RealCall call) {
    runningSyncCalls.add(call);
}
```
为什么同步请求, 还要加入队列中, 为了 cancel 时,  统一管理    
okhttp3.RealCall#enqueue  
okhttp3.Dispatcher#enqueue  
```
synchronized void enqueue(AsyncCall call) {
    //  并发数量不能超过64 个;
    //  同一个 host 下, 并发数量不能超过5个;  
    if (runningAsyncCalls.size() < maxRequests && runningCallsForHost(call) < maxRequestsPerHost) {
      runningAsyncCalls.add(call);
      executorService().execute(call);
    } else {
      readyAsyncCalls.add(call);
    }
}
```  
#### 1.. 并发数量超限, 等待  
在 okhttp3.RealCall.AsyncCall#execute 方法体中, 如果有任务执行结束  
就会回调  client.dispatcher().finished(this);   
```
void finished(AsyncCall call) {
    finished(runningAsyncCalls, call, true);
}

private <T> void finished(Deque<T> calls, T call, boolean promoteCalls) {
    synchronized (this) {
    //  回调  promoteCalls 方法  
        if (promoteCalls) promoteCalls();
    
    }
}

private void promoteCalls() {
    
    for (Iterator<AsyncCall> i = readyAsyncCalls.iterator(); i.hasNext(); ) {
      AsyncCall call = i.next();

      if (runningCallsForHost(call) < maxRequestsPerHost) {
        i.remove();
        runningAsyncCalls.add(call);
        //  从队列中取出 一个task, 并执行
        executorService().execute(call);
      }
    }
}
```
#### 2.. 线程池模型  
okhttp3.Dispatcher#executorService  

```
public synchronized ExecutorService executorService() {
    if (executorService == null) {
        //  
        executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
        new SynchronousQueue<Runnable>(), Util.threadFactory("OkHttp Dispatcher", false));
    }
    return executorService;
}
```
[ThreadPoolExecutor](/Java/basic/concurrent/ThreadPoolExecutor.md)   

corePoolSize: 最小并发线程数, 这里并发同时包括空闲与活动的线程, 如果是0的话, 空闲一段时间后所有线程将全部被销毁   
maximumPoolSize: 最大线程数, 当任务进来时可以扩充的线程最大值, 当大于了这个值就会根据丢弃处理机制来处理  
keepAliveTime: 当线程数大于corePoolSize时, 多余的空闲线程的最大存活时间, 类似于HTTP中的Keep-alive  

可以看出, 在Okhttp中, 构建了一个阀值为[0, Integer.MAX_VALUE]的线程池, 它不保留任何最小线程数, 随时创建更多的线程数, 当线程空闲时只能活60秒,   
它使用了一个不存储元素的阻塞工作队列, 一个叫做"OkHttp Dispatcher"的线程工厂。  
也就是说, 在实际运行中, 当收到10个并发请求时, 线程池会创建十个线程, 当工作完成后, 线程池会在60s后相继关闭所有线程。  

OkHttp最出彩的地方就是在try/finally中调用了finished函数, 可以主动控制等待队列的移动, 而不是采用锁或者wait/notify, 极大减少了编码复杂性  


SynchronousQueue每个插入操作必须等待另一个线程的移除操作, 同样任何一个移除操作都等待另一个线程的插入操作。  
因此队列内部其实没有任何一个元素, 或者说容量为0, 严格说并不是一种容器, 由于队列没有容量, 因此不能调用peek等操作, 因此只有移除元素才有元素,   
显然这是一种快速传递元素的方式, 也就是说在这种情况下元素总是以最快的方式从插入者(生产者)传递给移除者(消费者),这在多任务队列中最快的处理任务方式。  
对于高频请求场景, 无疑是最合适的。

在OKHttp中, 创建了一个阀值是Integer.MAX_VALUE的线程池, 它不保留任何最小线程, 随时创建更多的线程数, 而且如果线程空闲后, 只能多活60秒。  
所以也就说如果收到20个并发请求, 线程池会创建20个线程, 当完成后的60秒后会自动关闭所有20个线程。  
他这样设计成不设上限的线程, 以保证I/O任务中高阻塞低占用的过程, 不会长时间卡在阻塞上。  


