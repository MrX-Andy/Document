### first  
如果上游有onNext("1)， first("5")取出来的是 1，  
如果上游只有 onComplete， first("5")取出来的是 5，  

```
ObservableOnSubscribe<String> source = new ObservableOnSubscribe<String>() {
    @Override
    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
//                emitter.onNext("1");
        emitter.onComplete();
    }
};
Observable.create(source)
        .first("5")
        .subscribe(new LiteConsumer<String>() {
            @Override
            public void onNext(String result) {
                LogTrack.w(result);
            }
        });

```
