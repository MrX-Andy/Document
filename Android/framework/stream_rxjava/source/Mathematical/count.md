### count  
计算元素的个数  
```
Observable.just(1, 2, 6, 4, 5)
    .count()
    .subscribe(new LiteConsumer<Long>() {
        @Override
        public void onNext(Long result) {
            LogTrack.w(result);
        }
    });
```
