### zip  
将多个并发的Observable 合并成一个Observable  

◆ 示例 1  
```
Observable.zip(observable1, observable2, new BiFunction<String, String, String>() {
    @Override
    public String apply(String left, String right) throws Exception {
        return left + "::" + right;
    }
}).subscribe(new LiteObserver<String>() {
    @Override
    public void onNext(String result) {
        LogTrack.w(result);
    }
});
```  
◆ 示例 2  
```
List<Observable<String>> list = new ArrayList<>();
list.add(observable1);
list.add(observable2);
Observable.zip(list, new Function<Object[], Object>() {
    @Override
    public Object apply(Object[] objects) throws Exception {
        return objects[0] + "::" + objects[1];
    }
}).subscribe(new LiteObserver<Object>() {
    @Override
    public void onNext(Object result) {
        LogTrack.w(result);
    }
});
```
### ObservableZip.ZipCoordinator#drain  
假设有两个数据源, justA, justB  
justA  发送 A0, A1, A2  ...  
justB  发送 B0, B1, B2  ...  
final T[] os = row;  
第一次进入 for循环, os[0] 一定为空, z.queue.poll() = justA调用出队, 并且 empty = false, 将 A0 加入 os[0];  
继续循环, os[1]一定为空,  z.queue.poll() = justB调用出队, 并且 empty = true, emptyCount++,  判断 emptyCount != 0 , break;   
第二次进入for循环, os[0] 非空, 里面存放的是 A0, 不需要关注;  
继续循环, os[1]为空,  z.queue.poll() = justA调用出队, 并且 empty = true, emptyCount++;   
继续循环, os[1]为空,  z.queue.poll() = justB调用出队, 并且 empty = false, 判断 emptyCount != 0 , break;  
第三次进入for循环,    事件处理完成, checkTerminated = true, 返回函数体;  

总体来说, 就是, 从Observable队列中,取出数据, 放在一个数组中, 每构成一个组合数据, 会调用下游处理事件,  当所有的事件处理完成, 跳出 循环体;  

```
public void drain() {
    if (getAndIncrement() != 0) {
        return;
    }

    int missing = 1;

    final ZipObserver<T, R>[] zs = observers;
    final Observer<? super R> a = downstream;
    final T[] os = row;
    final boolean delayError = this.delayError;

    for (;;) {

        for (;;) {
            int i = 0;
            int emptyCount = 0;
            for (ZipObserver<T, R> z : zs) {
                if (os[i] == null) {
                    boolean d = z.done;
                    T v = z.queue.poll();
                    boolean empty = v == null;

                    if (checkTerminated(d, empty, a, delayError, z)) {
                        return;
                    }
                    if (!empty) {
                        os[i] = v;
                    } else {
                        emptyCount++;
                    }
                } else {
                    if (z.done && !delayError) {
                        Throwable ex = z.error;
                        if (ex != null) {
                            cancel();
                            a.onError(ex);
                            return;
                        }
                    }
                }
                i++;
            }

            if (emptyCount != 0) {
                break;
            }

            R v;
            try {
                v = ObjectHelper.requireNonNull(zipper.apply(os.clone()), "The zipper returned a null value");
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                cancel();
                a.onError(ex);
                return;
            }

            a.onNext(v);

            Arrays.fill(os, null);
        }

        missing = addAndGet(-missing);
        if (missing == 0) {
            return;
        }
    }
}
```