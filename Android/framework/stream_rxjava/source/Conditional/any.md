### any  
只要有一个满足条件；  
```
Observable.just("1", "2", "3", "4", "5", "6")
            .any(new Predicate<String>() {
                @Override
                public boolean test(String result) throws Exception {
                    LogTrack.w("result = " + result);
                    return "4".equalsIgnoreCase(result);
                }
            }).subscribe(new LiteConsumer<Boolean>() {
        @Override
        public void onNext(Boolean result) {
            LogTrack.w(result);
        }
    });
```  
找到 事件流中 满足条件的 元素， test 返回true就是找到， 找到会立刻停止遍历，否则继续遍历直到事件结束；  
找到与否下游的onNext都会收到，只不过是true 或者让 false；  
