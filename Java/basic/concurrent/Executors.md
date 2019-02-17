### Executors  
Executors.newCachedThreadPool();  
创建一个缓冲池，缓冲池容量大小为Integer.MAX_VALUE，如果缓存中有可用的线程，则不会新建线程；     

Executors.newFixedThreadPool(int);    
创建固定容量大小的缓冲池，超出的线程会在队列中等待。  

Executors.newScheduledThreadPool();    
创建一个定长线程池，支持定时及周期性任务执行。  

Executors.newSingleThreadExecutor();     
创建容量为1的缓冲池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。    


##### API  
```
// 返回 Callable 对象，调用它时可运行给定特权的操作并返回其结果。
static Callable<Object> callable(PrivilegedAction<?> action)
// 返回 Callable 对象，调用它时可运行给定特权的异常操作并返回其结果。
static Callable<Object> callable(PrivilegedExceptionAction<?> action)
// 返回 Callable 对象，调用它时可运行给定的任务并返回 null。
static Callable<Object> callable(Runnable task)
// 返回 Callable 对象，调用它时可运行给定的任务并返回给定的结果。
static <T> Callable<T> callable(Runnable task, T result)
// 返回 Callable 对象，调用它时可在当前的访问控制上下文中执行给定的 callable 对象。
static <T> Callable<T> privilegedCallable(Callable<T> callable)
// 返回 Callable 对象，调用它时可在当前的访问控制上下文中，使用当前上下文类加载器作为上下文类加载器来执行给定的 callable 对象。
static <T> Callable<T> privilegedCallableUsingCurrentClassLoader(Callable<T> callable)
// 返回用于创建新线程的线程工厂，这些新线程与当前线程具有相同的权限。
static ThreadFactory privilegedThreadFactory()
// 返回一个将所有已定义的 ExecutorService 方法委托给指定执行程序的对象，但是使用强制转换可能无法访问其他方法。
static ExecutorService unconfigurableExecutorService(ExecutorService executor)
// 返回一个将所有已定义的 ExecutorService 方法委托给指定执行程序的对象，但是使用强制转换可能无法访问其他方法。
static ScheduledExecutorService unconfigurableScheduledExecutorService(ScheduledExecutorService executor)
```