### Thread  
Thread有两种写法，继承Thread 和 Runnable，Runnable主要是为了解决Java不能多继承、又要实现多线程的问题；  
sleep 是让线程暂停，但是依然占用CPU的时间片；  
wait 是让线程等待，也就是等待CPU的时间片；  
getId 就是获取线程的编号 id；  
stop 停止线程，一定别去调用它，它即将被废弃了；  
yield 让出CPU时间片，但是合适让出，不确定；就算让出了，可能也会立刻又获取了；  

◆ 抛异常停止线程  
```
public static void main(String[] args) throws InterruptedException {
    LocalThread localThread = new LocalThread();
    localThread.start();
    Thread.sleep(3000);
    localThread.interrupt();
    Thread.sleep(10000);
}

private static final class LocalThread extends Thread {
    @Override
    public void run() {
        super.run();
        try {
            for (int i = 0; i < 2000; i++) {
                if (isInterrupted()) {
                    LogTrack.w("中断点，抛异常停止线程 ");
                    throw new InterruptedException("");
                }
                LogTrack.w("hello " + i);
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```