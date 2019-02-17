### actor  
既能发送，又能接收  

The basic thing to know about actor is that they have a mailbox, and you can send them messages.   
Kotlin guarantees that the actor only treats one message at a time, and that they will be treated in the order that they’ve been received.  
The expression actor<Int>(CommonPool){...} returns an actor that takes Integers as messages.   
The CommonPool denotes that all code of this actor will be executed in the CommonPool context, meaning in background,   
well away from the Main Thread. Indeed, an actor is really a kind of coroutine that exposes a SendChannel that you can send message to.   
That’s why we can use an actor as a way to achieve asynchronism.  
Once in the body of the actor, you have access to its channel. In the example above, we go through the channel and for each message (represented as an Integer),   
decide what to do with it depending on its value. Note how Kotlin’s when expression is useful here.  


### 示例1
```
fun testActor() {

    val sendChannel = createSendChannel()
    launch {
        delay(1000L)
        sendChannel.send("100")
        delay(1500L)
        sendChannel.send("102")
        delay(2000L)
        sendChannel.send("106")
    }

}

private fun createSendChannel(): SendChannel<String> = actor<String>(UI) {
    for (message in channel) {
        "B主线程 $isUiThread， $message".logI()
    }
}
```
执行结果  
```
2018-07-29 22:11:59.933 9270-9270/com.alex.andfun.coroutine I/LogTrack: [ (Sample12.kt:32) Sample12$createSendChannel$1#doResume] B主线程 true， 100
2018-07-29 22:12:01.434 9270-9270/com.alex.andfun.coroutine I/LogTrack: [ (Sample12.kt:32) Sample12$createSendChannel$1#doResume] B主线程 true， 102
2018-07-29 22:12:03.437 9270-9270/com.alex.andfun.coroutine I/LogTrack: [ (Sample12.kt:32) Sample12$createSendChannel$1#doResume] B主线程 true， 106
```

##### 参考  
https://blog.octo.com/en/a-responsive-and-clean-android-app-with-kotlin-actors/  
https://github.com/BonnetM/KotlinActorsAndCleanArchitecture/  
https://medium.com/@jagsaund/kotlin-concurrency-with-actors-34bd12531182  
https://github.com/jsaund/ActorsPlayground  
https://blog.elpassion.com/create-a-clean-code-app-with-kotlin-coroutines-and-android-architecture-components-part-2-4f585050d7d7  
https://blog.elpassion.com/create-a-clean-code-app-with-kotlin-coroutines-and-android-architecture-components-part-3-f3f3850acbe6  
  