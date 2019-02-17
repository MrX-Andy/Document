### Future简单示例  
```
public class SampleTest {
    public static void main(String[] args) throws Exception {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(6, 10, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<>()) {
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                super.beforeExecute(t, r);
                LogTrack.w("开始");
            }

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                LogTrack.w("结束");
            }
        };
        Future<String> future = threadPoolExecutor.submit(new InnerCallable());
        LogTrack.w("hello 线程池 0");  //  能执行到 这里
        LogTrack.w(future.get());  // 这里 是阻塞的 
        LogTrack.w("hello 线程池 1"); // 必须 先执行 callable 之后 future 被相应， 才会 执行  本条语句  
        threadPoolExecutor.shutdown(); // 执行到这里 会 关闭 线程池  
        LogTrack.w("hello 线程池 2");  // 必须 先执行 callable 之后 future 被相应， 才会 执行  本条语句  
    }

    private static final class InnerCallable implements Callable<String> {
        @Override
        public String call() throws Exception {
            Thread.sleep(3000);
            return "返回结果 " + new Random(20).nextInt(100);
        }
    }
}
```