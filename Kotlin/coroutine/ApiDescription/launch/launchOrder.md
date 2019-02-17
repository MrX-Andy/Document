### launch 的执行顺序  
#### launch(UI)  
````
fun testLaunch2() {
    "A主线程 ${BaseUtil.isUIThread()}".logI() //A  
    launch(UI) {
        "B主线程 ${BaseUtil.isUIThread()}".logI()  // true
        delay(5000L)
        "B 2主线程 ${BaseUtil.isUIThread()}".logI()  // true
    }
    "C主线程 ${BaseUtil.isUIThread()}".logI()  // C  
    for (i in 0..399) {
        i.logI()
    }
}
````
先执行A处代码；  
在执行C处代码；  
最后执行B处代码；  
不管C处代码，需要执行多久，都是如此，要等UI线程全部执行完成，才会执行launch{ }  
launch(UI){} 代码块是主线程，仍然可以执行耗时操作，并且在代码块内，是顺序执行的；  

#### launch(CommonPool)  
```
fun testLaunch() {
    "A主线程 ${BaseUtil.isUIThread()}".logI()
    launch {
        "B主线程 ${BaseUtil.isUIThread()}".logI()  // false
        delay(300L)
        "B 2主线程 ${BaseUtil.isUIThread()}".logI()  // false
    }
    "C主线程 ${BaseUtil.isUIThread()}".logI()
    for (i in 0..99) {
        i.logI()
    }
}
```  

执行结果  
```
2018-07-23 00:11:53.578 9391-9391/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:14) #testLaunch] A主线程 true
2018-07-23 00:11:53.579 9391-9391/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:18) #testLaunch] C主线程 true
2018-07-23 00:11:53.580 9391-9391/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:20) #testLaunch] 0
2018-07-23 00:11:53.580 9391-9391/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:20) #testLaunch] ...
2018-07-23 00:11:53.587 9391-9391/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:20) #testLaunch] 21
2018-07-23 00:11:53.587 9391-9450/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:16) Sample04$testLaunch$1#doResume] B主线程 false
2018-07-23 00:11:53.580 9391-9391/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:20) #testLaunch] ...
2018-07-23 00:11:53.587 9391-9391/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:20) #testLaunch] 99
2018-07-23 00:18:11.957 9593-9630/com.alex.andfun.coroutine I/LogTrack: [ (Sample04.kt:19) Sample04$testLaunch$1#doResume] B 2主线程 false
```
结果分析：  
A处代码最先执行；  
C处代码开始执行；  
launch{}代码块，涉及状态机的状态保存和切换，最后执行；  
但是C处的代码没有直接结束，launch{}代码块就开始执行的情况，还是很常见的；
和上面的launch(UI) 的执行结果是不一样的；  
  
