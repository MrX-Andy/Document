### Deferred  

● DeferredCoroutine  任务创建后会立即启动；  
● LazyDeferredCoroutine  任务创建后new的状态，要等用户调用 start() or join() or await()去启动他；  
● CompletableDeferred  

> Deferred 常用的属性和函数说明如下  

isCompletedExceptionally  当协程在计算过程中有异常failed 或被取消，返回true。 这也意味着isActive等于 false ，同时 isCompleted等于 true；  
isCancelled  如果当前延迟任务被取消，返回true；  
await  等待此延迟任务完成，而不阻塞线程；如果延迟任务完成, 则返回结果值或引发相应的异常。  

> Deferred 状态描述  

New (可选初始状态)  isActive = false；  isCompleted = false；  isCompletedExceptionally = false； isCancelled = false；  
Active (默认初始状态)  isActive = true；  isCompleted = false；  isCompletedExceptionally = false； isCancelled = false；  
Resolved (最终状态)  isActive = false；  isCompleted = true；  isCompletedExceptionally = false； isCancelled = false；  
Failed (最终状态)  isActive = false；  isCompleted = true；  isCompletedExceptionally = true； isCancelled = false；  
Cancelled (最终状态)  isActive = false；  isCompleted = true；  isCompletedExceptionally = true； isCancelled = true；  

#### CompletableDeferred  
````
val deferred = CompletableDeferred<WrapperEntity<T>>()
launch {
    kotlinx.coroutines.experimental.delay(delay)
    val wrapperEntity = WrapperEntity<T>().apply {
        code = C.CodeEnum.success
        message = "success"
        requestTag = "mock data"
        this.data = data
    }
    deferred.complete(wrapperEntity)
}
````