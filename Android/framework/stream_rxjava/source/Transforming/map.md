#### map  

apply方法法人入口参数是T， 出口参数是R   
所以map做的事情是，T  to R  
```
public interface Function<T, R> {
    R apply(@NonNull T t) throws Exception;
}

@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
public final <R> Observable<R> map(Function<? super T, ? extends R> mapper) {
    ObjectHelper.requireNonNull(mapper, "mapper is null");
    return RxJavaPlugins.onAssembly(new ObservableMap<T, R>(this, mapper));
}
```
map方法，直接返回了一个ObservableMap对象，其仍然是一个cold stream，需要subscribe才会有事件的发射；  
observable.subscribe仍然会回调其子类ObservableMap的subscribeActual方法；  

◆ ObservableMap#subscribeActual  
```
@Override
public void subscribeActual(Observer<? super U> t) {
    //所以我们应该关注MapObservable的onNext方法体
    source.subscribe(new MapObserver<T, U>(t, function));
}
```  
◆ ObservableMap.MapObserver#onNext  
```
@Override
public void onNext(T t) {
    ...
    U v;
    try {
        // 所以，最终的结果，是交给了 Function<T, R>的方法，R apply(@NonNull T t)
        // apply的返回值，就是stream的事件流对象  
        v = ObjectHelper.requireNonNull(mapper.apply(t), "The mapper function returned a null value.");
    } catch (Throwable ex) {
        fail(ex);
        return;
    }
    actual.onNext(v);
}
```  
