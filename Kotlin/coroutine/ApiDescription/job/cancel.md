### cancel  

● 如果协程内，有释放时间片的操作，那么，在cancel之后，协程会被立刻取消掉；  
● 如果协程内，一直是抢占式的操作，那么，在cancel之后，协程会在抢占式操作结束之后，被取消掉；  
● 如果要在协程内，捕获cancel信息，可以用try catch来包裹内部代码块；  
```
private fun delayFun() = runBlocking {
    val job = launch {
        try {
            repeat(6) {
                LogTrack.i("打印计数器 $it")
                delay(500)
            }
            "B".logI()
        } catch (e: Exception) {
            LogTrack.w(e.message)
        } finally {
            LogTrack.i("finally")
        }
    }
    delay(1300)
    "C".logI()
    job.cancel()
    job.join()
    "D".logI()
}
```
● 无论是 cancel 还是 cancelAndJoin，都会等待协程内部的finally代码块 执行完；  

● 如果finally包裹NonCancellable，则会等待这段代码块执行完，再取消掉；  
```
private fun delayFun() = runBlocking {
    val job = launch {
        try {
            repeat(6) {
                LogTrack.i("打印计数器 $it")
                delay(500)
            }
            "B".logI()
        } catch (e: Exception) {
            LogTrack.w(e.message)
        } finally {
            LogTrack.i("finally NonCancellable")
            run(NonCancellable) {
                LogTrack.i("finally  A")
                delay(3000)
                LogTrack.i("finally  B")
            }
        }
    }
    delay(1300)
    "C".logI()
//        job.cancelAndJoin()
    job.cancel()
    job.join()
    "D".logI()
}
```

#### 计算密集型 cancel 无法取消任务  
协程正工作在循环计算中，并且不检查协程当前的状态, 那么调用cancel来取消协程将无法停止协程的运行，可以这样解决  
```
if (!isActive) {
    return@launch
}
```
