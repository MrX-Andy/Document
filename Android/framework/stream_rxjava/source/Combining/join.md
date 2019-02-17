### join  

Observable.join(other,leftEnd,rightEnd,resultSelector)  

```
Observable<String> observable1 = Observable.create(new ObservableOnSubscribe<String>() {
    @Override
    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    emitter.onNext("1");
                    Thread.sleep(100);
                    emitter.onNext("2");
                    Thread.sleep(300);
                    emitter.onNext("3");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
});

Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
    @Override
    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(800);
                    emitter.onNext("11");
                    Thread.sleep(600);
                    emitter.onNext("22");
                    Thread.sleep(500);
                    emitter.onNext("33");
                    Thread.sleep(400);
                    emitter.onNext("44");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
});

observable1.join(observable2,
        new Function<String, ObservableSource<Long>>() {
            @Override
            public ObservableSource<Long> apply(String result) throws Exception {
                /* source Observable 的有效时间*/
                return Observable.timer(8000, TimeUnit.MILLISECONDS);
            }
        },
        new Function<String, ObservableSource<Long>>() {
            @Override
            public ObservableSource<Long> apply(String result) throws Exception {
                /* other Observable 的有效时间*/
                return Observable.timer(0, TimeUnit.MILLISECONDS);
            }
        },
        new BiFunction<String, String, String>() {
            @Override
            public String apply(String tLeft, String tRight) throws Exception {
                return "tLeft = " + tLeft + "  tRight = " + tRight;
            }
        }).subscribe(new LiteObserver<String>() {
    @Override
    public void onNext(String result) {
        LogTrack.w(result);
    }
});
```  
第一个Function的apply函数，return Observable.timer(8000, TimeUnit.MILLISECONDS);  表示第一个Observable也就是源事件源的有效期是8000毫秒；  
第二个Function的apply函数，return Observable.timer(0, TimeUnit.MILLISECONDS);  表示第二个Observable也就是目标事件源的有效期是0毫秒；    
如果源事件源 超过有效期，目标事件源 再发送任何事件流， 下游都不会收到任何事件；  
如果还在源事件源有效期，假设 源事件源 已经发射 1, 2, 3 元素； 目标事件源再发射元素11， 下游 会收到 ：  
tLeft = 1  tRight = 11  
tLeft = 2  tRight = 11  
tLeft = 3  tRight = 11  

