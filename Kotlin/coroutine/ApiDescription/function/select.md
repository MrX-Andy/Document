### select 选择器  
select表达式能够同时等待多条协程，并且选择第一个运行的协程。  
select是一个协程方法，他会暂停当前协程，等到select返回  

#### 示例 1
发送事件  
```
private fun firstProduce() = produce<String> {
    for (i in 0..5) {
        send("first $i")
        delay(1500L)
    }
}

private fun secondProduce() = produce<String> {
    for (i in 0..5) {
        send("second $i")
        delay(2000L)
    }
}
```
##### 直接处理  
```
private suspend fun handleChannel(firstChannel: ReceiveChannel<String>, secondChannel: ReceiveChannel<String>) {
    launch {
        for (result in firstChannel) {
            "A 收到 $result".logI()
        }
    }
    launch {
        for (result in secondChannel) {
            "B 收到 $result".logI()
        }
    }
}
```
运行结果  
```
2018-07-28 21:30:08.664 31060-31231/com.alex.andfun.coroutine I/LogTrack: [ (Sample10.kt:53) Sample10$handleChannel$2#doResume] A 收到 first 0
2018-07-28 21:30:08.664 31060-31230/com.alex.andfun.coroutine I/LogTrack: [ (Sample10.kt:58) Sample10$handleChannel$3#doResume] B 收到 second 0
2018-07-28 21:30:10.165 31060-31231/com.alex.andfun.coroutine I/LogTrack: [ (Sample10.kt:53) Sample10$handleChannel$2#doResume] A 收到 first 1
2018-07-28 21:30:10.665 31060-31230/com.alex.andfun.coroutine I/LogTrack: [ (Sample10.kt:58) Sample10$handleChannel$3#doResume] B 收到 second 1
2018-07-28 21:30:11.669 31060-31231/com.alex.andfun.coroutine I/LogTrack: [ (Sample10.kt:53) Sample10$handleChannel$2#doResume] A 收到 first 2
2018-07-28 21:30:12.667 31060-31230/com.alex.andfun.coroutine I/LogTrack: [ (Sample10.kt:58) Sample10$handleChannel$3#doResume] B 收到 second 2
2018-07-28 21:30:13.170 31060-31231/com.alex.andfun.coroutine I/LogTrack: [ (Sample10.kt:53) Sample10$handleChannel$2#doResume] A 收到 first 3
2018-07-28 21:30:14.668 31060-31231/com.alex.andfun.coroutine I/LogTrack: [ (Sample10.kt:58) Sample10$handleChannel$3#doResume] B 收到 second 3
2018-07-28 21:30:14.672 31060-31230/com.alex.andfun.coroutine I/LogTrack: [ (Sample10.kt:53) Sample10$handleChannel$2#doResume] A 收到 first 4
2018-07-28 21:30:16.174 31060-31231/com.alex.andfun.coroutine I/LogTrack: [ (Sample10.kt:53) Sample10$handleChannel$2#doResume] A 收到 first 5
2018-07-28 21:30:16.670 31060-31230/com.alex.andfun.coroutine I/LogTrack: [ (Sample10.kt:58) Sample10$handleChannel$3#doResume] B 收到 second 4
2018-07-28 21:30:18.672 31060-31234/com.alex.andfun.coroutine I/LogTrack: [ (Sample10.kt:58) Sample10$handleChannel$3#doResume] B 收到 second 5
```
##### select  
```
private suspend fun selectChannel(firstChannel: ReceiveChannel<String>, secondChannel: ReceiveChannel<String>) {
    select<String> {
        firstChannel.onReceive { result ->
            "A 收到 $result".logI()
            ""
        }
        secondChannel.onReceive { result ->
            "B 收到 $result".logI()
            ""
        }
    }
}
```
运行结果  
```
2018-07-28 21:35:45.238 31318-31390/com.alex.andfun.coroutine I/LogTrack: [ (Sample10.kt:40) Sample10$selectChannel$2$1#doResume] A 收到 first 0
2018-07-28 21:35:45.240 31318-31390/com.alex.andfun.coroutine I/LogTrack: [ (Sample10.kt:44) Sample10$selectChannel$2$2#doResume] B 收到 second 0
2018-07-28 21:35:46.740 31318-31392/com.alex.andfun.coroutine I/LogTrack: [ (Sample10.kt:40) Sample10$selectChannel$2$1#doResume] A 收到 first 1
2018-07-28 21:35:47.242 31318-31390/com.alex.andfun.coroutine I/LogTrack: [ (Sample10.kt:44) Sample10$selectChannel$2$2#doResume] B 收到 second 1
2018-07-28 21:35:48.244 31318-31390/com.alex.andfun.coroutine I/LogTrack: [ (Sample10.kt:40) Sample10$selectChannel$2$1#doResume] A 收到 first 2
2018-07-28 21:35:49.245 31318-31390/com.alex.andfun.coroutine I/LogTrack: [ (Sample10.kt:44) Sample10$selectChannel$2$2#doResume] B 收到 second 2
2018-07-28 21:35:49.746 31318-31392/com.alex.andfun.coroutine I/LogTrack: [ (Sample10.kt:40) Sample10$selectChannel$2$1#doResume] A 收到 first 3
```
##### 分析  
select执行结果有7条，
直接处理A有6条，B 有6条；  


