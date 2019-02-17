### ExecutorCompletionService  
submit take get  
```
public class Test02 {
    public static void main(String[] args) {

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
    }
}
```