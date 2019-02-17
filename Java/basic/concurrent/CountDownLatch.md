### CountDownLatch  
计数器  
```
public class Test {
    public static void main(String[] args) {

        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10);
        MyRunnable runnable = new MyRunnable();
        runnable.setLatch(latch);
        for (int i = 0; i < 10; i++) {
            service.execute(runnable);
        }
        LogTrack.w("await 之前");
        ThreadUtil.await(latch);
        LogTrack.w("await 之后");
        service.shutdownNow();
        LogTrack.w("shutdown 之后");
    }

    private static final class MyRunnable implements Runnable {
        CountDownLatch latch;

        void setLatch(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {
            ThreadUtil.sleep(2000);
            LogTrack.w(Thread.currentThread().getName() + ", 完成");
            latch.countDown();
        }
    }

}
```

执行结果  
```
LogTrack  16:31:30:0752  WARN  [ (Test.java:25) #main] await 之前
LogTrack  16:31:32:0754  WARN  [ (Test.java:42) Test$MyRunnable#run] pool-1-thread-2, 完成
LogTrack  16:31:32:0754  WARN  [ (Test.java:42) Test$MyRunnable#run] pool-1-thread-4, 完成
LogTrack  16:31:32:0754  WARN  [ (Test.java:42) Test$MyRunnable#run] pool-1-thread-5, 完成
LogTrack  16:31:32:0754  WARN  [ (Test.java:42) Test$MyRunnable#run] pool-1-thread-7, 完成
LogTrack  16:31:32:0754  WARN  [ (Test.java:42) Test$MyRunnable#run] pool-1-thread-9, 完成
LogTrack  16:31:32:0754  WARN  [ (Test.java:42) Test$MyRunnable#run] pool-1-thread-6, 完成
LogTrack  16:31:32:0754  WARN  [ (Test.java:42) Test$MyRunnable#run] pool-1-thread-3, 完成
LogTrack  16:31:32:0754  WARN  [ (Test.java:42) Test$MyRunnable#run] pool-1-thread-10, 完成
LogTrack  16:31:32:0754  WARN  [ (Test.java:42) Test$MyRunnable#run] pool-1-thread-8, 完成
LogTrack  16:31:32:0754  WARN  [ (Test.java:42) Test$MyRunnable#run] pool-1-thread-1, 完成
LogTrack  16:31:32:0755  WARN  [ (Test.java:27) #main] await 之后
LogTrack  16:31:32:0756  WARN  [ (Test.java:29) #main] shutdown 之后

```