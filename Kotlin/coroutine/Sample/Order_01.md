### 执行顺序  
#### 示例1  
```
fun sequenceExecution() {
    launch(UI) {
        TimeUtil.currentTimeMillis().logI()  // A 打印时间  
        getMessage(3000).await().logI()
        getUserName(2000).await().logI()
        TimeUtil.currentTimeMillis().logI()
    }
}
```
对上述代码，解说：  
getMessage 会在A 处，3s之后执行；  
getUserName 会在 getMessage 执行之后，2s之后执行；  
#### 示例2  
```
fun sequenceExecution2() {
    launch(UI) {
        TimeUtil.currentTimeMillis().logI()  // A 打印时间  
        "${getUserName(2000).await()} 说 ${getMessage(3000).await()}".logI()  // B 执行结果  
        TimeUtil.currentTimeMillis().logI()
    }
}
```  
对上述代码，解说：  
A 处代码，执行；  
B 处代码，先执行getUserName  再 执行 getMessage；  
B 处代码，打印操作，会在两个耗时操作顺序执行完之后，再执行；  
#### 耗时操作  
```
private fun getUserName(time: Long) = async {
    delay(time)
    "Alex"
}

private fun getMessage(time: Long) = async {
    delay(time)
    "好消息"
}
```



