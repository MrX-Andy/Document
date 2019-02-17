###  ThreadLocal  

当使用ThreadLocal维护变量时，ThreadLocal为每个使用该变量的线程提供独立的变量副本，就是单独new 出来一个实例对象，  
所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本，因为是不同的示例对象，所以互不影响。  

从线程的角度看，每个线程都保持对其线程局部变量副本的隐式引用，只要线程是活动的并且 ThreadLocal 实例是可访问的；  
在线程消失之后，其线程局部实例的所有副本都会被垃圾回收（除非存在对这些副本的其他引用）。  


通过ThreadLocal.set()将这个新创建的对象的引用保存到各线程的自己的一个map中，每个线程都有这样一个map，执行ThreadLocal.get()时，各线程从自己的map中取出放进去的对象，  
因此取出来的是各自自己线程中的对象，ThreadLocal实例是作为map的key来使用的。 
如果ThreadLocal.set()进去的东西本来就是多个线程共享的同一个对象，那么多个线程的ThreadLocal.get()取得的还是这个共享对象本身，还是有并发访问问题。   


总之，ThreadLocal不是用来解决对象共享访问问题的，而主要是提供了保持对象的方法和避免参数传递的对象访问方式。归纳了两点：   
1。每个线程中都有一个自己的ThreadLocalMap类对象，可以将线程自己的对象保持到其中，各管各的，线程可以正确的访问到自己的对象。   
2。将一个共用的ThreadLocal静态实例作为key，将不同对象的引用保存到不同线程的ThreadLocalMap中，然后在线程执行的各处通过这个静态ThreadLocal实例的get()方法，  
取得自己线程保存的那个对象，避免了将这个对象作为参数传递的麻烦。   

### Thread#threadLocals
thread内部有一个成员变量threadLocals，在线程退出的时候，会被置空    
```
Thread#threadLocals

/**
 * This method is called by the system to give a Thread
 * a chance to clean up before it actually exits.
 */
private void exit() {
    if (group != null) {
        group.threadTerminated(this);
        group = null;
    }
    /* Aggressively null out all reference fields: see bug 4006245 */
    target = null;
    /* Speed the release of some of these resources */
    threadLocals = null;
    inheritableThreadLocals = null;
    inheritedAccessControlContext = null;
    blocker = null;
    uncaughtExceptionHandler = null;
}
```
### 内存泄漏  
当使用ThreadLocal保存一个value时，会在ThreadLocalMap中的数组插入一个Entry对象，按理说key-value都应该以强引用保存在Entry对象中，  
但在ThreadLocalMap的实现中，key被保存到了WeakReference对象中。  
这就导致了一个问题，ThreadLocal在没有外部强引用时，发生GC时会被回收，如果创建ThreadLocal的线程一直持续运行，那么这个Entry对象中的value就有可能一直得不到回收，发生内存泄露。    
如何避免内存泄露？  
既然已经发现有内存泄露的隐患，自然有应对的策略，在调用ThreadLocal的get()、set()可能会清除ThreadLocalMap中key为null的Entry对象，这样对应的value就没有GC Roots可达了，  
下次GC的时候就可以被回收，当然如果调用remove方法，肯定会删除对应的Entry对象。  
如果使用ThreadLocal的set方法之后，没有显示的调用remove方法，就有可能发生内存泄露，所以养成良好的编程习惯十分重要，使用完ThreadLocal之后，记得调用remove方法。  

### ThreadLocal#get  
key值为null，则擦除该位置的Entry，那么Entry内的value也就没有强引用链，自然会被回收；    
set操作也有类似的思想，将key为null的这些Entry都删除，防止内存泄露。  
上面的设计思路依赖一个前提条件：要调用ThreadLocalMap的getEntry函数或者set函数。这当然是不可能任何情况都成立的，  
所以很多情况下需要使用者手动调用ThreadLocal的remove函数，手动删除不再需要的ThreadLocal，防止内存泄露。  

```
public T get() {
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);
    if (map != null) {
        ThreadLocalMap.Entry e = map.getEntry(this);
        if (e != null) {
            @SuppressWarnings("unchecked")
            T result = (T)e.value;
            return result;
        }
    }
    return setInitialValue();
}

ThreadLocalMap getMap(Thread t) {
    return t.threadLocals;
}
```
### ThreadLocal.ThreadLocalMap#set  
```

```
### ThreadLocal.ThreadLocalMap.Entry  
```
static class Entry extends WeakReference<ThreadLocal<?>>{
    /**
     * The table, resized as necessary.
     * table.length MUST always be a power of two.
     */
    private Entry[] table;
    
}

```


### 参考  
https://my.oschina.net/xianggao/blog/392440?fromerr=CLZtT4xC
http://blog.csdn.net/lufeng20/article/details/24314381  
https://juejin.im/post/5a64a581f265da3e3b7aa02d  
https://juejin.im/post/5b5ecf9de51d45190a434308  
https://juejin.im/post/5a0e985df265da430e4ebb92  
http://blog.jrwang.me/2016/java-simpledateformat-multithread-threadlocal/  
https://www.jianshu.com/p/377bb840802f  

