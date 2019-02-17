### withTimeout  
超时时间  
```
private fun coroutineFun() = runBlocking {
    val result = withTimeoutOrNull(1300) {
        "A".logI()
        delay(500)
        "B".logI()
        delay(500)
        "C".logI()
        delay(500)
        "D".logI()
        delay(500)
        "E".logI()
        try {

        } catch (e: Exception) {
            LogTrack.w(e.message)
        } finally {
            "finally 一定会运行的".logI()
        }
        "结束语"
    }
    LogTrack.w("result = $result")  // RESULT
}
```
● withTimeout
超时自动结束，但是不会看到抛出异常；  
用try catch 可以捕获到异常信息；  
不会执行到 RESULT 代码块；  
● withTimeoutOrNull  
超时自动结束，但是不会看到抛出异常；  
用try catch 可以捕获到异常信息；  
会执行到 RESULT 代码块，但是result为null；  
  
