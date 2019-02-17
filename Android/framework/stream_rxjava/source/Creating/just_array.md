#### just  
```
public static <T> Observable<T> just(T item1, T item2) {
    ObjectHelper.requireNonNull(item1, "The first item is null");
    ObjectHelper.requireNonNull(item2, "The second item is null");
    return fromArray(item1, item2);
}
```  
◆ Observable#fromArray   
```
public static <T> Observable<T> fromArray(T... items) {
    ...
    return RxJavaPlugins.onAssembly(new ObservableFromArray<T>(items));
}
```
其仍然是一个cold stream，需要subscribe才会有事件的发射；  
observable.subscribe仍然会回调其子类ObservableFromArray的subscribeActual方法；  

◆ ObservableFromArray#subscribeActual  
```
@Override
public void subscribeActual(Observer<? super T> s) {
    FromArrayDisposable<T> d = new FromArrayDisposable<T>(s, array);
    // 这里会 回调observer的onSubscribe方法；
    s.onSubscribe(d);
    if (d.fusionMode) {
        return;
    }
    // 真正会触发 发事件的，是这个run方法
    d.run();
}
```  
◆ ObservableFromArray.FromArrayDisposable#run  
```
void run() {
    T[] a = array;
    int n = a.length;

    for (int i = 0; i < n && !isDisposed(); i++) {
        T value = a[i];
        if (value == null) {
            actual.onError(new NullPointerException("The " + i + "th element is null"));
            return;
        }
        actual.onNext(value);
    }
    if (!isDisposed()) {
        actual.onComplete();
    }
}
```  


