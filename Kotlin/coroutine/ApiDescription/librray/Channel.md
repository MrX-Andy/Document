### Channel  

[示例1 ](../Channel/Sample01.md)   
#### receive()  
只接收一个事件  

#### for (result in channel)  
按顺序接收，处理所有事件，直到channel关闭    
```
for (result in channel) {
    "收到结果  $result".logI()
}
```
#### close  
关闭资源  

#### fan-out  
有一个SendChannel 多个 ReceiverChannel；  
一个 SendChannel 每次只发一个事件；  
如果一个事件，被其中一个ReceiverChannel消费掉，就不会再被其他ReceiverChannel处理，也就是其他ReceiverChannel的consumeEach不会能够迭代出事件；  
并且处理事件，推荐使用  for (result in channel) 而不是 channel.consumeEach ；    
理由：  https://github.com/Kotlin/kotlinx.coroutines/blob/master/coroutines-guide.md  
Also, pay attention to how we explicitly iterate over channel with for loop to perform fan-out in launchProcessor code.     
Unlike consumeEach, this for loop pattern is perfectly safe to use from multiple coroutines.     
If one of the processor coroutines fails, then others would still be processing the channel,     
while a processor that is written via consumeEach always consumes (cancels) the underlying channel on its normal or abnormal termination.  

```
for (result in channel) {
    "name = $name， 收到 $result，主线程 $isUiThread".logW()
}
channel.consumeEach { result ->
    "name = $name， 收到 $result，主线程 $isUiThread".logW()
}
```

#### fan-in 
```
/**
 * 一个发送方，多个接收方
 * */
fun oneReceiverMultipleSender() {
    val channel = Channel<String>()
    launch(UI) {
        launch { sendString(channel, "foo", 200L) }  // 如果没有launch，收不到事件
        launch { sendString(channel, "BAR!", 500L) }  // 如果没有launch，收不到事件
        for (result in channel) {  // 一直发，一直收
            "收到 $result".logW()
        }
        repeat(6) {
            // 只收到前6个
            "收到 ${channel.receive()}".logW()
        }
    }
}

private suspend fun sendString(channel: SendChannel<String>, s: String, time: Long) {
    while (true) {
        delay(time)
        channel.send(s)
    }
}
```  
#### 缓冲区  
val channel = Channel<Int>(4) // 创建一个缓冲区容量为4的通道  
channel.send(it) // 当缓冲区已满的时候， send将会挂起  


