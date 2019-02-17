### 在thread 中使用 handler 
一个Handler的标准写法其实是这样的：  
```
texttitle.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new Thread() {
                @Override
                public void run() {
                    // 必须 prepare, 否则 looper.loop 方法可能不会运行  
                    Looper.prepare();    
                    Handler mHandler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 101) {
                                Log.i(TAG, "在子线程中定义Handler, 并接收到消息。。。");
                            }
                        }
                    };
                    Looper.loop();
                }
            }.start();
        }
    });
```

首先需要了解几个类:  Handler, Looper, Message, MessageQueue;   
在C++层, 比较重要的是 NativeMessageQueue 和 Loop 这两个类;  
当我们启动一个app时, ActivityManagerService会为我们的Activity创建并启动一个主线程(ActivityThread对象);  
在 ActivityThread 的 main 方法, 会调用 Looper.prepareMainLooper(); 创建主线程对应的消息循环, 调用 Looper.loop(); 进入消息循环中;  
  
主线程的消息队列也一直存在的。当消息队列中没有消息时, 消息队列会进入空闲等待状态;  
当有消息时, 则消息队列会进入运行状态, 进而将消息交给相应的Handler进行处理;  
这种机制是通过pipe(管道)机制实现的;  
[管道](../ipc_service/binder.md)  

❀ 个数问题  
实际上, 消息队列的底层数据结构, 并不是队列, 而是链表;  
Looper 可能有多个, 每个 Looper 在生成的时候, 都会对应存放在 sThreadLocal 成员变量里面;  
这意味着, 每一个线程调用了 prepare 函数, 都会生成一个独立的 Looper 对象;  
sThreadLocal 保证了线程独立, 线程间不可见, 同时保证了, 一个线程只有一个 Looper, 只有一个 MessageQueue, 但是可以有多个 Handler;  

❀ Loop阶段  
主要工作可以概括为2部分内容:  
(01) Java层, 创建Looper对象, Looper的构造函数中会创建消息队列MessageQueue的对象。MessageQueue的作用存储消息队列, 用来管理消息的。  
(02) C++层, 消息队列创建时, 会调用JNI函数, 初始化NativeMessageQueue对象。NativeMessageQueue则会初始化Looper对象。  
Looper的作用就是, 当Java层的消息队列中没有消息时, 就使Android应用程序主线程进入等待状态, 而当Java层的消息队列中来了新的消息后, 就唤醒Android应用程序的主线程来处理这个消息。  
❀ enqueueMessage  
(01) 消息队列为空。 这时候应用程序的主线程一般就是处于空闲等待状态了, 这时候就要唤醒它。   
(02) 消息队列非空。 这时候就不需要唤醒应用程序的主线程了, 因为这时候它一定是在忙着处于消息队列中的消息, 因此不会处于空闲等待的状态。  
在添加完消息之后, 如果主线程需要唤醒, 则调用nativeWake()。nativeWake()是个JNI函数, 它对应的实现是frameworks/base/core/jni/android_os_MessageQueue.cpp中的android_os_MessageQueue_nativeWake()。  


创建 handler 的时候会调用looper.prepare()来创建一个 looper,   
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
❀ 参考  
https://blog.csdn.net/solarsaber/article/details/48974907  
http://book.51cto.com/art/201208/353352.htm  
http://wangkuiwu.github.io/2014/08/26/MessageQueue/  
