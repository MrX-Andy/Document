### 协程安全问题  
当多个协程操作一个变量时，也会存在安全问题，使用Mutex进行挂起    
```
val mutex = Mutex()
var counter = 0
fun main(args: Array<String>) = runBlocking<Unit> {
    massiveRun(CommonPool) {
        mutex.lock()
        try { counter++ }
        finally { mutex.unlock() }
    }
    ...
}
```

### 使用线程安全数据结构  
```
private val counter = AtomicInteger()
fun main(args: Array<String>) = runBlocking {
    massiveRun(CommonPool) {
        counter.incrementAndGet()
    }
    ...
}
```