### all  
所有的元素都满足条件  

```
Observable.just("1", "2", "3", "4", "5", "6")
        .all(new Predicate<String>() {
            @Override
            public boolean test(String result) throws Exception {
                return Integer.valueOf(result) <= 6;
            }
        }).subscribe(new LiteConsumer<Boolean>() {
    @Override
    public void onNext(Boolean result) {
        LogTrack.w(result);
    }
});
```  
事件流中 所有的元素， 都要满足条件， test 返回true就是都满足，返回false 就是至少有一个不满足条件；  
下游的onNext都会收到，只不过是true 或者让 false；  

