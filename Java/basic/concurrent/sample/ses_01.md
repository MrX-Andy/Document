#####  周期执行  
##### ScheduledExecutorService.scheduleAtFixedRate  

```
@SuppressWarnings("PointlessArithmeticExpression")
public class ExecutorsTest {
    public static void main(String[] args) {
        long sleepTime = 10;
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);
        executorService.scheduleAtFixedRate(new LocalRunnable(), 200, 1000, TimeUnit.MILLISECONDS);
    }

    private static final class LocalRunnable implements Runnable {
        private long sleepTime = 1 * 500;

        @Override
        public void run() {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LogTrack.w("hello  " + Thread.currentThread().getName());
        }
    }
}

```