### ExecutorService  
interface ExecutorService extends Executor  

#### submit  与 execute  
execute 无返回值， submit 有返回值； 
 
#### submit  
##### 顺序执行  
LogTrack.w(ThreadUtil.submit(executorService.submit(new LocalCallable(2000))));  
LogTrack.w(ThreadUtil.submit(executorService.submit(new LocalCallable(2000))));  
##### 并发执行  
```
for (int randTime : RandTimeArray) {
    futureList.add(executorService.submit(new LocalCallable(randTime)));
}
futureList.forEach(stringFuture -> LogTrack.w(ThreadUtil.submit(stringFuture)));
```

#### 关闭线程池  
shutdown()：不会立即终止线程池，而是要等所有任务缓存队列中的任务都执行完后才终止，但再也不会接受新的任务  
shutdownNow()：立即终止线程池，并尝试打断正在执行的任务，并且清空任务缓存队列，返回尚未执行的任务  

#### invokeAny  
只获取一个最先完成任务的执行结果  
```
ExecutorService executorService = Executors.newCachedThreadPool();
List<Callable<String>> callableList = new ArrayList<>(4);
for (int randTime : RandTimeArray) {
    callableList.add(new NameCallable(randTime));
}
LogTrack.i(TimeUtil.currentTimeMillis());
LogTrack.i(ThreadUtil.invokeAny(executorService, callableList));
executorService.shutdown();
```  
##### 测试表现  
测试发现效果不是很精确的，  A 执行需要耗时  2201ms，B执行需要耗时2200ms；  
ABCDE按顺序加入taskList，结果先拿到A的结果，当A改为耗时2202ms，则先拿到B的结果； 
##### 超时限制  
```
ExecutorService executorService = Executors.newCachedThreadPool();
List<Callable<String>> callableList = new ArrayList<>(4);
for (int i = 0; i < RandTimeArray.length; i++) {
    callableList.add(new NameCallable(RandTimeArray[i], i + ""));
}
LogTrack.i(TimeUtil.currentTimeMillis());
LogTrack.i(ThreadUtil.invokeAny(executorService, callableList, 1, TimeUnit.SECONDS, "timeOut"));
executorService.shutdown();
```

#### invokeAll  
当所有任务完成时，返回  
```
ExecutorService executorService = Executors.newCachedThreadPool();
List<Callable<String>> callableList = new ArrayList<>(4);
for (int randTime : RandTimeArray) {
    callableList.add(new NameCallable(randTime));
}
LogTrack.i(TimeUtil.currentTimeMillis());
List<Future<String>> futureList = ThreadUtil.invokeAll(executorService, callableList);
futureList.forEach(result -> {
    LogTrack.d(ThreadUtil.get(result, ""));
});
executorService.shutdown();
```


#### API  
```
// 请求关闭、发生超时或者当前线程中断，无论哪一个首先发生之后，都将导致阻塞，直到所有任务完成执行。  
boolean awaitTermination(long timeout, TimeUnit unit)  
// 如果此执行程序已关闭，则返回 true。  
boolean isShutdown()  
// 如果关闭后所有任务都已完成，则返回 true。  
boolean isTerminated()  
```
