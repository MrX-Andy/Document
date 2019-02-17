### window  
window与buffer很像， 也是做分组的  
◆ 示例 1  
```
.buffer(4)
    .subscribe(new LiteObserver<List<Long>>() {
        @Override
        public void onNext(List<Long> result) {
            
        }
    });
```
```
Observable.interval(1000, TimeUnit.MILLISECONDS)
        .window(4)
        .subscribe(new LiteObserver<Observable<Long>>() {
            @Override
            public void onNext(Observable<Long> result) {
                LogTrack.w("hello  "+result);
                result.subscribe(new LiteObserver<Long>() {
                    @Override
                    public void onNext(Long result) {
                        LogTrack.w(result);
                    }
                });
            }
        });
```  
onNext] hello  io.reactivex.subjects.UnicastSubject@1a5b2283  
onNext] 0  
onNext] 1  
onNext] 2  
onNext] 3  
onNext] hello  io.reactivex.subjects.UnicastSubject@1a5b2283  
onNext] 4  
onNext] 5  
onNext] 6  


◆ 示例 2  
```
Observable.interval(1000, TimeUnit.MILLISECONDS)
        .window(9, 3)
        .subscribe(new LiteObserver<Observable<Long>>() {
            @Override
            public void onNext(Observable<Long> result) {
                LogTrack.w("hello  " + result);
                result.subscribe(new LiteObserver<Long>() {
                    @Override
                    public void onNext(Long result) {
                        LogTrack.w(result);
                    }
                });
            }
        });
```
.window(9, 1)  
1,  
2,  
3,  
4,  
5,  
6,  
7,  
8,  
9,  

.window(9, 2)  
1, 1,  
2, 2,  
3, 3,  
4, 4,  
5, 4,  

.window(9, 3)   
1, 1, 1,  
2, 2, 2,  
3, 3, 3,  

.window(9, 4)  
1, 1, 1, 1,  
2, 2, 2, 2,  
3, 2, 2, 2,  

.window(9, 5)  
1, 1, 1, 1, 1,  
2, 2, 2, 2, 1,   

.window(9, 6)  
1, 1, 1, 1, 1, 1,   
2, 2, 2, 1, 1, 1,   

.window(9, 7)  
1, 1, 1, 1, 1, 1, 1,   
2, 2, 1, 1, 1, 1, 1,   

◆ 其算法就是：  
.window(long count, long skip)  
分组发射windows，每一组 也就是每一排 有skip个 单元；  
从1开始计数，例如 window(9, 6)  
第一排 1, 1, 1, 1, 1, 1,  总数相加 ≤ 9， 1的意思就是 当前元素被发射1次， 2的意思就是 当前元素被发射2次；      
第二排 2, 2, 2, 2, 2, 2,  总数相加 ＞ 9， 从右向左一次变成 2-1， 直至 总数相加 等于9    


