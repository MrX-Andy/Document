### ambWith  
◆ ambWith  
```
just1.ambWith(just2)
        .subscribe(new LiteConsumer<String>() {
            @Override
            public void onNext(String result) {
                LogTrack.w(result);
            }
        });
```  
◆ ambArray  
```
Observable.ambArray(just1, just2)
        .subscribe(new LiteConsumer<String>() {
            @Override
            public void onNext(String result) {
                LogTrack.w(result);
            }
        });
```
◆ amb  
```
List<ObservableSource<String>> sourcesList = new ArrayList<>();
sourcesList.add(just1);
sourcesList.add(just2);
Observable.amb(sourcesList)
        .subscribe(new LiteObserver<Object>() {
            @Override
            public void onNext(Object result) {
                LogTrack.w(result);
            }
        });
```
如果先收到 just1 的事件， 下游收到的 全都是 来自just1的事件  