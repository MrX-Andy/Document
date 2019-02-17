### startWith  

◆ 示例 1  
```
List<String> list = new ArrayList<>();
list.add("A");
list.add("B");
list.add("C");
observable1.startWith(list)
        .subscribe(new LiteObserver<String>() {
            @Override
            public void onNext(String result) {
                LogTrack.w(result);
            }
        });
```
◆ 示例 2  
observable1.startWith(Observable.just("A", "B", "C"))  
