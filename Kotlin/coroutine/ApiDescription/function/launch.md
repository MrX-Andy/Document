### launch  
  
```
public fun launch(
    context: CoroutineContext = DefaultDispatcher,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    val newContext = newCoroutineContext(context)
    val coroutine = if (start.isLazy)
        LazyStandaloneCoroutine(newContext, block) else
        StandaloneCoroutine(newContext, active = true)
    coroutine.initParentJob(context[Job])
    start(block, coroutine, coroutine)
    return coroutine
}
```
启动一个协程，不会阻塞当前线程，同时 以Job类对象形式，返回一个协程的引用；  
当Job调用cancel后，协程会被取消；  
如果协程的context引用的是 parent 的context，那么job也是parent的子job；  
默认情况下，协程会被立刻执行，还可以配置start的参数值，选择CoroutineStart.LAZY等；  
如果start=CoroutineStart.LAZY; 协程在Job.join 之后会被立即执行；    

● context  协程上下文；  
● start  协程启动选项；  
● block  协程真正要执行的代码块，必须是suspend修饰的挂起函数；  
● return  函数返回一个Job类型，Job是协程创建的后台任务的概念，它持有该协程的引用；  

launch(UI)  代码块是主线程，代码块可以执行耗时操作；  
launch(CommonPool) 代码块是子线程，代码块可以执行耗时操作；  

```
GlobalScope.launch(Dispatchers.Main) {
    delay(3000)
    
}
```
[看一下，launch代码块的执行顺序](../launch/launchOrder.md)   
[利用parent参数，关联主线程生命周期](../launch/launch_lifecycle.md)    

