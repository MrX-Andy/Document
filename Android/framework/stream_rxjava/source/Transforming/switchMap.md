### switchMap  

apply方法法人入口参数是T， 出口参数是Observable<R>   
所以map做的事情是，T  to Observable<R>  
和flatMap的区别就是，apply收到事件之后，下游的observer收到的事件全是来自于这个apply方法的，也会停止对上游事件的监测，即使它仍在发射；
flatMap则不然，只要有收到，上游事件仍会影响到下游事件；  

◆ 测试代码  
```
 public static void main(String[] args) {
        ObservableOnSubscribe<?> source = new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            emitter.onNext("1");
                            Thread.sleep(100);
                            emitter.onNext("2");
                            Thread.sleep(100);
                            emitter.onNext("3");
                            Thread.sleep(100);
                            emitter.onNext("4");
                            Thread.sleep(100);
                            emitter.onNext("5");
                            Thread.sleep(100);
                            emitter.onNext("6");
                            Thread.sleep(100);
                            emitter.onNext("7");
                            Thread.sleep(100);
                            emitter.onComplete();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        };
        Observable.create(source)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new LiteConsumer<Object>() {
                    @Override
                    public void onNext(Object result) {
//                        LogTrack.w("result = "+result);
                    }
                })
                .switchMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object result) throws Exception {
                        LogTrack.w("result = "+result);

                        return Observable.just(result + "  修改").subscribeOn(Schedulers.io()).delay(100, TimeUnit.MILLISECONDS);
                    }
                })
                .subscribe(new LiteObserver<Object>() {
                    @Override
                    public void onNext(Object result) {
                        LogTrack.i(result);
                    }
                });


        SystemUtil.delayProcess(5000);
    }
```
◆ 测试结果  
NetworkTest$5#apply] result = 1  
NetworkTest$5#apply] result = 2  
NetworkTest$5#apply] result = 3  
NetworkTest$5#apply] result = 4  
NetworkTest$5#apply] result = 5  
NetworkTest$5#apply] result = 6  
NetworkTest$5#apply] result = 7  
NetworkTest$4#onNext] 7  修改  
◆ Observable#switchMap  
```
@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
public final <R> Observable<R> switchMap(Function<? super T, ? extends ObservableSource<? extends R>> mapper) {
    return switchMap(mapper, bufferSize());
}

@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
public final <R> Observable<R> switchMap(Function<? super T, ? extends ObservableSource<? extends R>> mapper, int bufferSize) {
    ObjectHelper.requireNonNull(mapper, "mapper is null");
    ObjectHelper.verifyPositive(bufferSize, "bufferSize");
    if (this instanceof ScalarCallable) {
        @SuppressWarnings("unchecked")
        T v = ((ScalarCallable<T>)this).call();
        if (v == null) {
            return empty();
        }
        return ObservableScalarXMap.scalarXMap(v, mapper);
    }
    return RxJavaPlugins.onAssembly(new ObservableSwitchMap<T, R>(this, mapper, bufferSize, false));
}
```  

◆ ObservableSwitchMap#subscribeActual  
```
@Override
public void subscribeActual(Observer<? super R> t) {
    if (ObservableScalarXMap.tryScalarXMapSubscribe(source, t, mapper)) {
        return;
    }
    source.subscribe(new SwitchMapObserver<T, R>(t, mapper, bufferSize, delayErrors));
}
```  
