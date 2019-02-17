### takeWhile  
首先判断test的返回值， 是true则发送，是false则忽略；  
它和takeUntil的区别在于 先发射 还是 先判断 
```
Observable.just("1", "2", "3", "44", "5", "66")
        .takeWhile(new Predicate<String>() {
            @Override
            public boolean test(String result) throws Exception {
                return result.length() <= 1;
            }
        })
        .subscribe(new LiteObserver<String>() {
            @Override
            public void onNext(String result) {
                LogTrack.w(result);
            }
        });
```