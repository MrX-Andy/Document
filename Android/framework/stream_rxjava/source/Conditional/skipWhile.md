### skipWhile  
test 返回 true 表示 跳过， 在test第一次返回false之后的所有事件源， 下游都会收到的；  
```
Observable.just("1", "2", "3", "44", "5")
        .skipWhile(new Predicate<String>() {
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