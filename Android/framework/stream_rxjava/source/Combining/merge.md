### merge  
并发无序  
```
Observable<String> just1 = Observable.just("1", "2", "3", "4", "5", "6").delay(20, TimeUnit.MILLISECONDS);
    Observable<String> just2 = Observable.just("11", "22", "33", "44", "55", "66").delay(40, TimeUnit.MILLISECONDS);

    List<ObservableSource<String>> sourcesList = new ArrayList<>();
    sourcesList.add(just1);
    sourcesList.add(just2);
    Observable.merge(just1, just2);  
    
    Observable.merge(sourcesList)
            .subscribe(new LiteObserver<Object>() {
                @Override
                public void onNext(Object result) {
                    LogTrack.w(result);
                }
            });
```

