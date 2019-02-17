### takeLast  
只发送最后count个 事件，之后就算产生事件，也不会发送  

```
@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
public final Observable<T> take(long count) {
    if (count < 0) {
        throw new IllegalArgumentException("count >= 0 required but it was " + count);
    }
    return RxJavaPlugins.onAssembly(new ObservableTake<T>(this, count));
}
```