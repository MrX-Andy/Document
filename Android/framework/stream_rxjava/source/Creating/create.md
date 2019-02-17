#### Observable.create(source).subscribe  

◆ Observable#create  
public static <T> Observable<T> create(ObservableOnSubscribe<T> source)  
只是创建了并返回 一个Observable的子类ObservableCreate；  
```
public static <T> Observable<T> create(ObservableOnSubscribe<T> source) {
    ObjectHelper.requireNonNull(source, "source is null");
    return RxJavaPlugins.onAssembly(new ObservableCreate<T>(source));
}
```  
因为这种模式下，属于cold事件流，没有订阅者订阅，事件是不会流动的；  

◆ Observable#subscribe  
public final void subscribe(Observer<? super T> observer)  
```
@Override
public final void subscribe(Observer<? super T> observer) {
    ObjectHelper.requireNonNull(observer, "observer is null");
    try {
        observer = RxJavaPlugins.onSubscribe(this, observer);

        ObjectHelper.requireNonNull(observer, "Plugin returned null Observer");

        // 会调用 ObservableCreate 的 subScribeActual 方法
        subscribeActual(observer);
    } catch (NullPointerException e) { // NOPMD
        throw e;
    } catch (Throwable e) {
        ...
    }
}
```  

◆ ObservableCreate#subscribeActual  
protected void subscribeActual(Observer<? super T> observer)  
```
@Override
protected void subscribeActual(Observer<? super T> observer) {
    //这个observer对象，就是Observable.create(source).subscribe(observer) 传过来的 observer
    CreateEmitter<T> parent = new CreateEmitter<T>(observer);
    observer.onSubscribe(parent);

    try {
        // 这个source对象，就是调用 Observable.create(source) 时，传入的source对象；
        // 所以这个时候，source的subscribe方法会被执行；  
        source.subscribe(parent);
    } catch (Throwable ex) {
        Exceptions.throwIfFatal(ex);
        parent.onError(ex);
    }
}
```  

◆ source  
这个source是ObservableOnSubscribe的实例化对象  
```
ObservableOnSubscribe<String> source = new ObservableOnSubscribe<String>() {
    @Override
    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
        // 这个 emitter 就是 上一步 ObservableCreate#subscribeActual 中，创建出来的；  
        // 
        emitter.onNext("A");
        emitter.onNext("B");
        emitter.onNext("C");
        emitter.onComplete();
    }
};
```  
◆ CreateEmitter  
ObservableCreate.CreateEmitter#onNext  
```
@Override
public void onNext(T t) {
    if (t == null) {
        onError(new NullPointerException("onNext called with null. Null values are generally not allowed in 2.x operators and sources."));
        return;
    }
    if (!isDisposed()) {
        //这个observer对象，就是Observable.create(source).subscribe(observer) 传过来的 observer
        observer.onNext(t);
    }
}
```  

◆ 概述  
RxJava的事件流设计的比较巧妙，Observable的静态方法create的，  
入口参数是一个ObservableOnSubscribe的实例化对象 source，  
出口参数是Observable的子类ObservableCreate的实例化对象，  
如果不对Observable进行subscribe，是不会有事件产生于发射的，  
在Observable调用subscribe之后，会调用ObservableOnSubscribe的subscribe方法，  
这个时候调用emitter.onNext("A"); 一个一个产生事件，这时emitter会调用observer的onNext方法，消费事件；  

