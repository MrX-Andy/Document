### repeat  
使Observable 对象在发出 onNext() 通知之后重复发射数据。重做结束才会发出 onComplete() 通知，  
若重做过程中出现异常则会中断并发出 onError() 通知。  
使用场景：可使用该操作符指定一次任务执行完成后立即重复执行上一次的任务，如发送多次网络请求等。  

```
public final Observable<T> repeat(long times) {
    if (times < 0) {
        throw new IllegalArgumentException("times >= 0 required but it was " + times);
    }
    if (times == 0) {
        return empty();
    }
    return RxJavaPlugins.onAssembly(new ObservableRepeat<T>(this, times));
}
```  
