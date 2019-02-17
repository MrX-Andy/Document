### API  
```
// 使用给定核心池大小创建一个新 ScheduledThreadPoolExecutor。
ScheduledThreadPoolExecutor(int corePoolSize)
// 使用给定初始参数创建一个新 ScheduledThreadPoolExecutor。
ScheduledThreadPoolExecutor(int corePoolSize, RejectedExecutionHandler handler)
// 使用给定的初始参数创建一个新 ScheduledThreadPoolExecutor。
ScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory)
// 使用给定初始参数创建一个新 ScheduledThreadPoolExecutor。
ScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler)

// 修改或替换用于执行 callable 的任务。
protected <V> RunnableScheduledFuture<V> decorateTask(Callable<V> callable, RunnableScheduledFuture<V> task)
// 修改或替换用于执行 runnable 的任务。
protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable, RunnableScheduledFuture<V> task)
// 使用所要求的零延迟执行命令。
void execute(Runnable command)
// 获取有关在此执行程序已 shutdown 的情况下、是否继续执行现有定期任务的策略。
boolean getContinueExistingPeriodicTasksAfterShutdownPolicy()
// 获取有关在此执行程序已 shutdown 的情况下是否继续执行现有延迟任务的策略。
boolean getExecuteExistingDelayedTasksAfterShutdownPolicy()
// 返回此执行程序使用的任务队列。
BlockingQueue<Runnable> getQueue()
// 从执行程序的内部队列中移除此任务（如果存在），从而如果尚未开始，则其不再运行。
boolean remove(Runnable task)
// 创建并执行在给定延迟后启用的 ScheduledFuture。
<V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit)
// 创建并执行在给定延迟后启用的一次性操作。
ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit)
// 创建并执行一个在给定初始延迟后首次启用的定期操作，后续操作具有给定的周期；也就是将在 initialDelay 后开始执行，然后在 initialDelay+period 后执行，接着在 initialDelay + 2 * period 后执行，依此类推。
ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)
// 创建并执行一个在给定初始延迟后首次启用的定期操作，随后，在每一次执行终止和下一次执行开始之间都存在给定的延迟。
ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit)
// 设置有关在此执行程序已 shutdown 的情况下是否继续执行现有定期任务的策略。
void setContinueExistingPeriodicTasksAfterShutdownPolicy(boolean value)
// 设置有关在此执行程序已 shutdown 的情况下是否继续执行现有延迟任务的策略。
void setExecuteExistingDelayedTasksAfterShutdownPolicy(boolean value)
// 在以前已提交任务的执行中发起一个有序的关闭，但是不接受新任务。
void shutdown()
// 尝试停止所有正在执行的任务、暂停等待任务的处理，并返回等待执行的任务列表。
List<Runnable> shutdownNow()
// 提交一个返回值的任务用于执行，返回一个表示任务的未决结果的 Future。
<T> Future<T> submit(Callable<T> task)
// 提交一个 Runnable 任务用于执行，并返回一个表示该任务的 Future。
Future<?> submit(Runnable task)
// 提交一个 Runnable 任务用于执行，并返回一个表示该任务的 Future。
<T> Future<T> submit(Runnable task, T result)

```