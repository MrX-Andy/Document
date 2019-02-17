### defaultIfEmpty  
上游事件为空， 给下游一个默认元素；  
```
ObservableOnSubscribe<String> source = new ObservableOnSubscribe<String>() {
    @Override
    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
        emitter.onNext("1");
        emitter.onComplete();
    }
};
Observable.create(source).defaultIfEmpty("4")
        .subscribe(new LiteObserver<String>() {
            @Override
            public void onNext(String result) {
                LogTrack.w(result);
            }
        });
```