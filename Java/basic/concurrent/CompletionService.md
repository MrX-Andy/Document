### CompletionService  
#### submit  
1. 根据 Executor 构建一个 completionService；  
2. 一次性，通过completionService.submit 函数，把所有的Callable添加到线程池；  
3. 通过 completionService.take()得到Future， 在通过future.get 得到最终的result；  
4. 达到最终的并发的效果，再所有的任务结束时，才会继续向下执行；  

```
ThreadPoolExecutor poolExecutor = ThreadUtil.newExecutor();
ExecutorCompletionService<String> completionService = new ExecutorCompletionService<>(poolExecutor);
LogTrack.w("submit = " + TimeUtil.currentTimeMillis());
for (int i = 0; i < RandTimeArray.length; i++) {
    completionService.submit(new NameCallable(RandTimeArray[i], i + ""));
}
LogTrack.w("take = " + TimeUtil.currentTimeMillis());
Arrays.stream(RandTimeArray).forEachOrdered(index -> {
    String result = ThreadUtil.takeGet(completionService, "");
    LogTrack.w("index = " + index + " result = " + result);
});

LogTrack.w("上面几个任务，并发执行，所有的任务都结束了");
poolExecutor.shutdown();
```
我们知道简单的Future的缺点就是，各个Callable是串行运行的，是对CPU资源的浪费；  使用 CompletionService可以使多个Callable并行运行；  

