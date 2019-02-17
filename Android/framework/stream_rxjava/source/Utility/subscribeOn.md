### subscribeOn  
◆ Observable#subscribeOn  
```
public final Observable<T> subscribeOn(Scheduler scheduler) {
    ObjectHelper.requireNonNull(scheduler, "scheduler is null");
    return RxJavaPlugins.onAssembly(new ObservableSubscribeOn<T>(this, scheduler));
}
```  
◆ ObservableSubscribeOn#subscribeActual  
```
@Override
public void subscribeActual(final Observer<? super T> s) {
    final SubscribeOnObserver<T> parent = new SubscribeOnObserver<T>(s);
    s.onSubscribe(parent);
    parent.setDisposable(scheduler.scheduleDirect(new SubscribeTask(parent)));
}
```
◆ Scheduler#scheduleDirect  
```
public Disposable scheduleDirect(@NonNull Runnable run, long delay, @NonNull TimeUnit unit) {
    final Worker w = createWorker();
    final Runnable decoratedRun = RxJavaPlugins.onSchedule(run);
    DisposeTask task = new DisposeTask(decoratedRun, w);
    w.schedule(task, delay, unit);
    return task;
}
```
◆ IoScheduler#createWorker = IoScheduler.EventLoopWorker  
◆ IoScheduler.EventLoopWorker#schedule  
```
public Disposable schedule(@NonNull Runnable action, long delayTime, @NonNull TimeUnit unit) {
    if (tasks.isDisposed()) {
        return EmptyDisposable.INSTANCE;
    }
    return threadWorker.scheduleActual(action, delayTime, unit, tasks);
}
```
◆ NewThreadWorker#scheduleActual  
```
public ScheduledRunnable scheduleActual(final Runnable run, long delayTime, @NonNull TimeUnit unit, @Nullable DisposableContainer parent) {
    Runnable decoratedRun = RxJavaPlugins.onSchedule(run);
    ScheduledRunnable sr = new ScheduledRunnable(decoratedRun, parent);
    ...
    try {
        if (delayTime <= 0) {
            f = executor.submit((Callable<Object>)sr);
        } else {
            f = executor.schedule((Callable<Object>)sr, delayTime, unit);
        }
        sr.setFuture(f);
    } catch (RejectedExecutionException ex) {
       ...
    }
    return sr;
}
```

