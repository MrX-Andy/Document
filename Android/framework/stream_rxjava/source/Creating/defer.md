### defer  
defer操作符与create、just、from等操作符一样，不过相关的数据都是在订阅是才生效的。  
在观察者订阅之前不创建这个Observable，为每一个观察者创建一个新的Observable;  
在订阅的时候才会创建 Observable 对象；  
每一次订阅都创建一个新的 Observable 对象。  
使用场景：可以使用该操作符封装需要被多次执行的函数。  

```
Callable<? extends ObservableSource<?>> supplier = new Callable<ObservableSource<?>>() {
    @Override
    public ObservableSource<?> call() throws Exception {
        return Observable.just("Hello");
    }
};
Observable.defer(supplier)
        .subscribe(new LiteObserver<Object>() {
            @Override
            public void onNext(Object result) {
                LogTrack.w(result);
            }
        });
```
