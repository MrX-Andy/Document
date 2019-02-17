### debounce 防抖动  

如果有多个事件被发射，那么在timeout之内的两个事件A  B，会发生替换的现象；  
也就是说，观察者最终收到的事件是B，接收不到事件A；  
如果在 timeout 内， 所有的连续事件 的时间间隔 都比较小， 下游将不会收到事件；  
```
public static void main(String[] args) {
    ObservableOnSubscribe<?> source = new ObservableOnSubscribe<Object>() {
        @Override
        public void subscribe(ObservableEmitter<Object> emitter) throws Exception {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        emitter.onNext("1");
                        Thread.sleep(100);
                        emitter.onNext("2");
                        Thread.sleep(150);
                        emitter.onNext("3");
                        Thread.sleep(200);
                        emitter.onNext("4");
                        Thread.sleep(250);
                        emitter.onNext("5");
                        Thread.sleep(300);
                        emitter.onNext("6");
                        Thread.sleep(350);
                        emitter.onNext("7");
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    };
    Observable.create(source)
            .debounce(151, TimeUnit.MILLISECONDS)
            .subscribe(new LiteObserver<Object>() {
                @Override
                public void onNext(Object result) {
                    LogTrack.i(result);
                }

            });


    SystemUtil.delayProcess(5000);
}
```

◆ 执行结果  
NetworkTest$2#onNext] 3    
NetworkTest$2#onNext] 4    
NetworkTest$2#onNext] 5    
NetworkTest$2#onNext] 6    
NetworkTest$2#onNext] 7    
