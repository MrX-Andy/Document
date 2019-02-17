### Future 的缺点  
Callable接口与Runnable接口在对比时，主要的优点是，Callable接口可以通过Future取得返回值；  
但是future.get 方法是阻塞式的，大大影响运行效率，这就是Future的缺点；  

◆ 示例代码 说明  
```
ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(6, 10, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
for (int i = 0; i < 3; i++) {
    InnerCallable callable = new InnerCallable((i + 1) * 1000 + 1000);  //  2  3  4
    Future<String> future = threadPoolExecutor.submit(callable);
    LogTrack.w("hello  " + i);
    LogTrack.w("得到  " + future.get());
}
LogTrack.w("循环 外面");
threadPoolExecutor.shutdown();
```  
threadPoolExecutor先后执行submit3次，callable0 运行完之后，callable1 才一会运行；callable1运行完之后，callable2才会运行；  
所以多个callable是串行运行的；   


