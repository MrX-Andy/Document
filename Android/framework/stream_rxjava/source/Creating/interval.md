#### interval  

间隔重复（频率限定），类似于计数器（不会停下来）；  

```
Observable.interval(1000, TimeUnit.MILLISECONDS)
        .compose(RxHelper.schedulersTransformer())
        .compose(RxHelper.addLifecycleTransformer(this))
        .compose(bindUntilEvent(ActivityLifeCycleEvent.STOP))
        .subscribe {
            LogTrack.w(it)
        }
```
