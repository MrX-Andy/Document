### skipUntil  
main可能先产生事件， 但必须other产生事件， 下游才会收到 事件；  
main.skipUntil(ObservableSource other)  
```
Observable.create(new ObservableOnSubscribe<String>() {
        @Override
        public void subscribe(ObservableEmitter<String> emitter) throws Exception {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        emitter.onNext("1");
                        emitter.onNext("2");
                        emitter.onNext("3");
                        Thread.sleep(100);
                        emitter.onNext("4");
                        emitter.onNext("5");
                        emitter.onNext("6");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    })
            .skipUntil(Observable.just("11", "22", "33").delay(80, TimeUnit.MILLISECONDS))
            .subscribe(new LiteObserver<String>() {
                @Override
                public void onNext(String result) {
                    LogTrack.w(result);
                }
            });
```