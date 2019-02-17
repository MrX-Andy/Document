### takeUntil  
首先发射一个元素， 当test返回true 立刻停止；  
下游仍然会收到第一个44，收不到5   66；  
```
Observable.just("1", "2", "3", "44", "5", "66")
        .takeUntil(new Predicate<String>() {
            @Override
            public boolean test(String result) throws Exception {
                return result.length() > 1;
            }
        })
        .subscribe(new LiteObserver<String>() {
            @Override
            public void onNext(String result) {
                LogTrack.w(result);
            }
        });
```