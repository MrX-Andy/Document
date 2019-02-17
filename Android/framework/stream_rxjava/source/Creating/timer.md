### timer  
创建一个在指定延迟时间后发射一条数据（固定值：0）的 Observable 对象。  
```
Observable.timer(1000, TimeUnit.MILLISECONDS)
        .subscribe(new LiteObserver<Object>() {
            @Override
            public void onNext(Object result) {
                LogTrack.w(result);
            }
        });
```
