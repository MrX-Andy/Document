### scan  

◆ 示例 1    
```
Observable.range(1, 20)
        .scan(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) throws Exception {
                LogTrack.w(integer + "  " + integer2);
                return integer + integer2;
            }
        })
        .subscribe(new LiteObserver<Integer>() {
            @Override
            public void onNext(Integer result) {
                LogTrack.w("result = "+result);
            }
        });
```
onNext] result = 1  
apply] 1  2
  
onNext] result = 3  
apply] 3  3  

onNext] result = 6  
apply] 6  4  
逐个迭代每一个元素；  
第一次，第一个元素会直接输出；   
上一次的输出结果 当做 本次的 apply 的第一个参数， 本次迭代的元素 当做 apply的第二个参数，  apply的返回值是本次迭代的最终数值；    

