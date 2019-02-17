### ticker 定时器  
#### 示例1
```
fun testTicker() {
    launch(UI, parent = mainJob) {
        val tickerChannel = ticker(1, TimeUnit.SECONDS, initialDelay = 0)
        for (channel in tickerChannel) {
            currentTimeMillis.logI()
        }
    }
}
```
解释：  每间隔1秒，发一个事件，每个事件之间间隔0秒；  
#### 简单封装  
```
/**
 * 计时器；
 * @param unit  delay 和 initialDelay 的时间单位
 * @param delay 相邻事件之间的delay时间
 * @param initialDelay 在initialDelay时间之后，开始第一个事件
 * @param context producing coroutine 的上下文
 * @param uiBlock 回调在ui线程的代码块
 * */
@Suppress("NAME_SHADOWING")
fun ticker(delay: Long, unit: TimeUnit = TimeUnit.MILLISECONDS, initialDelay: Long = 0L,
           context: CoroutineContext = EmptyCoroutineContext,
           mode: TickerMode = TickerMode.FIXED_PERIOD, parent: Job? = null, uiBlock: () -> Unit) = launch(UI, parent = parent) {
    val receiveChannel = kotlinx.coroutines.experimental.channels.ticker(delay, unit, initialDelay, context, mode)
    for (unit in receiveChannel) {
        uiBlock.invoke()
    }
}
```
试用一下  
```
fun testTicker2() {
    ticker(1, TimeUnit.SECONDS, parent = mainJob) {
        "主线程 $isUiThread，$currentTimeMillis".logI()  // 主线程
    }
}
```