### contains  
事件流 是否包含 元素 element  
```
Observable.just("1", "2", "3", "4", "5", "6").contains("4")
        .subscribe(new LiteConsumer<Boolean>() {
            @Override
            public void onNext(Boolean result) {
                LogTrack.w(result);
            }
        });
```