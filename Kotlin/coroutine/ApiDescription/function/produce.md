### produce  

启动一个新的协程，产生数据流，通过管道流发送数据；  
函数的返回值是ProducerJob类对象，即为协程对象；  
```
if (R.id.bt9 == id) {
    LogTrack.i("before launch")
    val job = launch(UI) {
        Sample01().createProduce().consumeEach {
            "主线程 ${BaseUtil.isUIThread()}  ， 收到 $it".logI()
        }
        LogTrack.i("consumeEach finish")
    }
    LogTrack.i("after launch")
    return
}

fun createProduce() = produce {
    "主线程 ${BaseUtil.isUIThread()}".logI()
    send("A")
    delay(1000L)
    send("B")
    delay(2000L)
    send("C")
    delay(3000L)
    send("D")
}
```
执行结果  
```
2018-07-23 13:01:17.859 6990-6990/? I/LogTrack: [ (New01Activity.kt:66) #onClick] before launch
2018-07-23 13:01:17.868 6990-6990/? I/LogTrack: [ (New01Activity.kt:73) #onClick] after launch
2018-07-23 13:01:17.875 6990-7028/? I/LogTrack: [ (Sample01.kt:50) Sample01$createProduce$1#doResume] 主线程 false
2018-07-23 13:01:17.876 6990-6990/? I/LogTrack: [ (New01Activity.kt:70) New01Activity$onClick$job$1#doResume] 主线程 true  ， 收到 A
2018-07-23 13:01:18.880 6990-6990/? I/LogTrack: [ (New01Activity.kt:70) New01Activity$onClick$job$1#doResume] 主线程 true  ， 收到 B
2018-07-23 13:01:20.880 6990-6990/? I/LogTrack: [ (New01Activity.kt:70) New01Activity$onClick$job$1#doResume] 主线程 true  ， 收到 C
2018-07-23 13:01:23.882 6990-6990/? I/LogTrack: [ (New01Activity.kt:70) New01Activity$onClick$job$1#doResume] 主线程 true  ， 收到 D
2018-07-23 13:01:23.883 6990-6990/? I/LogTrack: [ (New01Activity.kt:71) New01Activity$onClick$job$1#doResume] consumeEach finish
```

