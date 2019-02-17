### join函数  

挂起协程，一直等到job结束，当job正常执行一直到完成或者被cancel，挂起的协程会正常返回，不会抛出异常信息；  
当协程已经被取消，或者已经完成，再调用join函数，就会抛出异常；  
当外部因素使用当前job，调用cancel函数，会使得job.join() 之后的代码快被执行；      

#### 示例3
```
fun testLaunch4() {
    "A主线程 $isUiThread".logI()
    launch(UI) {
        val job = launch {
            delay(5000L)
            "B主线程 $isUiThread".logI()
            delay(5000L)
            "B1主线程 $isUiThread".logI()
            delay(8000L)
            "B2主线程 $isUiThread".logI()
            delay(5000L)
        }
        "C主线程 $isUiThread".logI()
        job.join()
        "D主线程 $isUiThread".logI()
        delay(6000L)
        "E主线程 $isUiThread".logI()
        job.cancel()
        "F主线程 $isUiThread".logI()
    }
}
```
执行结果  
```
2018-07-24 19:49:03.585 10445-10445/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:59) #testLaunch4] A主线程 true
2018-07-24 19:49:03.598 10445-10445/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:70) Sample04$testLaunch4$1#doResume] C主线程 true
2018-07-24 19:49:08.603 10445-10482/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:63) Sample04$testLaunch4$1$job$1#doResume] B主线程 false
2018-07-24 19:49:13.606 10445-10482/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:65) Sample04$testLaunch4$1$job$1#doResume] B1主线程 false
2018-07-24 19:49:21.608 10445-10482/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:67) Sample04$testLaunch4$1$job$1#doResume] B2主线程 false
2018-07-24 19:49:26.612 10445-10445/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:72) Sample04$testLaunch4$1#doResume] D主线程 true
2018-07-24 19:49:32.620 10445-10445/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:74) Sample04$testLaunch4$1#doResume] E主线程 true
2018-07-24 19:49:32.621 10445-10445/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:76) Sample04$testLaunch4$1#doResume] F主线程 true
```
