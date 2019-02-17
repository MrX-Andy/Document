### runBlocking  

````
public fun <T> runBlocking(context: CoroutineContext = EmptyCoroutineContext, block: suspend CoroutineScope.() -> T): T {
    val currentThread = Thread.currentThread()
    val eventLoop = if (context[ContinuationInterceptor] == null) BlockingEventLoop(currentThread) else null
    val newContext = newCoroutineContext(context + (eventLoop ?: EmptyCoroutineContext))
    val coroutine = BlockingCoroutine<T>(newContext, currentThread, privateEventLoop = eventLoop != null)
    coroutine.initParentJob(context[Job])
    block.startCoroutine(coroutine, coroutine)
    return coroutine.joinBlocking()
}
````
runBlocking函数不是用来当作普通协程函数使用的，它的设计主要是用来桥接普通阻塞代码和挂起风格-非阻塞代码的；  
主要用来跑 psvm 的main函数，或者test函数的；  
如果直接在main函数跑协程代码，可能看不到执行结果，因为这个时候，主线程已经结束了，故此 需要runBlocking代码块；  
在runBlocking代码块，会在当前thread中执行，需要执行的携程函数，并等待携程执行完成；    
如果runBlocking代码块跑在UI线程，出现耗时操作，一定会出现ANR异常的；  

#### 示例代码01  
```
fun testRunBlocking() {
    "A主线程 ${BaseUtil.isUIThread()}".logI()
    val result = runBlocking {
        "B0 主线程 ${BaseUtil.isUIThread()}".logI()
        delay(6000L)
        "B1 主线程 ${BaseUtil.isUIThread()}".logI()
        "runBlocking return value"
    }
    "result = $result， C主线程 ${BaseUtil.isUIThread()}".logI()
    "D主线程 ${BaseUtil.isUIThread()}".logI()
}
```  
执行结果：  
```
2018-07-24 12:57:44.150 22250-22250/com.alex.andfun.coroutine I/LogTrack: [ (Sample06.kt:16) #testRunBlocking] A主线程 true
2018-07-24 12:57:44.159 22250-22250/com.alex.andfun.coroutine I/LogTrack: [ (Sample06.kt:18) Sample06$testRunBlocking$result$1#doResume] B0 主线程 true
2018-07-24 12:57:50.164 22250-22250/com.alex.andfun.coroutine I/LogTrack: [ (Sample06.kt:20) Sample06$testRunBlocking$result$1#doResume] B1 主线程 true
2018-07-24 12:57:50.165 22250-22250/com.alex.andfun.coroutine I/LogTrack: [ (Sample06.kt:23) #testRunBlocking] result = runBlocking return value， C主线程 true
2018-07-24 12:57:50.165 22250-22250/com.alex.andfun.coroutine I/LogTrack: [ (Sample06.kt:24) #testRunBlocking] D主线程 true
```
#### 示例代码02  
如果要在非 挂起函数内 使用 delay等挂起函数，需要用 runBlocking来修饰函数，例如下面的示例；  

```
private fun coroutineFun() = runBlocking<Unit> {
    val job0 = launch(UI) {
        LogTrack.w("job0 Start： ${Thread.currentThread()}")
        delay(3000L)
        LogTrack.w("job0 World!")
    }

    val job1 = launch(UI) {
        LogTrack.w("job1 Start：${Thread.currentThread()}")
        delay(5000L)
        LogTrack.w("job1 World!")
    }

    LogTrack.w("Main Thread: ${Thread.currentThread()}")
    LogTrack.w("job0 is active: ${job0.isActive}  ${job0.isCompleted}")
    LogTrack.w("job1 is active: ${job1.isActive}  ${job1.isCompleted}")
    //理论上这里是可以delay的，但是如果delay的时间比较长，就会报 ANR了；
    //= runBlocking<Unit>  如果函数或者 block里面，没有加 runBlocking修饰，那么是不可以使用delay函数的；
    delay(2000)
    LogTrack.w("我延时了2秒")
}
```
#### 示例代码03  
这样会 ANR  
```
fun testLaunch3() = runBlocking {
    val job = launch(UI) {
        delay(5000L)
    }
    job.join()
}
```  
这样会 ANR  
```
fun testRunBlocking() = runBlocking {
    "A主线程 $isUiThread".logI()
    delay(10000L)
    "B主线程 $isUiThread".logI()
}
```