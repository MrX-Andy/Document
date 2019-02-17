### buffer  

◆ 示例 1  
Observable.range(1, 7).buffer(4)  
onNext：1, 2, 3, 4    
onNext：5, 6, 7  
◆ 示例 2  
Observable.range(1, 7).buffer(4, 3)  
onNext：1, 2, 3, 4    
onNext：4, 5, 6, 7    
onNext：7    

```
.buffer(4)
    .subscribe(new LiteObserver<List<Long>>() {
        @Override
        public void onNext(List<Long> result) {
            
        }
    });
```