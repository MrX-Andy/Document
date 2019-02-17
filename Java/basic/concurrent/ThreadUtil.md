### ThreadUtil  
```
package org.alex.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings({"Duplicates", "WeakerAccess", "unused"})
public class ThreadUtil {
    public static void sleep(long millis) {
        if (millis <= 0) {
            return;
        }
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // e.printStackTrace();
        }
    }

    public static <T> T submit(Future<T> submit) {
        return submit(submit, null);
    }

    public static <T> T submit(Future<T> submit, T error) {
        if (submit == null) {
            return error;
        }
        try {
            return submit.get();
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return error;
    }

    public static <T> List<Future<T>> invokeAll(ExecutorService executorService, List<Callable<T>> futureList) {
        return invokeAll(executorService, futureList, new ArrayList<>());
    }

    public static <T> List<Future<T>> invokeAll(ExecutorService executorService, List<Callable<T>> futureList, List<Future<T>> error) {
        if (executorService == null || futureList == null) {
            return error;
        }
        try {
            return executorService.invokeAll(futureList);
        } catch (InterruptedException e) {
            // e.printStackTrace();
        }
        return error;
    }

    public static <T> T invokeAny(ExecutorService executorService, List<Callable<T>> futureList) {
        return invokeAny(executorService, futureList, null);
    }

    public static <T> T invokeAny(ExecutorService executorService, List<Callable<T>> futureList, T error) {
        if (executorService == null || futureList == null) {
            return error;
        }
        try {
            return executorService.invokeAny(futureList);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return error;
    }

    public static <T> T invokeAny(ExecutorService executorService, List<Callable<T>> futureList, long timeout, TimeUnit unit, T error) {
        if (executorService == null || futureList == null) {
            return error;
        }
        if (timeout <= 0) {
            timeout = 0;
        }
        try {
            return executorService.invokeAny(futureList, timeout, unit);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return error;
    }

    public static <T> T get(Future<T> future) {
        if (future == null) {
            return null;
        }
        return get(future, null);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static <T> T get(Future<T> future, T error) {
        if (future == null) {
            return error;
        }
        try {
            return future.get();
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return error;
    }

    public static <T> T get(ScheduledFuture<T> scheduledFuture, T error) {
        if (scheduledFuture == null) {
            return error;
        }
        try {
            return scheduledFuture.get();
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return error;
    }

    public static Object getObj(ScheduledFuture<?> scheduledFuture) {
        if (scheduledFuture == null) {
            return null;
        }
        return getObj(scheduledFuture, null);
    }

    public static Object getObj(ScheduledFuture<?> scheduledFuture, Object error) {
        if (scheduledFuture == null) {
            return error;
        }
        try {
            return scheduledFuture.get();
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return error;
    }

    public static ThreadPoolExecutor newExecutor() {
        return newExecutor(16, 32, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(128), new ThreadPoolExecutor.DiscardPolicy());
    }

    public static ThreadPoolExecutor newExecutor(BlockingQueue<Runnable> workQueue,
                                                 RejectedExecutionHandler handler) {
        return newExecutor(16, 32, 30, TimeUnit.SECONDS, workQueue, handler);
    }

    public static ThreadPoolExecutor newExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                                 BlockingQueue<Runnable> workQueue,
                                                 RejectedExecutionHandler handler) {
        if (workQueue == null) {
            workQueue = new ArrayBlockingQueue<>(128);
        }
        if (handler == null) {
            handler = new ThreadPoolExecutor.DiscardPolicy();
        }
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }


    public static <T> Future<T> take(CompletionService<T> completionService) {
        return take(completionService, null);
    }

    public static <T> Future<T> take(CompletionService<T> completionService, Future<T> error) {
        if (completionService == null) {
            return error;
        }
        try {
            return completionService.take();
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return error;
    }

    public static <T> T takeGet(CompletionService<T> completionService, T error) {
        if (completionService == null) {
            return error;
        }
        try {
            Future<T> take = completionService.take();
            if (take == null) {
                return error;
            }
            return take.get();
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return error;
    }

    public static void tryLock(ReentrantLock lock, long timeout) {
        if (lock == null) {
            return;
        }
        try {
            lock.tryLock(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // e.printStackTrace();
        }
    }

    public static void tryLock(ReentrantLock lock, long timeout, TimeUnit unit) {
        if (lock == null) {
            return;
        }
        try {
            lock.tryLock(timeout, unit);
        } catch (InterruptedException e) {
            // e.printStackTrace();
        }
    }

    public static void await(CountDownLatch latch) {
        if (latch == null) {
            return;
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

```