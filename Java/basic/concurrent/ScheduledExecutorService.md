### ScheduledExecutorService  
interface ScheduledExecutorService extends ExecutorService  

#### schedule  
schedule(callable, delay, timeUnit);    
先delay一段时间，再 执行任务，且只执行一次；   
```
ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
ScheduledFuture<String> schedule = executorService.schedule(new LocalCallable(2000), 1000, TimeUnit.MILLISECONDS);
LogTrack.w(TimeUtil.currentTimeMillis());
LogTrack.w(ThreadUtil.get(schedule, ""));
executorService.shutdown();
```
虽然是并发执行，但是必须 schedule0结束，才能收到schedule1的结果；  
#### scheduleAtFixedRate  
scheduleAtFixedRate(runnable, initialDelay, period, timeUnit);  
initialDelay：初始化延时；  
period：两次开始执行最小间隔时间；  
每次执行时间为： initialDelay、  initialDelay+period、  initialDelay+2*period、  ...  
是基于固定时间间隔进行任务调度；  
两次 轮询只间隔 period，不管上次有没有执行完；    
```
ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);
ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(new LocalRunnable(1500), 0, 3000, TimeUnit.MILLISECONDS);
```
#### scheduleWithFixedDelay  
scheduleWithFixedDelay(runnable, initialDelay, delay, timeUnit);  
initialDelay：初始化延时；  
period：前一次执行结束到下一次执行开始的间隔时间（间隔执行延迟时间）；  
每次执行时间为： initialDelay、  initialDelay+executeTime+delay、  initialDelay+2*executeTime+2*delay、  ...  
取决于每次任务执行的时间长短，是基于不固定时间间隔进行任务调度；  
先执行完上一次任务，再间隔period，再执行下一次轮询；  
```
ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
ScheduledFuture<?> scheduledFuture = executorService.scheduleWithFixedDelay(new LocalRunnable(2000), 3000, 1000, TimeUnit.MILLISECONDS);
LogTrack.w(TimeUtil.currentTimeMillis());
LogTrack.w(ThreadUtil.getObj(scheduledFuture, ""));
executorService.shutdown();
```
