### groupBy  

◆ 示例 1  
```
Observable.range(0, 20)
    .groupBy(new Function<Integer, Object>() {
        @Override
        public Object apply(Integer integer) throws Exception {
            /*按照 某种 规则 分组*/
            return integer > 7;
        }
    })
    .subscribe(new LiteObserver<GroupedObservable<Object, Integer>>() {

        @Override
        public void onNext(GroupedObservable<Object, Integer> groupedObservable) {
            groupedObservable.subscribe(new LiteObserver<Integer>() {
                @Override
                public void onNext(Integer result) {
                    LogTrack.w(groupedObservable.getKey() + "  " + result);
                }
            });
        }
    });
```  
onNext] false  7  
onNext] true  8  

◆ 示例 2  
```
Observable.range(0, 20)
    .groupBy(new Function<Integer, Object>() {
        @Override
        public Object apply(Integer integer) throws Exception {
            LogTrack.w("key = " + integer);
            return integer > 8;
        }
    }, new Function<Integer, Object>() {
        @Override
        public Object apply(Integer integer) throws Exception {
            LogTrack.w("value = " + integer);
            return integer;
        }
    })
    .subscribe(new LiteObserver<GroupedObservable<Object, Object>>() {
        @Override
        public void onNext(GroupedObservable<Object, Object> groupedObservable) {
            groupedObservable.subscribe(new LiteObserver<Object>() {
                @Override
                public void onNext(Object result) {
                    LogTrack.w(groupedObservable.getKey() + "  " + result);
                }
            });
        }
    });
```
key = 8  
value = 8  
onNext] false  8  

key = 9  
value = 9  
onNext] true  9  
