### RxJava  
Flowable  事件源(0..N个元素), 支持背压  
Observable  事件源(0..N个元素), 不支持背压  
Single  仅发射一个元素或产生error的事件源  
Completable  不发射任何元素，只产生completion或error的事件源  
Maybe  不发射任何元素，或只发射一个元素，或产生error的事件源  
Subject  既是事件源，也是事件接受者  
◆ Creating  
[create(source).subscribe ](source/Creating/create.md)  
[defer](source/Creating/defer.md)  
[fromArray](source/Creating/fromArray.md)  
[interval](source/Creating/interval.md)  
[just(T...)](source/Creating/just.md)  
[range](source/Creating/range.md)  
[repeat](source/Creating/repeat.md)  
[timer](source/Creating/timer.md)  
◆ Utility  
[subscribeOn  observeOn](source/Utility/Scheduler.md)  
[timeout](source/Utility/timeout.md)  
◆ Transforming  
[buffer](source/Transforming/buffer.md)  
[flatMap](source/Transforming/flatMap.md)  
[groupBy](source/Transforming/groupBy.md)  
[map](source/Transforming/map.md)  
[scan](source/Transforming/scan.md)  
[switchMap](source/Transforming/switchMap.md)  
[window](source/Transforming/window.md)  
[lift](source/lift.md)   
◆ Filtering  
[debounce](source/Filtering/debounce.md)  
[throttle](source/Filtering/throttle.md)  
[elementAt](source/Filtering/elementAt.md)  
[filter](source/Filtering/filter.md)  
[first](source/Filtering/first.md)  
[firstElement](source/Filtering/firstElement.md)   
[sample](source/Filtering/sample.md)  
[skip](source/Filtering/skip.md)  
[skipLast](source/Filtering/skipLast.md)  
[take](source/Filtering/take.md)  
[takeLast](source/Filtering/takeLast.md)  
◆ Conditional and Boolean  
[all](source/Conditional/all.md)   
[amb](source/Conditional/amb.md)  
[any](source/Conditional/any.md)   
[contains](source/Conditional/contains.md)    
[defaultIfEmpty](source/Conditional/defaultIfEmpty.md)  
[sequenceEqual](source/Conditional/sequenceEqual.md)  
[skipUntil](source/Conditional/skipUntil.md)  
[skipWhile](source/Conditional/skipWhile.md)  
[takeUntil](source/Conditional/takeUntil.md)  
[takeWhile](source/Conditional/takeWhile.md)  
◆ ErrorHandling  
[retry_BiPredicate](source/ErrorHandling/retry_BiPredicate.md)
◆ Combining  
[combineLatest](source/Combining/combineLatest.md)  
[join](source/Combining/join.md)  
[merge](source/Combining/merge.md)     
[zip](source/Combining/zip.md)  
◆ Mathematical  
[reduce](source/Mathematical/reduce.md)  
[count](source/Mathematical/count.md)  

◆ 高级操作符  
原理就是利用 takeUntil  
[生命周期](rxLifecycle/Lifecycle.md)  

◆ 参考  
http://rxmarbles.com/  
http://reactivex.io/documentation/operators.html   
https://juejin.im/post/5a1e0e4051882512a8610fcf  
https://juejin.im/post/5a209c876fb9a0452577e830  
https://github.com/mcxiaoke/RxDocs  
https://github.com/nanchen2251/RxJava2Examples  
http://blog.csdn.net/u014165119/article/details/52582782  
https://www.jianshu.com/p/90e9434b8590  
http://blog.csdn.net/xiechengfa/article/details/51821030  
https://github.com/uber/AutoDispose  

### RxJava

https://github.com/ReactiveX/RxJava  
http://reactivex.io/documentation/scheduler.html  
https://mcxiaoke.gitbooks.io/rxdocs/content/Intro.html  
稀土掘金Rx专题  https://juejin.im/tag/RxJava  
云在千峰 Intro To RxJava 系列教程 总结  http://blog.chengyunfeng.com/?p=983  
Rx和RxJava文档中文翻译项目 https://github.com/mcxiaoke/RxDocs  
csdn RxJava入门  http://blog.csdn.net/column/details/rxjava.html  
图说RxJava  http://rxmarbles.com/  
RxJava操作符大全  http://blog.csdn.net/maplejaw_/article/details/52396175  
Rxjava2 给初学者的RxJava2.0教程(一)  https://juejin.im/post/5848d96761ff4b0058c9d3dc  
极客学院 ReactiveX 文档中文翻译  http://wiki.jikexueyuan.com/project/rx-docs/operators/Transforming-Observables.html  
debug RxJava2    https://github.com/T-Spoon/Traceur  
RxJava+Retrofit  
csdn Android开发之Rxjava+Retrofit  http://blog.csdn.net/column/details/retrofit.html  
https://github.com/amitshekhariitbhu/RxJava2-Android-Samples  
https://blog.csdn.net/dehang0/article/details/79135015  
https://cloud.tencent.com/developer/article/1368392  

BroadcastReceivers  
https://github.com/f2prateek/rx-receivers  

Binding  
https://github.com/JakeWharton/RxBinding  

https://github.com/VictorAlbertos/RxCache    

https://github.com/airbnb/RxGroups  
https://github.com/ReactiveX/RxKotlin  

https://hk.saowen.com/a/0da89c6083f7f899eeb198ff71a3f5af721dab2254e0377aed514f305f29e4b3  
https://juejin.im/post/5c4e57bdf265da6110376748  






