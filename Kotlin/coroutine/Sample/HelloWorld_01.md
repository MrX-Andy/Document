### 入门示例  01  

#### 延时2秒之后，吐司提示  
```
import kotlinx.coroutines.experimental.android.UI

override fun onClick(v: View) {
    val id = v.id
    if (id == R.id.bt1) {
        coroutineFun()
        return
    }

}

fun coroutineFun() {
    launch(UI) {
        delay(2000L)
        ToastUtil.shortCenter("hello UI thread = ${BaseUtil.isUIThread()}")  // true
    }
}
```

#### suspend  挂起函数  
```
if (R.id.bt2 == id) {
    launch(UI) {
        Sample01().testSuspend()
    }
    return
}

suspend fun testSuspend() {
    "主线程 ${BaseUtil.isUIThread()}".logI()  // true
    delay(2000L)
    ToastUtil.shortCenter("delay之后是主线程 ${BaseUtil.isUIThread()} ")  // true
}
```

#### async 与 Deferred  
```
// New01Activity
if (R.id.bt3 == id) {
    "launch 之外-前".logI()
    launch(UI) {
        "主线程 ${BaseUtil.isUIThread()}".logI()
        val await = Sample01().testAsync().await()
        "主线程 ${BaseUtil.isUIThread()}".logI()
        ToastUtil.shortCenter(await)
    }
    "launch 之外-后".logI()
    return
}

// Sample01 
suspend fun testAsync() = async {
    "主线程 ${BaseUtil.isUIThread()}".logI()
    delay(2000)
    "主线程 ${BaseUtil.isUIThread()}".logI()
    "我是返回结果"
}
```
执行结果  
```
10:06:27.611 25054-25054/[ (New01Activity.kt:37) #onClick] launch 之外-前
10:06:27.621 25054-25054/[ (New01Activity.kt:44) #onClick] launch 之外-后
10:06:27.623 25054-25054/[ (New01Activity.kt:39) New01Activity$onClick$2#doResume] 主线程 true
10:06:27.630 25054-25091/[ (Sample01.kt:39) Sample01$testAsync$2#doResume] 主线程 false
10:06:29.634 25054-25091/[ (Sample01.kt:41) Sample01$testAsync$2#doResume] 主线程 false
10:06:29.636 25054-25054/[ (New01Activity.kt:42) New01Activity$onClick$2#doResume] 主线程 true
```

