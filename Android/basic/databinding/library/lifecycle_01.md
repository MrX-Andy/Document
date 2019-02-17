#### OnLifecycleEvent 源码分析  
Activity 实现 LifecycleOwner  接口  
Activity 内部持有 LifecycleRegistry  对象, LifecycleRegistry 继承与 Lifecycle   
LifecycleRegistryOwner 继承与 LifecycleOwner, 但是被废弃了;  

在 Activity中, LifeRegistry做了哪些事情?    
关联 onSaveInstanceState  
```
@CallSuper
@Override
protected void onSaveInstanceState(Bundle outState) {
    mLifecycleRegistry.markState(Lifecycle.State.CREATED);
    super.onSaveInstanceState(outState);
}
```  

```
@Override
public Lifecycle getLifecycle() {
    return mLifecycleRegistry;
}
```
ReportFragment 被挂载到 Activity上, 监测其生命周期, 并且调用 宿主 Activity 内部的 LifecycleRegistry, 管理 生命周期的状态;  
在处理 LiveData 的回调函数, 也会 LifecycleOwner 也就是当前的 Activity/Fragment, 只有在其没被回收的时候, 响应其回调函数, 防止 context 不在了, view 不在了, 造成空指针;  

### 怎样关联生命周期的  
1.. 嵌入 ReportFragment  
ComponentActivity#onCreate  
ReportFragment#injectIfNeededIn  
```
public static void injectIfNeededIn(Activity activity) {
    // ProcessLifecycleOwner should always correctly work and some activities may not extend
    // FragmentActivity from support lib, so we use framework fragments for activities
    android.app.FragmentManager manager = activity.getFragmentManager();
    if (manager.findFragmentByTag(REPORT_FRAGMENT_TAG) == null) {
        manager.beginTransaction().add(new ReportFragment(), REPORT_FRAGMENT_TAG).commit();
        // Hopefully, we are the first to make a transaction.
        manager.executePendingTransactions();
    }
}
```
2.. ReportFragment 的生命周期方法  
```
@Override
public void onResume() {
    super.onResume();
    dispatchResume(mProcessListener);
    dispatch(Lifecycle.Event.ON_RESUME);
}

@Override
public void onPause() {
    super.onPause();
    dispatch(Lifecycle.Event.ON_PAUSE);
}
```  

3.. ReportFragment#dispatch  
```
private void dispatch(Lifecycle.Event event) {
    Activity activity = getActivity();
    if (activity instanceof LifecycleRegistryOwner) {
        ((LifecycleRegistryOwner) activity).getLifecycle().handleLifecycleEvent(event);
        return;
    }

    if (activity instanceof LifecycleOwner) {
        Lifecycle lifecycle = ((LifecycleOwner) activity).getLifecycle();
        if (lifecycle instanceof LifecycleRegistry) {
            ((LifecycleRegistry) lifecycle).handleLifecycleEvent(event);
        }
    }
}
```   
4.. LifecycleRegistry#handleLifecycleEvent  
```
public void handleLifecycleEvent(@NonNull Lifecycle.Event event) {
    State next = getStateAfter(event);
    moveToState(next);
}

private void moveToState(State next) {
    if (mState == next) {
        return;
    }
    mState = next;
    if (mHandlingEvent || mAddingObserverCounter != 0) {
        mNewEventOccurred = true;
        // we will figure out what to do on upper level.
        return;
    }
    mHandlingEvent = true;
    sync();
    mHandlingEvent = false;
}


private void sync() {
    LifecycleOwner lifecycleOwner = mLifecycleOwner.get();
    if (lifecycleOwner == null) {
        Log.w(LOG_TAG, "LifecycleOwner is garbage collected, you shouldn't try dispatch "
                + "new events from it.");
        return;
    }
    while (!isSynced()) {
        mNewEventOccurred = false;
        // no need to check eldest for nullability, because isSynced does it for us.
        if (mState.compareTo(mObserverMap.eldest().getValue().mState) < 0) {
            backwardPass(lifecycleOwner);
        }
        Entry<LifecycleObserver, ObserverWithState> newest = mObserverMap.newest();
        if (!mNewEventOccurred && newest != null
                && mState.compareTo(newest.getValue().mState) > 0) {
            forwardPass(lifecycleOwner);
        }
    }
    mNewEventOccurred = false;
}

```
LifecycleRegistry#backwardPass  
LifecycleRegistry.ObserverWithState#dispatchEvent  
SingleGeneratedAdapterObserver#onStateChanged  



### liveData  
  
LiveData#setValue  
LiveData#dispatchingValue    
```
protected void setValue(T value) {
    assertMainThread("setValue");
    mVersion++;
    mData = value;
    dispatchingValue(null);
}

```  
### LIveData 怎么收到 改变的  
LiveData#setValue  
LiveData#dispatchingValue  
LiveData#considerNotify   

LiveData#onStateChanged   
LiveData#activeStateChanged   
LiveData#dispatchingValue   
LiveData#considerNotify   
```
private void considerNotify(ObserverWrapper observer) {
    if (!observer.mActive) {
        return;
    }
    // Check latest state b4 dispatch. Maybe it changed state but we didn't get the event yet.
    //
    // we still first check observer.active to keep it as the entrance for events. So even if
    // the observer moved to an active state, if we've not received that event, we better not
    // notify for a more predictable notification order.
    if (!observer.shouldBeActive()) {
        observer.activeStateChanged(false);
        return;
    }
    if (observer.mLastVersion >= mVersion) {
        return;
    }
    observer.mLastVersion = mVersion;
    //noinspection unchecked
    observer.mObserver.onChanged((T) mData);
}
```
### LiveData#observe  
```
@MainThread
public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
    assertMainThread("observe");
    //  如果 当前 Activity 已经 结束了, 则不会受到 回调
    if (owner.getLifecycle().getCurrentState() == DESTROYED) {
        // ignore
        return;
    }
    LifecycleBoundObserver wrapper = new LifecycleBoundObserver(owner, observer);
    ObserverWrapper existing = mObservers.putIfAbsent(observer, wrapper);
    if (existing != null && !existing.isAttachedTo(owner)) {
        throw new IllegalArgumentException("Cannot add the same observer"
                + " with different lifecycles");
    }
    if (existing != null) {
        return;
    }
    owner.getLifecycle().addObserver(wrapper);
}
```
 


