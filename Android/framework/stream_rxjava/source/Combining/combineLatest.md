### combineLatest  
当observable1有事件发出，apply方法会响应，  
当observable2有事件发出，apply方法会响应，  
s,  s2 分别是 observable1 和 observable2 最后一次的事件；  
◆ 方式 1    
```
Observable.combineLatest(observable1, observable2, new BiFunction<String, String, Boolean>() {
        @Override
        public Boolean apply(String s, String s2) throws Exception {
            LogTrack.w(s + "  " + s2);
            return ObjUtil.isNotEmpty(s) && ObjUtil.isNotEmpty(s2);
        }
    }).subscribe(new LiteObserver<Boolean>() {
        @Override
        public void onNext(Boolean result) {
            LogTrack.w(result);
        }
    });
```
