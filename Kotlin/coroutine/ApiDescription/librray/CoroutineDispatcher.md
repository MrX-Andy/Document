### CoroutineDispatcher  

协程启动选项，是一个抽象类，其子类：    
● Unconfined    和当前线程，保持一致；  
● CommonPool    线程池，工作线程；    
● HandlerContext   利用Handler实现的，Android的UI线程；    

> CommonPool  

CommonPool是代表共享线程池，它的主要作用是用来调度计算密集型任务的协程的执行。它的实现使用的是java.util.concurrent包下面的API；  
它首先尝试创建一个ForkJoinPool，ForkJoinPool是一个可以执行ForkJoinTask的ExcuteService，  它采用了work-stealing模式，  
所有在池中的线程尝试去执行其他线程创建的子任务，这样很少有线程处于空闲状态，更加高效，如果不可用，就使用Executors来创建一个普通的线程池；  

