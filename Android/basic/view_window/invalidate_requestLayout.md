### 屏幕刷新机制  

先问几个问题:  
1.. Android 每隔 16.6ms 会刷新一次屏幕, 是指每隔 16.6ms 调用 onDraw() 绘制一次么?  
2.. 如果界面一直保持没变的话, 那么还会每隔 16.6ms 刷新一次屏幕么?  
3.. 界面的显示其实就是, 一个 Activity 的 View 树里, 所有的 View 都进行测量, 布局, 绘制操作, 之后的结果呈现;  
      那么如果这部分工作, 都完成后, 屏幕会马上就刷新么?  
4.. 如果某次用户点击屏幕, 导致的界面刷新操作, 是在某一个 16.6ms 帧快结束的时候, 那么即使这次绘制操作, 小于 16.6 ms, 会不也会造成丢帧呢?  
5.. 主线程耗时的操作, 会导致丢帧, 但是耗时的操作, 为什么会导致丢帧? 它是如何导致丢帧发生的?  
6.. 为什么是 16.6ms   

基本概念:  
在一个典型的显示系统中, 一般包括 CPU, GPU, display 三个部分;  
CPU 负责计算数据, 把计算好数据交给 GPU;  
GPU 会对图形数据进行渲染, 渲染好后放到 buffer 里存起来;  
然后 display(有的文章也叫屏幕或者显示器)负责把 buffer 里的数据呈现到屏幕上;  
display 每秒钟, 刷新 60 帧, 也就是说, 每 16.6ms 刷新一帧;  
 
 简单的说, 就是 CPU, GPU 准备好数据, 存入buffer;  
 display 每隔一段时间, 去 buffer 里取数据, 然后显示出来;  
 display 读取的频率是固定的, 比如每个 16.6ms 读一次, 但是 CPU, GPU 写数据, 是完全无规律的;  
 
对于 Android 而言, CPU 计算屏幕数据, 是指 View 树的绘制过程;  
也就是 Activity 对应的视图, 从 DecorView 开始, 层层遍历每个 View, 分别执行测量, 布局, 绘制, 三个操作的过程;  

也就是说, 我们常说的 Android 每隔 16.6ms 刷新一次屏幕, 其实是指, 底层以固定的频率, 比如每 16.6ms 将 buffer 里的屏幕数据显示出来;  


 如果用户不再操作数据了, 或者没有哪一个定时任务, 或者延时操作, 再主动刷新 View;  
 那么这之后, 我们的 app, 就不会再接收到, 屏幕刷新信号了, 所以也就不会再让 CPU, 去绘制视图树来计算下一帧画面了;  
 
但是, 底层还是会每隔 16.6ms, 发出一个屏幕刷新信号, 只是我们 app 不会接收到而已;  
Display 还是会在每隔一个屏幕刷新信号, 去显示下一帧画面, 只是下一帧画面, 一直是最后一帧的内容而已;  


FrameDisplayEventReceiver 继承与 DisplayEventReceiver;  
接收底层的 VSync 信号开始处理UI过程;  
VSync 信号由 SurfaceFlingerVsyncChoreographer 实现并定时发送;  
FrameDisplayEventReceiver 收到信号后, 调用 onVsync 方法组织消息发送到主线程处理, 这个消息主要内容就是run方法里面的 doFrame 了;  
 
 
### 屏幕刷新机制#总结  
我们知道一个 View 发起刷新的操作时, 最终是走到了 ViewRootImpl 的 scheduleTraversals() 里去;  
然后这个方法将遍历绘制 View 树的操作 performTraversals() 封装到 Runnable 里, 传给 Choreographer, 以当前的时间戳放进一个 mCallbackQueue 队列里;  
然后调用了 native 层的方法, 向底层注册监听下一个屏幕刷新信号事件;  
当下一个屏幕刷新信号, 发出的时候, 如果我们 app 有对这个事件进行监听, 那么底层, 就会回调我们 app 层的 onVsync() 方法来通知;  
当 onVsync() 被回调时, 会发一个 Message 到主线程, 将后续的工作, 切到主线程来执行;  
切到主线程的工作, 就是去 mCallbackQueue 队列里, 根据时间戳, 将之前放进去的 Runnable 取出来执行;  
而这些 Runnable 有一个就是遍历绘制 View 树的操作 performTraversals();  
在这次的遍历操作中, 就会去绘制那些需要刷新的 View;  
所以说, 当我们调用了 invalidate(), requestLayout(), 等之类刷新界面的操作时, 并不是马上就会执行这些刷新的操作,  
而是通过 ViewRootImpl 的 scheduleTraversals() 先向底层, 注册监听, 下一个屏幕刷新信号事件;  
然后等下一个屏幕刷新信号来的时候, 才会去通过 performTraversals() 遍历绘制 View 树来执行这些刷新操作;  
 
### requestLayout   
ViewRootImpl 实现了 ViewParent, 和一些 AttachInfo.Callbacks 和 DrawCallbacks, 并不是 ViewGroup;   

View#requestLayout  
函数体内部, 会回调父窗体的 requestLayout 方法;  
然后继续向上传递 requestLayout 事件, 直到 DecorView, 然后再传递给 ViewRootImpl;   
也就是说子 View 的 requestLayout 事件, 最终会被 ViewRootImpl 接收并得到处理;  

ViewRootImpl#requestLayout  
```
@Override
public void requestLayout() {
    if (!mHandlingLayoutInLayoutRequest) {
        checkThread();
        mLayoutRequested = true;
        scheduleTraversals();
    }
}
``` 
在这里, 调用了 scheduleTraversals 方法, 这是一个异步方法;  
主要是 把 mTraversalRunnable 添加到 Choreographer 的任务队列中;  
最终会调用到 ViewRootImpl#performTraversals 方法;  
这样会回调子 View 的 onLayout, onMeasure, onDraw 等方法;  

Choreographer#postCallback  
Choreographer#postCallbackDelayed  
Choreographer#postCallbackDelayedInternal  
Choreographer#scheduleFrameLocked(delay 0)  
```
如果当前函数, 运行在主线程, 立即调用 Choreographer#scheduleVsyncLocked;  
否则, 通过 handler 回调到主线程, 再调用 Choreographer#scheduleVsyncLocked;  
```
DisplayEventReceiver#scheduleVsync  
DisplayEventReceiver#nativeScheduleVsync  
```
这个方法, 可以理解为, 底层调用 native 方法, 监听 屏幕刷新回调;  
当 遇到 display 从缓存中取数据, 开始 刷新视图是, 此次事件, 在 View 层, 得到相应;  
SurfaceFlinger 进程收到 vsync 信号后, 转发给请求过的应用, 应用的 socket 接收到 vsync 后, DisplayEventReceiver#dispatchVsync     
```
DisplayEventReceiver#dispatchVsync  
Choreographer.FrameDisplayEventReceiver#onVsync    
```
//  FrameDisplayEventReceiver 实现了 runnable 接口;  
//  这里通过 handler 切换到 UI线程, 在 FrameDisplayEventReceiver 的 run 方法中执行响应的操作;  
Message msg = Message.obtain(mHandler, Runnable:this);
msg.setAsynchronous(true);
mHandler.sendMessageAtTime(msg, timestampNanos / TimeUtils.NANOS_PER_MS);
```
Choreographer.FrameDisplayEventReceiver#run  
Choreographer#doFrame  
Choreographer#doCallbacks  
```
把之前, 存在任务队列里面的 runnable, 取出来并执行;  
```
ViewRootImpl.TraversalRunnable#run;  
doTraversal;  
performTraversals;  

### invalidate  
View.invalidate ->  
View.invalidateInternal ->  
```
p.invalidateChild(this, damage);
```
ViewGroup#invalidateChild ->  
```
parent = parent.invalidateChildInParent(location, dirty);
```
invalidateChild 内部有个 do-while 循环, 不停调用父控件的 invalidateChildInParent, 一直到调用 ViewRootImpl 的 invalidateChildInParent;  
而 ViewRootImpl 的 invalidateChildInParent 内部, 调用 invalidateRectOnScreen, 然后调用 scheduleTraversals;   
在 performTraversals 方法中, mLayoutRequested 为 false, 所有 onMeasure 和 onLayout 都不会被调用;  

ViewGroup 的 invalidate:  
ViewGroup 的 dispatchDraw 方法会调用子 view 的 draw, 就是对子 view 进行重绘;  


### 总结  

1..  DecorView 有个虚拟 parentView 就是 ViewRootImpl,  它并不是一个 View 或者 ViewGroup, 他有个成员 mView 是 DecorView, 所有的操作从 ViewRootImpl 开始自上而下分发;   

2.. invalidate 触发子 View, 一直向上调用父控件的 invalidateChildInParent,   直到 ViewRootImpl 的 invalidateChildInParent, 然后触发 performTraversals, 由于 mLayoutRequested 为 false,  
      不会导致 onMeasure 和 onLayout 被调用, 只有 当前被标记过的 view 被重绘, onDraw 会被调用;  
3.. View 的 invalidate 会导致本身 PFLAG_INVALIDATED 置 1, 导致本身以及父族 viewGroup 的 PFLAG_DRAWING_CACHE_VALID 置 0, 所以只有被标记的子 View 才会被回调;  

4.. requestLayout 会直接递归调用父控件的 requestLayout, 直到 ViewRootImpl, 然后触发 performTraversals, 由于 mLayoutRequested 为true,  
      会导致 ViewTree 上所有的控件 onMeasure 和 onLayout 被调用, 不一定会触发 onDraw;  
5.. requestLayout 触发 onDraw 可能是因为在在 layout 过程中发现 l,t,r,b和以前不一样, 那就会触发一次 invalidate, 所以触发了onDraw,   
      也可能是因为别的原因导致mDirty非空(比如在执行动画);  
      但是当前子 view 的 onDraw 会被回调;   
6.. requestLayout 会导致自己以及父族 view 的 PFLAG_FORCE_LAYOUT 和 PFLAG_INVALIDATED 标志被设置;  
7.. 一般来说, 只要刷新的时候就调用 invalidate, 需要重新 measure 就调用 requestLayout, 后面再跟个 invalidate (为了保证重绘);   

### api  
获取手机屏幕的刷新频率  
```
Display display = getWindowManager().getDefaultDisplay();
float refreshRate = display.getRefreshRate();
```



### 参考  
VSync: 即V-Sync垂直同步;  垂直同步信号;  
http://dandanlove.com/2018/04/13/android-16ms/  
https://blog.csdn.net/litefish/article/details/52859300  
https://blog.csdn.net/litefish/article/details/53939882  
https://blog.csdn.net/xu_fu/article/details/44998403  
https://blog.csdn.net/yangwen123/article/details/39518923  
https://blog.csdn.net/houliang120/article/details/50908098  
https://www.jianshu.com/p/0d00cb85fdf3  
https://www.jianshu.com/p/996bca12eb1d  
 https://www.jianshu.com/p/dd32ec35db1d  
https://www.jianshu.com/p/a769a6028e51  
http://aspook.com/2017/11/01/Android-Choreographer/  

 
 
