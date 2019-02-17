### 并发顺序  

#### 示例 01 
```
fun concurrentExecution() = launch(UI) {
    "async 之前 ${TimeUtil.currentTimeMillis()}".logI()  // A  打印时间  
    val userName = async(CommonPool) { getUserName(5000L) }
    val message = async(CommonPool) { getMessage(2000L) }
    listOf(userName, message).forEach {
        it.join()
        "join 之后 ${TimeUtil.currentTimeMillis()}".logI()  // B  打印时间
    }
    "userName 之前 ${TimeUtil.currentTimeMillis()}".logI()
    userName.await().logI() // C 打印结果 
    "message 之前 ${TimeUtil.currentTimeMillis()}".logI()
    message.await().logI() // D 打印结果 
    "最后的 ${TimeUtil.currentTimeMillis()}".logI()
}
```  
简单描述：  
1. 先执行A处代码；  
2. 再执行 getUserName和getMessage这两个耗时操作并发执行；  
3. B处的代码，会在 上面两个耗时操作都执行完，也就是并发执行完成之后，才会执行；  
4. C和D 处的打印结果，是顺序，非耗时操作；  
#### 示例 02  
```
fun concurrentExecution2() = launch(UI) {
    TimeUtil.currentTimeMillis().logI()
    "async 之前 ${TimeUtil.currentTimeMillis()}".logI() // A  打印时间  
    val userName = async(CommonPool) { getUserName(3000L) }
    val message = async(CommonPool) { getMessage(2000L) }
    listOf(userName, message).forEach {
        it.join()
        "join 之后 ${TimeUtil.currentTimeMillis()}".logI()   // B  打印时间
    }
    "await 之前 ${TimeUtil.currentTimeMillis()}".logI()
    "${userName.await()} 说 ${userName.await()}".logI()  // C 打印结果 
    "await 之后 ${TimeUtil.currentTimeMillis()}".logI()
}
``` 
简单描述：  
1. 先执行A处代码；  
2. 再执行 getUserName和getMessage这两个耗时操作并发执行；  
3. B处的代码，会在 上面两个耗时操作都执行完，也就是并发执行完成之后，才会执行；  
4. C处的打印结果，是顺序，非耗时操作；   
####  耗时操作    
```
private suspend fun getUserName(time: Long): String {
    delay(time)
    return "Alex"
}

private suspend fun getMessage(time: Long): String {
    delay(time)
    return "好消息"
}
```  
