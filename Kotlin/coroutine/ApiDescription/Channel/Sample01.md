### 发送与接收
```
   fun testChannel() {
       val channel = Channel<String>()
       launch(UI) {
           launch {
               channel.send("A")
               delay(2, TimeUnit.SECONDS)
               channel.send("B")
               delay(1, TimeUnit.SECONDS)
               channel.send("C")
               channel.close()
           }
           for (result in channel) {
               "收到结果  $result".logI()
           }
       }
   }
   ```