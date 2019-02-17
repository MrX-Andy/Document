### sequenceEqual  
判断两个事件流的 元素 是否 完全一致  

```
Observable<String> just1 = Observable.just("1", "2", "3", "4", "5", "6").delay(20, TimeUnit.MILLISECONDS);
Observable<String> just1_1 = Observable.just("1", "2", "3", "4", "5", "6").delay(30, TimeUnit.MILLISECONDS);
Observable.sequenceEqual(just1, just1_1)
        .subscribe(new LiteConsumer<Boolean>() {
            @Override
            public void onNext(Boolean result) {
                LogTrack.w(result);
            }
        });
```