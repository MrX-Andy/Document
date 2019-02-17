### reduce  

迭代所有元素 找出最值  
```
Observable.just(1, 2, 6, 4, 5).reduce(new BiFunction<Integer, Integer, Integer>() {
    @Override
    public Integer apply(Integer integer, Integer integer2) throws Exception {
        LogTrack.w(integer + "  " + integer2);
        if (integer > integer2) {
            return integer;
        } else {
            return integer2;
        }
    }
}).subscribe(new LiteConsumer<Integer>() {
    @Override
    public void onNext(Integer result) {
        LogTrack.w(result);
    }
});
```  
