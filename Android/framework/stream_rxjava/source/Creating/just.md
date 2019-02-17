#### just  
```
@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
public static <T> Observable<T> just(T item) {
    ObjectHelper.requireNonNull(item, "The item is null");
    return RxJavaPlugins.onAssembly(new ObservableJust<T>(item));
}
```  
其仍然是一个cold stream，需要subscribe才会有事件的发射；  
observable.subscribe仍然会回调其子类ObservableJust的subscribeActual方法；  

◆ ObservableJust#subscribeActual  
```
@Override
protected void subscribeActual(Observer<? super T> s) {
    ScalarDisposable<T> sd = new ScalarDisposable<T>(s, value);
    // 这里会 回调observer的onSubscribe方法；
    s.onSubscribe(sd);
    // 真正会触发 发事件的，是这个run方法  
    sd.run();
}
```  
◆ ObservableScalarXMap.ScalarDisposable#run  
```
@Override
public void run() {
    if (get() == START && compareAndSet(START, ON_NEXT)) {
        observer.onNext(value);
        if (get() == ON_NEXT) {
            lazySet(ON_COMPLETE);
            observer.onComplete();
        }
    }
}
```  

