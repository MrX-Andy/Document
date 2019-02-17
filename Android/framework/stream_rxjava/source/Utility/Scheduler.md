### Scheduler  

subscribeOn 切换的是 调用subscribeOn之前的线程。  
observeOn 切换的是 调用observeOn之后的线程。  
observeOn之后，不可再调用subscribeOn 切换线程。  

只有第一subscribeOn() 起作用（所以多个 subscribeOn() 无意义）；  
observeOn() 可以使用多次，每个 observeOn() 将导致一次线程切换()，都是影响其后的操作的线程；  
不论是 subscribeOn() 还是 observeOn()，每次线程切换如果不受到下一个 observeOn() 的干预，线程将不再改变，不会自动切换到其他线程。  
如果没有调用过observableOn，那么所有的操作，都是受subscribeOn的影响，也就是第一次调用subscribeOn的影响；  
◆ 对应类    
Schedulers#io = IoScheduler  
Schedulers#newThread = NewThreadScheduler    
Schedulers#computation = ComputationScheduler    
Schedulers#trampoline = TrampolineScheduler    
Schedulers#single = SingleScheduler    

◆ 示例  
只有第一subscribeOn() 起作用（所以多个 subscribeOn() 无意义）；  
```
Observable.just("你好")
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .map(new Function<String, String>() {
            @Override
            public String apply(String inValue) throws Exception {
                LogTrack.i(Looper.myLooper() == Looper.getMainLooper());  //true  主线程
                return inValue + "，被修改了";
            }
        })
        .subscribeOn(AndroidSchedulers.mainThread())
        .map(new Function<String, String>() {
            @Override
            public String apply(String inValue) throws Exception {
                LogTrack.i(Looper.myLooper() == Looper.getMainLooper());  //true  主线程
                return inValue + "，被修改了";
            }
        })

Observable.just("你好")
        .subscribeOn(Schedulers.io())
        .subscribeOn(AndroidSchedulers.mainThread())
        .map(new Function<String, String>() {
            @Override
            public String apply(String inValue) throws Exception {
                LogTrack.i(Looper.myLooper() == Looper.getMainLooper());  //false  子线程
                return inValue + "，被修改了";
            }
        })
        .subscribeOn(AndroidSchedulers.mainThread())
        .map(new Function<String, String>() {
            @Override
            public String apply(String inValue) throws Exception {
                LogTrack.i(Looper.myLooper() == Looper.getMainLooper());  //false  子线程
                return inValue + "，被修改了";
            }
        })
```
◆ 示例  
observeOn() 可以使用多次，每个 observeOn() 将导致一次线程切换()，都是影响其后的操作的线程；    
```
Observable.just("你好")
                .observeOn(Schedulers.io())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String inValue) throws Exception {
                        LogTrack.i(Looper.myLooper() == Looper.getMainLooper());  // false 子线程
                        return inValue + "，被修改了";
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String inValue) throws Exception {
                        LogTrack.i(Looper.myLooper() == Looper.getMainLooper());  // true  主线程
                        return inValue + "，被修改了";
                    }
                })
                .observeOn(Schedulers.io())
                .subscribe(new LiteObserver<String>() {
                    @Override
                    public void onNext(String result) {
                        LogTrack.i(Looper.myLooper() == Looper.getMainLooper());  // false 子线程
                        LogTrack.i(result);
                    }
                });
```
### subscribeOn  
```
Observable#subscribe(Consumer onNext)  
Observable#subscribe(Consumer onNext,  Consumer onError,  Action onComplete, Consumer onSubscribe)  
Observable#subscribe(Observer observer)  
ObservableSubscribeOn#subscribeActual
Scheduler#scheduleDirect  
IoScheduler.EventLoopWorker#schedule  
NewThreadWorker#scheduleActual  
```

### observeOn  
为什么 subscribe{ } 可以工作在UI线程?    
1.. 先看调用顺序  
```
Observable#subscribe(Consumer onNext)  
Observable#subscribe(Consumer onNext,  Consumer onError,  Action onComplete, Consumer onSubscribe)  
Observable#subscribe(Observer observer)  
ObservableObserveOn#subscribeActual  
ObservableObserveOn.ObserveOnObserver#onNext  
ObservableObserveOn.ObserveOnObserver#schedule  
HandlerScheduler.HandlerWorker#schedule  
Handler#handleCallback
HandlerScheduler.ScheduledRunnable#run  
```
2.. 切换到UI线程  
```
public Disposable schedule(Runnable run, long delay, TimeUnit unit) {
    if (run == null) throw new NullPointerException("run == null");
    if (unit == null) throw new NullPointerException("unit == null");

    if (disposed) {
        return Disposables.disposed();
    }

    run = RxJavaPlugins.onSchedule(run);

    //  A
    ScheduledRunnable scheduled = new ScheduledRunnable(handler, run);

    //  B
    Message message = Message.obtain(handler, scheduled);
    message.obj = this; // Used as token for batch disposal of this worker's runnables.

    if (async) {
        message.setAsynchronous(true);
    }

    //  C
    handler.sendMessageDelayed(message, unit.toMillis(delay));

    // Re-check disposed state for removing in case we were racing a call to dispose().
    if (disposed) {
        handler.removeCallbacks(scheduled);
        return Disposables.disposed();
    }

    return scheduled;
}

```
BC 处代码的实现逻辑, 怎么能将当前事件, 切换到UI线程?  
Message.callback 为什么会工作在UI线程?  
[参见](/Android/basic/handler/library/message_callback.md)    

