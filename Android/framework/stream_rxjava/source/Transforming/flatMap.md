### flatMap  
apply方法法人入口参数是T， 出口参数是Observable<R>   
所以map做的事情是，T  to Observable<R>  
```
@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
public final <R> Observable<R> flatMap(Function<? super T, ? extends ObservableSource<? extends R>> mapper) {
    return flatMap(mapper, false);
}

public interface Function<T, R> {
    R apply(@NonNull T t) throws Exception;
}
```
