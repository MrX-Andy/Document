### async  

```
public fun <T> async(
    context: CoroutineContext = DefaultDispatcher,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
): Deferred<T> {
    val newContext = newCoroutineContext(context)
    val coroutine = if (start.isLazy)
        LazyDeferredCoroutine(newContext, block) else
        DeferredCoroutine<T>(newContext, active = true)
    coroutine.initParentJob(context[Job])
    start(block, coroutine, coroutine)
    return coroutine
}
```  

在函数体内，异步 创建一个协程对象，以Deferred类对象的形式，返回一个预结果值；  
当结果对象，即Deferred对象调用cancel后，协程会被取消；  
如果协程的context引用的是 parent 的context，那么job也是parent的子job；  
默认情况下，协程会被立刻执行，还可以配置start的参数值，选择 CoroutineStart.LAZY 等；  
如果start=CoroutineStart.LAZY; 协程在Job.join 之后 或者 Deferred.await之后会被立即执行；    

