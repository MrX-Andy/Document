### startActivity  

api >= 26 之后  
ApplicationThreadProxy  ApplicationThreadNative  ActivityManagerNative  BulkCursorNative  ContentProviderNative  
都被废弃;  

ActivityThread  
App的真正入口, 当开启App之后, 会调用main()开始运行, 开启消息循环队列, 启动UI线程;   

ActivityThread.ApplicationThread, 用来完成 ActivityManagerService 与 ActivityThread 之间的交互;    
在 ActivityManagerService 需要管理相关 Application 中的 Activity 的生命周期时,  通过ApplicationThread的代理对象与ActivityThread通讯;  
ApplicationThread是ActivityThread的内部类;  

ApplicationThreadProxy  
是 ApplicationThread 在服务器端的代理, 负责和客户端的 ApplicationThread 通讯;  AMS就是通过该代理与 ActivityThread 进行通信的;  

ActivityThread.H  
H其实是一个Handler, 也是ActivityThread的一个内部类, 运行在主线程;  

Instrumentation  
管理一个活动的生命周期;  
Instrumentation是android系统中启动Activity的一个实际操作类, 也就是说Activity在应用进程端的启动实际上就是Instrumentation执行的；  

每一个应用程序只有一个Instrumentation对象, 每个Activity内都有一个对该对象的引用;  Instrumentation可以理解为应用进程的管家,   
ActivityThread要创建或暂停某个Activity时, 都需要通过Instrumentation来进行具体的操作;    

Instrumentation#newActivity();  
Instrumentation#newApplication();  

ActivityStack  
管理一个活动栈  
Activity在AMS的栈管理, 用来记录已经启动的Activity的先后关系, 状态信息等;  通过ActivityStack决定是否需要启动新的进程;  

ActivityStackSupervisor  
管理所有的活动栈  

ActivityRecord  
ActivityStack的管理对象, 每个Activity在AMS对应一个ActivityRecord, 来记录Activity的状态以及其他的管理信息;  其实就是服务器端的Activity对象的映像;  

ActivityStarter  
根据intent, flags 找到 activity, stack  

TaskRecord  
AMS抽象出来的一个“任务”的概念, 是记录ActivityRecord的栈, 一个“Task”包含若干个ActivityRecord;  AMS用TaskRecord确保Activity启动和退出的顺序;    

ActivityManagerService  
管理所有的活动;  
位于system_server进程, 从ActivityManagerService提供的接口来看, 它负责管理Activity的启动和生命周期;  

ActivityManagerProxy  
是 ActivityManagerService 在普通应用进程的一个代理对象;  
已经被废弃, 通过AIDL生成的对象;  
它只负责准备相关的数据发送到system_process进程去处理startActivity;  
应用进程通过 ActivityManagerProxy 对象调用 ActivityManagerService 提供的功能;  

应用进程并不会直接创建 ActivityManagerProxy 对象,   
而是通过调用 ActivityManagerNative 类的工具方法 getDefault 方法得到 ActivityManagerProxy 对象;  
也就是 通过 ActivityManager#getService 方法得到 ActivityManagerProxy 对象;  

### PackageManagerService  
PackageManagerService 在启动后会扫描系统和第三方的app信息, 在 scanPackageLI 方法中实例化 PackageParser 对象 pp, 使用 pp 对包进行解析;  
PackageParser 的 parseBaseApk 在调用之后解析 AndroidManifest.xml, 返回一个 Package 对象, 将手机中所有的 app 的 AndroidManifest.xml 解析完毕, 构建出一个手机中所有 app 的信息树;  
从这颗棵树上


### 概述    
无论是通过 launcher 来启动 Activity 还是通过其他 Activity 来启动另一个 Activity, 都需要通过IPC调用 ActivityManagerService 的 startActivity 的方法;  

ActivityManagerService 调用 ActivityStarter.startActivityMayWait, 经过一系列复杂的调用, 收集并记录Activity的启动信息, 调整ActivityStack(让栈顶的Activity进入pause状态),  
创建并初始化Application对象, 创建ActivityThread并调用main方法;  

最后在 ActivityStackSupervisor 的 realStartActivityLocked 方法调用 app.thread.scheduleLaunchActivity 方法,   
也就是说, ActivityManagerService 调用 ApplicationThread 的 scheduleLaunchActivity 接口方法;  

Activity.startActivity  
Instrumentation#execStartActivity  
ActivityManagerProxy#startActivity  
通过Binder驱动程序就进入到 ActivityManagerService 的 startActivity 方法;  
ActivityManagerService#startActivity  
经过IPC调用，启动Activity的指令来到了ActivityManagerService，紧接着AMS调用 startActivityAsUser 着手Activity的启动工作;   

ActivityStarter#startActiviytMayWait  
AMS有一个ActivityStack, 负责Activity的调度工作, 比如维护回退栈, 但ActivityStack内的Activity是抽象成ActivityRecord来管理的, Activity对象不会存在于AMS当中;  

ActivityStarter#startActivityUncheckedLocked  
这个方法会根据Activity启动信息(提取封装到ActivityInfo类中)中的launchMode, flag等属性来调度ActivityStack中的Task和ActivityRecord;  
因此这个方法是理解Activity启动模式的关键;  

ActivityStack#resumeTopActivityInnerLocked  
这个方法内部会把前台处于Resume状态的Activity变成Pause状态后才会继续启动Activity的逻辑;  
将一个Activity变成Pause状态需要经历的调用于后面的启动调用非常相似;  

ActivityStack#startSpecificActivityLocked  
这里最后会调用AMS的startProcessLocked, 这个方法会先判断是否已经存在相应的进程, 如果不存在则通过远程调用Zygote进程来孵化出新的应用进程,  
Zygote进程孵化出新的应用进程后, 会执行ActivityThread类的main方法;  
在该方法里会先准备好Looper和消息队列, 然后调用attach方法将应用进程绑定到ActivityManagerService, 然后进入loop循环, 不断地读取消息队列里的消息，并分发消息;  
这个过程在Android的消息机制里已经非常熟悉了, 其中attach方法在与AMS绑定的过程中会调用attachApplicationLocked方法;  
attachApplicationLocked方法有两个重要的函数调用thread.bindApplication和mMainStack.realStartActivityLocked;  
thread.bindApplication将应用进程的ApplicationThread对象绑定到ActivityManagerService, 也就是说获得ApplicationThread对象的代理对象;  
mMainStack.realStartActivityLocked通知应用进程启动Activity  

ActivityStack#realStartActivityLocked  
app.thread其实就是ApplicationThread在AMS的代理对象, 实际上是调用ApplicationThread#scheduleLaunchActivity;  
接下来Activity的启动工作就交给应用进程来完成了, 别忘了这时候的Activity对象还没被创建呢;  

ActivityThread#performLaunchActivity  
通过类加载器加载Activity对象;  
创建ContextImpl对象并调用activity的attach方法，把上下文变量设置进activity中，创建Window对象和设置WindowManager;  
回调onCreate,onStart和onRestoreInstanceState方法;  

ActivityThread#handleResumeActivity  
回调Activity的onResume方法;  
调用WindowManager的addView方法，将前面在attach方法内创建出来的window对象添加到WindowManager当中;  


### 参考 
https://blog.csdn.net/stonecao/article/details/6591847  
https://blog.csdn.net/qq_23547831/article/details/51224992  
http://gityuan.com/2016/03/12/start-activity/  
https://github.com/yipianfengye/androidSource/blob/master/14%20activity%E5%90%AF%E5%8A%A8%E6%B5%81%E7%A8%8B.md  
http://aspook.com/2017/02/10/Android-Instrumentation%E6%BA%90%E7%A0%81%E5%88%86%E6%9E%90%EF%BC%88%E9%99%84Activity%E5%90%AF%E5%8A%A8%E6%B5%81%E7%A8%8B%EF%BC%89/  
深入理解Android内核设计思想  
https://blog.csdn.net/pihailailou/article/details/78545391  
https://blog.csdn.net/itachi85/article/details/64123035  
https://blog.csdn.net/qian520ao/article/details/81908505  
https://blog.csdn.net/luoshengyang/article/details/6689748  
https://blog.csdn.net/luoshengyang/article/details/6703247  
https://juejin.im/post/5c4180566fb9a049a62cdfd7  
https://juejin.im/post/5c469b23f265da614933efe8  
https://juejin.im/post/5c483eaff265da61327fa0e3  
https://blog.csdn.net/AndrExpert/article/details/81488503  
https://www.jianshu.com/p/a72c5ccbd150  
https://lrh1993.gitbooks.io/android_interview_guide/content/android/advance/app-launch.html  







