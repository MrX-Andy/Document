### 使用 handler 
要刷新UI, handler 要用到主线程的 looper, 
在主线程中初始化 Handler handler = new Handler();  
在子线程中初始化, 要更新UI, Handler handler = new Handler(Looper.getMainLooper());  
在子线程中初始化, 不更新UI, Handler handler = new Handler();  
子线程 run 方法, 创建 handler, 无论Handler构造函数, 是用的哪个Looper, 如果没有调用 looper.prepare, 就会抛异常;  
所以, 在run方法中, 调用了 prepare, 会创建一个looper对象, 赋值给threadLocal, threadLocal, 是以当前线程为key, 存储刚才的对象, 因此做到了, 一个线程对应一个looper;  
主线程的 looper 可以更新UI, 子线程的 looper 不可以更新UI;  
创建 handler 的时候会调用 looper.prepare() 来创建一个 looper, 在 new Looper 的时候会创建一个 MessageQueue 对象;  
也就是 一个线程对应一个 looper 和 一个 messageQueue 和多个 handler 对象;  
sThreadLocal 保证了线程独立, 线程间不可见, 同时保证了, 一个线程只有一个 Looper, 只有一个 MessageQueue, 但是可以有多个 Handler;  
```
@Override
public void onCreateData(@Nullable Bundle bundle) {

    new Thread() {
        @SuppressLint("HandlerLeak")
        @Override
        public void run() {
            super.run();
            LogTrack.v("thread.id = " + Thread.currentThread().getId());
            Looper.prepare();
            Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    LogTrack.v("thread.id = " + Thread.currentThread().getId() + ", what = " + msg.what);
                }
            };
            handler.sendEmptyMessage(99);
            SystemClock.sleep(12 * 1000);
            handler.sendEmptyMessage(100);
            Looper.loop();
            handler.sendEmptyMessage(101);
            handler.sendEmptyMessage(102);
        }
    }.start();

}
```
只能收到 99-100, 收不到 101-102;  

### 子线程更新UI, 会怎么样?  
```
@SuppressLint("SetTextI18n")
@Override
public void onCreateData(@Nullable Bundle bundle) {
    textView = findView(R.id.textView);
    new Thread() {
        @SuppressLint("HandlerLeak")
        @Override
        public void run() {
            super.run();
            LogTrack.v("thread.id = " + Thread.currentThread().getId());
            SystemClock.sleep(5000);  // A
            textView.setText("thread.id = " + Thread.currentThread().getId() + "子线程");
        }
    }.start();
}
```
在A代码处, 先sleep后更新UI, 会抛异常;  
异常信息: ViewRootImpl#invalidateChildInParent  
```
@Override
public ViewParent invalidateChildInParent(int[] location, Rect dirty) {
    checkThread();
}
void checkThread() {
    if (mThread != Thread.currentThread()) {
        throw new CalledFromWrongThreadException(
                "Only the original thread that created a view hierarchy can touch its views.");
    }
}
```
invalidateChildInParent, 为什么会触发?  setTest 更新了文本, 改变了 TextView 的大小, 会触发 invalidate 方法, 
但是, 为什么, 如果是先更新UI, 后sleep, 不会抛异常? 还有, 在view的点击事件更新UI也会抛异常.  
所以, 很肯定的是, 没抛异常是因为 没执行 checkThread方法, 换句话说也没执行 invalidateChildInParent;  
怎么回事?  要研究View的绘制原理, 和绘制机制, 绘制流程, 系统调用时机;  
[链接](/Android/basic/view_window/invalidate_requestLayout.md)  

### Handler#机制  
首先需要了解几个类:  Handler, Looper, Message, MessageQueue;   
在C++层, 比较重要的是 NativeMessageQueue 和 Loop 这两个类;  
当我们启动一个app时, ActivityManagerService会为我们的Activity创建并启动一个主线程(ActivityThread对象);  
在 ActivityThread 的 main 方法, 会调用 Looper.prepareMainLooper(); 创建主线程对应的消息循环, 调用 Looper.loop(); 进入消息循环中;  
  
主线程的消息队列也一直存在的。当消息队列中没有消息时, 消息队列会进入空闲等待状态;  
当有消息时, 则消息队列会进入运行状态, 进而将消息交给相应的Handler进行处理;  
这种机制是通过pipe(管道)机制实现的;  
[管道](../ipc_service/system_zygote_binder.md)  

❀ Loop阶段  
主要工作可以概括为2部分内容:  
(01) Java层, 创建Looper对象, Looper的构造函数中会创建消息队列MessageQueue的对象。MessageQueue的作用存储消息队列, 用来管理消息的。  
(02) C++层, 消息队列创建时, 会调用JNI函数, 初始化NativeMessageQueue对象。NativeMessageQueue则会初始化Looper对象。  
Looper的作用就是, 当Java层的消息队列中没有消息时, 就使Android应用程序主线程进入等待状态, 而当Java层的消息队列中来了新的消息后, 就唤醒Android应用程序的主线程来处理这个消息。  
❀ enqueueMessage  
(01) 消息队列为空。 这时候应用程序的主线程一般就是处于空闲等待状态了, 这时候就要唤醒它。   
(02) 消息队列非空。 这时候就不需要唤醒应用程序的主线程了, 因为这时候它一定是在忙着处于消息队列中的消息, 因此不会处于空闲等待的状态。  
在添加完消息之后, 如果主线程需要唤醒, 则调用nativeWake()。nativeWake()是个JNI函数, 它对应的实现是frameworks/base/core/jni/android_os_MessageQueue.cpp中的android_os_MessageQueue_nativeWake()。  

handler 通过 send 发送消息 (sendMessage) ,当然 post 一系列方法最终也是通过 send 来实现的,     
在 send 方法中handler 会通过 enqueueMessage() 方法中的 enqueueMessage(msg,millis )向消息队列 MessageQueue 插入一条消息,    
同时会把本身的 handler 通过 msg.target = this 传给message,     
Looper 是一个死循环, 不断的读取MessageQueue中的消息, loop 方法会调用 MessageQueue 的 next 方法来获取新的消息,   
next 操作是一个阻塞操作,当没有消息的时候 next 方法会一直阻塞, 进而导致 loop 一直阻塞,   
当有消息的时候,Looper 就会处理消息 Looper 收到消息之后就开始处理消息: msg.target.dispatchMessage(msg),当然这里的 msg.target 就是上面传过来的发送这条消息的 handler 对象,   
这样 handler 发送的消息最终又交给他的dispatchMessage方法来处理了,这里不同的是,handler 的 dispatchMessage 方法是在创建 Handler时所使用的 Looper 中执行的,   
这样就成功的将代码逻辑切换到了主线程了, Handler 处理消息的过程是:首先,检查Message 的 callback 是否为 null,不为 null 就通过 handleCallBack 来处理消息,   
Message 的 callback 是一个 Runnable 对象,实际上是 handler 的 post 方法所传递的 Runnable 参数.其次是检查 mCallback 是 否为 null,不为 null 就调用 mCallback 的handleMessage 方法来处理消息.

[postDelay](library/handler_postDelay.md)  
[HandlerThread](library/HandlerThread.md)  
[Message Callback](library/message_callback.md)  

理论上 messageQueue.nativePollOnce 会让线程挂起-阻塞-block 住, 但是为什么, 在发送 delay 10s 的消息, 假设消息队列中, 目前只有这一个消息;  
那么为什么在这 10s 内, UI是可操作的, 或者列表页是可滑动的, 或者动画还是可以执行的?  
先不讲 nativePollOnce 是怎么实现的阻塞, 我们还知道, 另外一个 nativeWake, 是实现线程唤醒的;  
那么什么时候会, 触发这个方法的调用呢, 就是在有新消息添加进来的时候, 可是并没有手动添加消息啊?  
display 每隔16.6秒, 刷新一次屏幕;  
SurfaceFlingerVsyncChoreographer 每隔16.6秒, 发送一个 vSync 信号;  
FrameDisplayEventReceiver 收到信号后, 调用 onVsync 方法, 通过 handler 消息发送到主线程处理, 所以就会有消息添加进来, UI线程就会被唤醒;  
事实上, 安卓系统, 不止有一个屏幕刷新的信号, 还有其他的机制, 比如输入法和系统广播, 也会往主线程的 MessageQueue 添加消息;  
所以, 可以理解为, 主线程也是随时挂起, 随时被阻塞的;  

### 线程被挂起了  
```
@Override
public void onCreateData(@Nullable Bundle bundle) {

    new Thread() {
        @SuppressLint("HandlerLeak")
        @Override
        public void run() {
            super.run();
            LogTrack.v("thread.id = " + Thread.currentThread().getId());
            Looper.prepare();
            Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    LogTrack.v("thread.id = " + Thread.currentThread().getId() + ", what = " + msg.what);
                }
            };
            LogTrack.w("loop.之前");  // 执行了
            Looper.loop();  // 执行了
            LogTrack.w("loop.之后");  // 无法执行
        }
    }.start();

}
```
### 参考  
https://blog.csdn.net/solarsaber/article/details/48974907  
http://book.51cto.com/art/201208/353352.htm  
http://wangkuiwu.github.io/2014/08/26/MessageQueue/  
