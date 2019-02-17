### 线程安全与线程不安全  
```
package com.alex.leet;

import org.alex.util.LogTrack;
import org.alex.util.ThreadUtil;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 作者：Alex
 * 时间：2018/7/31 23:48
 * 简述：
 * https://leetcode.com/problemset/all/
 */
public class Test {
    private static int count = 0;
    private static AtomicInteger aiCount = new AtomicInteger(0);

    public static void main(String[] args) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                testUnsafe();
            }
        }.start();
        new Thread(){
            @Override
            public void run() {
                super.run();
                testSafe();
            }
        }.start();

    }

    private static void testSafe() {
        for (int i = 0; i < 20; i++) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    aiCount = addCount(aiCount);
                }
            }.start();
        }
        ThreadUtil.sleep(2000);
        LogTrack.w("线程安全 "+aiCount);
    }

    private static void testUnsafe() {
        for (int i = 0; i < 20; i++) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    count = addCount(count);
                }
            }.start();
        }
        ThreadUtil.sleep(2000);
        LogTrack.w("线程不安全 "+count);
    }

    private static int addCount(int count) {
        for (int i = 0; i < 300; i++) {
            String s = "asdhao" + i;
        }
        return count + 1;
    }

    private static AtomicInteger addCount(AtomicInteger aiCount) {
        for (int i = 0; i < 300; i++) {
            String s = "asdhao" + i;
        }
        aiCount.addAndGet(1);
        return aiCount;
    }

}


```

执行结果  
```
LogTrack  00:29:10:0255  WARN  [ (Test.java:47) #testSafe] 线程安全 20
LogTrack  00:29:10:0253  WARN  [ (Test.java:61) #testUnsafe] 线程不安全 17  
.....
LogTrack  00:30:11:0293  WARN  [ (Test.java:61) #testUnsafe] 线程不安全 18
LogTrack  00:30:11:0291  WARN  [ (Test.java:47) #testSafe] 线程安全 20
.....
LogTrack  00:30:26:0356  WARN  [ (Test.java:61) #testUnsafe] 线程不安全 16
LogTrack  00:30:26:0357  WARN  [ (Test.java:47) #testSafe] 线程安全 20
.....
LogTrack  00:30:47:0263  WARN  [ (Test.java:61) #testUnsafe] 线程不安全 18
LogTrack  00:30:47:0262  WARN  [ (Test.java:47) #testSafe] 线程安全 20
.....
LogTrack  00:31:03:0424  WARN  [ (Test.java:61) #testUnsafe] 线程不安全 19
LogTrack  00:31:03:0392  WARN  [ (Test.java:47) #testSafe] 线程安全 20
```
