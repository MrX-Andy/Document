### retry_BiPredicate  
在执行 Observable对象的序列出现异常时，不直接发出 onError() 通知，而是重新订阅该 Observable对象，  
直到重做过程中未出现异常，则会发出 onNext() 和 onCompleted()通知；  
若重做过程中也出现异常，则会继续重试，直到达到重试次数上限，超出次数后发出最新的 onError() 通知。  
使用场景：网络等请求异常出错后，可重新发起请求。  

```
public static void main(String[] args) {


    ObservableOnSubscribe<String> source = new ObservableOnSubscribe<String>() {
        @Override
        public void subscribe(ObservableEmitter<String> emitter) throws Exception {
            emitter.onNext("A");
            emitter.onNext("B");
            emitter.onError(new Throwable());
        }
    };
    Observable.create(source).retry(new BiPredicate<Integer, Throwable>() {
        @Override
        public boolean test(Integer integer, Throwable throwable) throws Exception {
            LogTrack.w("integer = " + integer);
            if (integer <= 3) {
                return true;
            } else {
                return false;
            }
        }
    })
            .subscribe(new LiteObserver<Object>() {
                @Override
                public void onNext(Object result) {
                    LogTrack.w(result);
                }
            });
    SystemUtil.delayProcess(5000);
}
```
