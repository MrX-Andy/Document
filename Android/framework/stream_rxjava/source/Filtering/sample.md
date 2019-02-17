### sample  

```
Observable.interval(200, TimeUnit.MILLISECONDS)
        .sample(1000, TimeUnit.MILLISECONDS)
        .subscribe(new LiteObserver<Long>() {
            @Override
            public void onNext(Long result) {
                LogTrack.w(result);
            }
        });
```  
上游事件 200毫秒发送一个， 下游1000毫秒接收一个，会被当做忽略；  

