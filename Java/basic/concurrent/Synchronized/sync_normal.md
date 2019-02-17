### 假设没有做任何处理  

◆ NormalCat  
```
public class NormalCat {
    public void printVisitor(String visitorName){
        LogTrack.w(Thread.currentThread().getName()+"  "+visitorName);
    }
}
```  
◆ 线程并发访问    
```
public class SynchronizedTest {
    public static void main(String[] args) {
        final NormalCat normalCat = new NormalCat();
        new Thread("Thread0") {
            @Override
            public void run() {
                super.run();
                try {
                    for (int i = 0; i < 5; i++) {
                        normalCat.printVisitor("我是线程0");
                        Thread.sleep(500);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        new Thread("Thread1") {
            @Override
            public void run() {
                super.run();
                try {
                    for (int i = 0; i < 5; i++) {
                        normalCat.printVisitor("我是线程1");
                        Thread.sleep(500);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }
}
```  

◆ 测试结果  
LogTrack  07:17:32:0450  WARN  [ (NormalCat.java:12) #printVisitor] Thread0  我是线程0  
LogTrack  07:17:32:0451  WARN  [ (NormalCat.java:12) #printVisitor] Thread1  我是线程1  
LogTrack  07:17:33:0002  WARN  [ (NormalCat.java:12) #printVisitor] Thread0  我是线程0  
LogTrack  07:17:33:0003  WARN  [ (NormalCat.java:12) #printVisitor] Thread1  我是线程1  
LogTrack  07:17:33:0503  WARN  [ (NormalCat.java:12) #printVisitor] Thread0  我是线程0  
LogTrack  07:17:33:0504  WARN  [ (NormalCat.java:12) #printVisitor] Thread1  我是线程1  
LogTrack  07:17:34:0003  WARN  [ (NormalCat.java:12) #printVisitor] Thread0  我是线程0  
LogTrack  07:17:34:0004  WARN  [ (NormalCat.java:12) #printVisitor] Thread1  我是线程1  
LogTrack  07:17:34:0504  WARN  [ (NormalCat.java:12) #printVisitor] Thread0  我是线程0  
LogTrack  07:17:34:0505  WARN  [ (NormalCat.java:12) #printVisitor] Thread1  我是线程1      
