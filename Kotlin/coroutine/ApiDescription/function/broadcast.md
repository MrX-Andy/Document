### broadcast 广播

#### 示例1
```
fun testBroadcast() {
    launch {
        val broadcastChannel = broadcast<String> {
            delay(2000)
            send("A")
            delay(1000)
            send("B")
            delay(1500)
            send("C")
        }
        val receiveChannel = broadcastChannel.openSubscription()
        for (element in receiveChannel) {
            "$element $currentTimeMillis".logI()
        }
    }

}
```