### ignoreElements  
过滤掉所有的元素，不接收onNext 方法；  
```
Observable.create(source)
        .ignoreElements()
        .subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(Throwable e) {

            }
        });
```  
