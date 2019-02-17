### Timer  
◑ 延时 delay 毫秒； 
timer.schedule(task, 2000);  
◑ 延时 delay 毫秒； 每隔 period 毫秒 开始下一次 task  
timer.schedule(task, 2000, 10);  
◆ 示例 
```
public class TimerTest {
    public static void main(String[] args) throws InterruptedException {
        Timer timer = new Timer(true);

        LogTrack.w("开始...");
        timer.schedule(new LocalTimerTask(3000), 2000, 1000);  // task 3000
        timer.schedule(new LocalTimerTask(1000), 2000, 1000); // task 1000
        timer.schedule(new LocalTimerTask(2000), 2000, 1000); // task 2000
        Thread.sleep(30000);
    }

    private static final class LocalTimerTask extends TimerTask {
        private long timeFinish;

        public LocalTimerTask(long timeFinish) {
            this.timeFinish = timeFinish;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(timeFinish);
                LogTrack.w("我在干活..."+timeFinish);
            } catch (InterruptedException e) {
            }
        }
    }
}
```
Timer是顺序执行的，  
task3000执行完成之后，才会执行task1000；  
task1000执行完成之后，才会执行task2000；  
