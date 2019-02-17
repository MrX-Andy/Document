### Future  
[简单示例](Future/sample.md)  
[Future的缺点](Future/shortage.md)  
##### API 
```
// 试图取消对此任务的执行。
boolean     cancel(boolean mayInterruptIfRunning)

// 如果在任务正常完成前将其取消，则返回 true。
boolean     isCancelled()

// 如果任务已完成，则返回 true。
boolean     isDone()

// 如有必要，等待计算完成，然后获取其结果。
V           get() throws InterruptedException, ExecutionException;

// 如有必要，最多等待为使计算完成所给定的时间之后，获取其结果（如果结果可用）。
V             get(long timeout, TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException;
```
