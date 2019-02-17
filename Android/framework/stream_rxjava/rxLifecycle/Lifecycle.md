### 生命周期  

### onStart - onStop  

```
protected void onStart() {
    this.lifecycleSubject.onNext(RxLifeCycleEvent.START);
    super.onStart();
}

override fun onStart() {
    super.onStart()
    Observable.interval(1000, TimeUnit.MILLISECONDS)
            .compose(RxHelper.schedulersTransformer())
            .compose(RxHelper.addLifecycleTransformer(this))
            .compose(bindUntilEvent(RxLifeCycleEvent.STOP))
            .subscribe {
                LogTrack.i(it)
            }
}
```  
### onCreate - onDestroy  
```
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.lifecycleSubject.onNext(RxLifeCycleEvent.CREATE);
}

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState ?: Bundle())
    setContentView(R.layout.activity_main)
    Observable.interval(1000, TimeUnit.MILLISECONDS)
            .compose(RxHelper.schedulersTransformer())
            .compose(bindUntilEvent(RxLifeCycleEvent.DESTROY))
            .subscribe {
                LogTrack.i(it)
            }
}
```

### bindToLifecycle  
```
public <T> LifecycleTransformer<T> bindToLifecycle() {
    return RxLifecycle.bind(this.lifecycleSubject, LifecycleHelper.activityLifecycle());
}

Observable.interval(1000, TimeUnit.MILLISECONDS)
        .compose(RxHelper.schedulersTransformer())
        .compose(RxHelper.rxLifecycleTransformer(this))
        .subscribe {
            LogTrack.i(it)
        }
        
Observable.interval(1000, TimeUnit.MILLISECONDS)
        .compose(RxHelper.schedulersTransformer())
        .compose(bindToLifecycle())
        .subscribe {
            LogTrack.i(it)
        }
``` 

```

```