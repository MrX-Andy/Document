### autodispose
```
autodispose       : "0.6.1",

uber_autodispose                                   : "com.uber.autodispose:autodispose:${versions.autodispose}",
uber_autodispose_android                           : "com.uber.autodispose:autodispose-android:${versions.autodispose}",
uber_autodispose_android_archcomponents            : "com.uber.autodispose:autodispose-android-archcomponents:${versions.autodispose}",
uber_autodispose_android_archcomponents_test       : "com.uber.autodispose:autodispose-android-archcomponents-test:${versions.autodispose}",
uber_autodispose_rxlifecycle                       : "com.uber.autodispose:autodispose-rxlifecycle:${versions.autodispose}",
uber_autodispose_kotlin                            : "com.uber.autodispose:autodispose-kotlin:${versions.autodispose}",
uber_autodispose_kotlin_android                    : "com.uber.autodispose:autodispose-android-kotlin:${versions.autodispose}",
uber_autodispose_kotlin_android_archcomponents     : "com.uber.autodispose:autodispose-android-archcomponents-kotlin:${versions.autodispose}",
uber_autodispose_kotlin_android_archcomponents_test: "com.uber.autodispose:autodispose-android-archcomponents-test-kotlin:${versions.autodispose}",

```

```
implementation libs.uber_autodispose
implementation libs.uber_autodispose_android
implementation libs.uber_autodispose_android_archcomponents
implementation libs.uber_autodispose_kotlin
implementation libs.uber_autodispose_kotlin_android
implementation libs.uber_autodispose_kotlin_android_archcomponents
```

```
Observable.interval(1, TimeUnit.SECONDS)
        .doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                LogTrack.i("Disposing subscription from onStart()");
            }
        })
        .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
        .subscribe(new LiteObserver<Long>() {
            @Override
            public void onNext(Long result) {
                LogTrack.i("Started in onStart(), running until onStop(): " + result);
            }
        });
```
```
model.getNewsList(req)
        .compose(RxHelper.defaultTransformer(view, this))
        .compose(RxHelper.wrap2DataTransformer<WrapperBean<List<NewsListItemBean>>, List<NewsListItemBean>>(this))
        .autoDisposable(view.androidLifecycleScopeProvide())
        .subscribe(LiteObserver {
        
        }
```  

### 常见
1. onCreate 发起 onPause  结束  
.autoDisposable(this)  
2. onCreate 发起 onDestroy  结束  
.autoDisposable(view)  
3. onResume 发起 onPause  结束  
.autoDisposable(view,Lifecycle.Event.ON_PAUSE)  
4. 自定义 start  stop  
.liteDisposable(view)  


### 自定义-SimpleLifecycleScopeProvider  
LifeCycleEvent  
```
public interface LifeCycleEvent {
}
```
SimpleLifeCycleEvent  
```
public enum SimpleLifeCycleEvent implements LifeCycleEvent {
    START,
    STOP,
}
```  
SimpleLifecycleScopeProvider  
```
public class SimpleLifecycleScopeProvider implements LifecycleScopeProvider<SimpleLifeCycleEvent> {
    private final BehaviorSubject<SimpleLifeCycleEvent> lifecycleSubject;

    public SimpleLifecycleScopeProvider() {
        lifecycleSubject = BehaviorSubject.create();
    }

    @Override
    public Observable<SimpleLifeCycleEvent> lifecycle() {
        return lifecycleSubject.hide();
    }

    public void next(SimpleLifeCycleEvent lifeCycleEvent) {
        lifecycleSubject.onNext(lifeCycleEvent);
    }

    @SuppressWarnings("Convert2Lambda")
    @Override
    public Function<SimpleLifeCycleEvent, SimpleLifeCycleEvent> correspondingEvents() {
        return new Function<SimpleLifeCycleEvent, SimpleLifeCycleEvent>() {
            @SuppressWarnings("RedundantThrows")
            @Override
            public SimpleLifeCycleEvent apply(SimpleLifeCycleEvent simpleLifeCycleEvent)
                    throws Exception {
                if (simpleLifeCycleEvent == SimpleLifeCycleEvent.START) {
                    return SimpleLifeCycleEvent.STOP;
                }
                throw new LifecycleEndedException();
            }
        };
    }

    @Nullable
    @Override
    public SimpleLifeCycleEvent peekLifecycle() {
        return lifecycleSubject.getValue();
    }
}

```  
### 自定义-使用  
```
private val lifecycleScopeProvider: SimpleLifecycleScopeProvider = SimpleLifecycleScopeProvider()  
override fun onClickEvent(v: View) {
    if (R.id.tvLogin == v.id) {
//            presenter.loginWithLiveData(LoginReq()).observeBlock(view) { result ->
//                LogTrack.i(result)
//            }
        lifecycleScopeProvider.next(SimpleLifeCycleEvent.START)
        LogTrack.i("点击登录")
        loginWithLiveData3(LoginReq())
        return
    }
    if (R.id.tvLogout == v.id) {
        LogTrack.i("点击退出")
        lifecycleScopeProvider.next(SimpleLifeCycleEvent.STOP)
        return
    }

}

private fun loginWithLiveData3(req: LoginReq): LiveData<WrapperEntity<UserEntity>> {
    val loginRepLiveData = MutableLiveData<WrapperEntity<UserEntity>>()
    //  AndroidLifecycleScopeProvider   LifecycleScopeProvider
    func@ StringObservable(5 * 1000)
            .map { SuccessWrapperEntity(UserEntity()) }
            .compose(RxHelper.defaultTransformer(presenter))
            .compose(RxHelper.wrap2DataTransformer<WrapperEntity<UserEntity>, UserEntity>(presenter))
            .doFinally {
                LogTrack.i("doFinally")
            }
            .autoDisposable(lifecycleScopeProvider)
            .subscribe(object : LiteObserver<UserEntity>() {
                override fun onNext(result: UserEntity) {
                    SpUtil.putString(C.SpKey.userToken, result.userToken)
                    SpUtil.putString(C.SpKey.userId, result.id)
                    loginRepLiveData.value = SuccessWrapperEntity(UserEntity(nickname = "alex", phone = "13023033043"))
                    LogTrack.w("开始")
                    return@func loginRepLiveData
                }

                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                }

                override fun onComplete() {
                    super.onComplete()
                    LogTrack.w("结束")
                }
            })
    view.dismissLoadingView()
    return loginRepLiveData
}
```
### 自定义-运行结果  
```
2018-07-09 13:33:35.176 21012-21012/com.alex.andfun.account I/LogTrack: [ (LoginViewModel.kt:67) #onClickEvent] 点击登录
2018-07-09 13:33:35.178 21012-21012/com.alex.andfun.account I/LogTrack: [ (SimpleLifecycleScopeProvider.java:54) #peekLifecycle] event = START
2018-07-09 13:33:36.360 21012-21012/com.alex.andfun.account I/LogTrack: [ (LoginViewModel.kt:72) #onClickEvent] 点击退出
2018-07-09 13:33:36.361 21012-21012/com.alex.andfun.account I/LogTrack: [ (AndBasePresenter.java:135) #onObtainFinish] 结束了
2018-07-09 13:33:36.362 21012-21012/com.alex.andfun.account I/LogTrack: [ (LoginViewModel.kt:88) LoginViewModel$loginWithLiveData3$2#run] doFinally

```

### 参考  
https://blog.csdn.net/mq2553299/article/details/78927617  
https://www.jianshu.com/p/8490d9383ba5  


