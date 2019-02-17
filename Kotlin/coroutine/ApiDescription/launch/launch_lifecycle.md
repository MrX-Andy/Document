### 利用parent参数，关联主线程生命周期    
```
fun testLaunch5() {
    "A主线程 $isUiThread".logI()
    val job = Job()
    launch {
        repeat(5) { index ->
            launch(UI, parent = job, onCompletion = {
                "结束，$it".logW()
            }) {
                "$index B主线程 $isUiThread".logI()
                delay(3, TimeUnit.SECONDS)
                "$index B2主线程 $isUiThread".logI()
                delay(5, TimeUnit.SECONDS)
                "$index B3主线程 $isUiThread".logI()
            }
        }
        "C主线程 $isUiThread".logI()
        delay(4, TimeUnit.SECONDS)
        "D主线程 $isUiThread".logI()
        job.cancelAndJoin()
        "E主线程 $isUiThread".logI()
    }
}
```
运行结果：  
```
07-24 23:38:42.594 13401-13401/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:88) #testLaunch5] A主线程 true
07-24 23:38:42.604 13401-13401/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:95) Sample04$testLaunch5$1$1$2#doResume] 0 B主线程 true
07-24 23:38:42.605 13401-13401/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:95) Sample04$testLaunch5$1$1$2#doResume] 1 B主线程 true
07-24 23:38:42.606 13401-13401/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:95) Sample04$testLaunch5$1$1$2#doResume] 2 B主线程 true
07-24 23:38:42.606 13401-13447/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:102) Sample04$testLaunch5$1#doResume] C主线程 false
07-24 23:38:42.606 13401-13401/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:95) Sample04$testLaunch5$1$1$2#doResume] 3 B主线程 true
07-24 23:38:42.606 13401-13401/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:95) Sample04$testLaunch5$1$1$2#doResume] 4 B主线程 true
07-24 23:38:45.610 13401-13401/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:97) Sample04$testLaunch5$1$1$2#doResume] 0 B2主线程 true
07-24 23:38:45.613 13401-13401/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:97) Sample04$testLaunch5$1$1$2#doResume] 1 B2主线程 true
07-24 23:38:45.615 13401-13401/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:97) Sample04$testLaunch5$1$1$2#doResume] 2 B2主线程 true
07-24 23:38:45.616 13401-13401/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:97) Sample04$testLaunch5$1$1$2#doResume] 3 B2主线程 true
07-24 23:38:45.618 13401-13401/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:97) Sample04$testLaunch5$1$1$2#doResume] 4 B2主线程 true
07-24 23:38:46.608 13401-13447/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:104) Sample04$testLaunch5$1#doResume] D主线程 false
07-24 23:38:46.614 13401-13401/com.alex.andfun.coroutine W/LogTrack: [ (Sample04.kt:93) Sample04$testLaunch5$1$1$1#invoke] 结束，kotlinx.coroutines.experimental.JobCancellationException: Child job was cancelled because of parent failure; job=StandaloneCoroutine{Cancelled}@2eff9080
07-24 23:38:46.618 13401-13401/com.alex.andfun.coroutine W/LogTrack: [ (Sample04.kt:93) Sample04$testLaunch5$1$1$1#invoke] 结束，kotlinx.coroutines.experimental.JobCancellationException: Child job was cancelled because of parent failure; job=StandaloneCoroutine{Cancelled}@23d7b6b9
07-24 23:38:46.620 13401-13401/com.alex.andfun.coroutine W/LogTrack: [ (Sample04.kt:93) Sample04$testLaunch5$1$1$1#invoke] 结束，kotlinx.coroutines.experimental.JobCancellationException: Child job was cancelled because of parent failure; job=StandaloneCoroutine{Cancelled}@89b5afe
07-24 23:38:46.623 13401-13401/com.alex.andfun.coroutine W/LogTrack: [ (Sample04.kt:93) Sample04$testLaunch5$1$1$1#invoke] 结束，kotlinx.coroutines.experimental.JobCancellationException: Child job was cancelled because of parent failure; job=StandaloneCoroutine{Cancelled}@1e53525f
07-24 23:38:46.625 13401-13401/com.alex.andfun.coroutine W/LogTrack: [ (Sample04.kt:93) Sample04$testLaunch5$1$1$1#invoke] 结束，kotlinx.coroutines.experimental.JobCancellationException: Child job was cancelled because of parent failure; job=StandaloneCoroutine{Cancelled}@210d34ac
07-24 23:38:46.627 13401-13447/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:106) Sample04$testLaunch5$1#doResume] E主线程 false

```
代码讲解：  
在上述代码中， 首先定义一个 job，作为主的job；  
在Activity的onDestroy函数中，调用job.cancel 就可以做到取消job以及其所有的子协程；  
