### observeOn  

◆ Observable#observeOn  
```
public final Observable<T> observeOn(Scheduler scheduler, boolean delayError, int bufferSize) {
    // ... 8817  
    return RxJavaPlugins.onAssembly(new ObservableObserveOn<T>(this, scheduler, delayError, bufferSize));
}
```
◆ ObservableObserveOn#subscribeActual  
```
@Override
protected void subscribeActual(Observer<? super T> observer) {
    if (scheduler instanceof TrampolineScheduler) {
        source.subscribe(observer);
    } else {
        Scheduler.Worker w = scheduler.createWorker();
        source.subscribe(new ObserveOnObserver<T>(observer, w, delayError, bufferSize));
    }
}
```
◆ ObservableObserveOn.ObserveOnObserver#onNext  
