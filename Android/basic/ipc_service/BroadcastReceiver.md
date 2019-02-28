### 广播接收者  
[广播注册](library/register_broadcast_receiver.md)  

注册广播接收器, 分两种: java 代码注册(动态注册), 清单文件注册(静态注册);  

1.. 静态广播  
对于静态广播, 例如 开机广播, 解锁广播, 屏幕点亮广播;  
在 android 3.1 之后, app 首次安装未启动过, 或者用户强杀 force stop 之后, 应用无法检测到这些广播;  
但在启动一次后, 就能够正常收到系统广播, 但是会导致手机启动变慢, 耗电明显;  

新安装的应用, 或者被用户强杀的应用, 会被置于"stopped"状态, 必须在用户主动打开这个应用, 才会改变状态, 能够正常接收到指定的广播消息;  
系统这样做的目的是, 防止广播无意或者不必要地开启未启动的 APP 的后台服务;  

如果想给这样的接收器发广播, 
```
<receiver android:name=".receiver.UpdateWidgetReceiver"
   android:exported="true">
   ...
 </receiver>
 
 Intent intent = new Intent();
 intent.setAction("xxx.action");
 intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
 sendBroadcast(intent);
```

2.. 动态广播  
对于动态注册的广播, 必须有对应的注销, 否则会造成内存泄漏;  
不允许重复注册, 不允许重复注销;  

### 广播的类型  
默认广播, Context#sendBroadcast();  
有序广播, Context#sendOrderedBroadcast();  
粘性广播, Context#sendStickyBroadcast();  
局部广播, 应用内广播, LocalBroadcast();  

有序是针对广播接收者而言的,  广播接受者按照 Priority 属性值从大-小排序, Priority 属性相同的, 动态注册的广播优先;  
先接收的广播接收者, 可以对广播进行截断, 那么后接收的广播接收者, 不再接收到此广播;  
先接收的广播接收者, 可以对广播进行修改, 那么后接收的广播接收者, 将接收到被修改后的广播;  

局部广播, 是 App 应用内广播, 广播的发送者, 和接收者都同属于一个 App;  
相比于全局广播(普通广播), 局部广播安全性高, 效率高;  

具体使用1 - 将全局广播设置成局部广播;  
注册广播时将 exported 属性设置为 false, 使得非本 App 内部发出的此广播不被接收;  
在广播发送和接收时, 增设相应权限 permission, 用于权限验证;
发送广播时指定该广播接收器所在的包名, 此广播将只会发送到此包中的, App内与之相匹配的有效广播接收器中;  
通过intent.setPackage(packageName)指定报名;  

具体使用2 - 使用封装好的LocalBroadcastManager类  
使用方式上与全局广播几乎相同, 只是注册/取消注册广播接收器和发送广播时将参数的context, 变成了 LocalBroadcastManager 的单一实例;  
对于 LocalBroadcastManager 方式发送的应用内广播, 只能通过 LocalBroadcastManager 动态注册, 不能静态注册;  

❀ 上下文是谁  
对于静态注册, onReceive 回调的 context 是 ReceiverRestrictedContext;  
对于动态注册, onReceive 回调的 context 是 注册时用的那个 context, 是 activity 或者 application;  
LocalBroadcastManager 方式, 动态注册的局部广播, onReceive 回调的 context 是 Application;  
非 LocalBroadcastManager方式, 动态注册的局部广播, onReceive 回调的 context 是 Activity;  

ReceiverRestrictedContext 和 Application 都是 ContextWrapper 的子类;  
Activity -> ContextThemeWrapper -> ContextWrapper;  

### 只能动态注册的广播  
```
//  计时器变化, 每分钟发送一次的广播  
public static final String ACTION_TIME_TICK = "android.intent.action.TIME_TICK";  

//  屏幕点亮  
public static final String ACTION_SCREEN_ON = "android.intent.action.SCREEN_ON";

//  屏幕关闭  
public static final String ACTION_SCREEN_OFF = "android.intent.action.SCREEN_OFF";

//  电量发生变化
public static final String ACTION_BATTERY_CHANGED = "android.intent.action.BATTERY_CHANGED";

// 设备当前设置被改变时
public static final String ACTION_CONFIGURATION_CHANGED = "android.intent.action.CONFIGURATION_CHANGED";
```

### 不同 App 发送广播  
A 接收广播  
```
<!-- 声明使用指定的权限 -->
<permission android:name="com.alex.andfun.service.RECEIVE" />
<uses-permission android:name="com.alex.andfun.service.RECEIVE" />

<receiver
        android:name=".context.PiKaChuReceiver"
        android:enabled="true"
        android:exported="true"
        android:permission="com.alex.andfun.service.RECEIVE">
            <intent-filter>
                <action android:name="com.alex.andfun.service.context.Action.start_from_stopped_state"/>
             </intent-filter>
</receiver>
```
B 发送广播  
```
<!-- 声明使用指定的权限 -->
<permission android:name="com.alex.andfun.service.RECEIVE" />

sendBroadcast(Intent().apply {
    action = ACTION_START_FROM_STOPPED_STATE
})
```

### 顺序广播  
顺序广播, 需要指定权限, 指定优先级, 只能在清单文件注册并配置;  
priority 数值在 -1000 到 1000 之间, 值越大, 优先级越高;  

```
<permission android:name="com.alex.andfun.service.ORDERED_RECEIVE"/>
<uses-permission android:name="com.alex.andfun.service.ORDERED_RECEIVE"/>

<receiver
        android:enabled="true"
        android:exported="true"
        android:name=".context.orderedreceiver.StitchOrdered2Receiver"
        android:permission="com.alex.andfun.service.ORDERED_RECEIVE">
    <intent-filter android:priority="200">
        <action android:name="com.alex.andfun.service.context.Action.stitch_ordered_receiver"/>
    </intent-filter>
</receiver>

btSend.setOnClickListener {
    sendOrderedBroadcast(
        Intent().apply {
            action = Action.STITCH_ORDERED
        }, Permission.ORDER_RECEIVER
    )
}

```
ContextWrapper#sendOrderedBroadcast(intent, receiverPermission, resultReceiver, Handler, initialCode, initialData, initialExtras)  
resultReceiver 指定一个最终的广播接收器, 相当于 finally 功能;  
优先级较高的 BroadcastReceiver 的 onReceive 方法中, 可以调用 abortBroadcast 中断广播的继续传播;  

### 粘性广播  
```
<uses-permission android:name="android.permission.BROADCAST_STICKY"/>  
```
粘性广播会一直保留下去, 除非调用了 removeStickyBroadcast, 或者手机重启了;  
如果在接收器注册之前, 已经存在多条相同 action 的广播, 那么接收器只会收到一条最新的消息内容;  
系统网络状态的改变发送的广播就是粘性广播;  


### 生命周期  
BroadcastReceiver 的生命周期非常短, 在 onReceive 方法内, 不可以做耗时操作, 如果有超过10秒, 会报ANR异常;  
 
建议在 onResume 中注册, 在 onPause 中解除注册;  

### 局部广播  
使用简单, 只能动态注册, 没有顺序广播;  

区分同步广播和异步广播:  
同步广播, 阻塞式处理, onReceive 方法和发送者在同一个线程;  
异步广播, 非阻塞处理, 发送者可以在任意线程, onReceive 方法都是在UI线程;  

```
private val localBroadcastManager = LocalBroadcastManager.getInstance(BaseUtil.context())
private val criBugReceiver = CriBugReceiver()
private val criBugFilter = IntentFilter(Action.CRI_BUG)

override fun onResume() {
    localBroadcastManager.registerReceiver(criBugReceiver, criBugFilter)
    super.onResume()
}

override fun onPause() {
    localBroadcastManager.unregisterReceiver(criBugReceiver)
    super.onPause()
}

localBroadcastManager.sendBroadcast(Intent().apply {
        action = Action.CRI_BUG
    })
```
全局广播会被注册到系统的AMS当中, 由于AMS任务繁忙, 一般可能不会立即能处理到我们发出的广播;    
如果我们使用广播是在应用内的单个进程中使用, 则完全可以采用 LocalBroadcastManager 来处理;    
局部广播采用的是 Handler 的消息机制来处理的, 而全局广播是通过 Binder 机制实现的, 局部广播响应速度更快;  
局部广播无法跨进程, 只能在同一个进程中使用, 广播信号不会被其他app捕获, 相对安全;  
使用简单, 只能动态注册, 没有顺序广播;  

### 原理  
Activity#sendBroadcast  
ContextWrapper#sendBroadcast  
ContextImpl#sendBroadcast  
ActivityManagerServiceNative#broadcastIntent  
ActivityManagerServiceNative#broadcastIntentLocked  
BroadcastQueue#scheduleBroadcastsLocked  
```
//  广播是通过 handler 实现的异步发送;  
mHandler.sendMessage(mHandler.obtainMessage(BROADCAST_INTENT_MSG, this));  
```
//  处理下一条广播  
BroadcastQueue#processNextBroadcast  
BroadcastQueue#processNextBroadcastLocked  
//  检查广播发送和接受的权限判断  
BroadcastQueue#deliverToRegisteredReceiverLocked  
BroadcastQueue#performReceiveLocked  
ApplicationThreadNative#scheduleRegisteredReceiver  
ApplicationThread#scheduleRegisteredReceiver  
LoadedApk.ReceiverDispatcher.InnerReceiver#performReceive  
LoadedApk.ReceiverDispatcher#performReceive  
LoadedApk.ReceiverDispatcher.Args#run  
BroadcastReceiver#onReceive  

第一阶段  
通过 sendBroadcast 把一个广播通过 Binder 机制发送给 AMS, AMS 根据这个广播的Action类型找到相应的广播接收器, 然后把这个广播放进自己的消息队列中去;  
AMS 在消息循环中处理这个广播, 并通过 Binder 机制, 把这个广播分发给注册的广播接收分发器 ReceiverDispatcher,  
ReceiverDispatcher 把这个广播放进 MainActivity 所在的线程的消息队列中去;  
ReceiverDispatcher 的内部类 Args 在 MainActivity 所在的线程消息循环中处理这个广播, 最终将这个广播分发给 BroadcastReceiver#onReceive 函数进行处理;  

### 参考  
原理  
https://www.jianshu.com/p/d0ab021a65f9  
https://www.jianshu.com/p/37f366064b98  


https://blog.csdn.net/luoshengyang/article/details/6744448  
https://www.jianshu.com/p/abb173858faf  
https://www.kancloud.cn/alex_wsc/androids/477751  
https://www.jianshu.com/p/ca3d87a4cdf3  
https://www.cnblogs.com/lwbqqyumidi/p/4168017.html  
http://www.aoaoyi.com/archives/342.html  
https://www.jianshu.com/p/02085150339c  
https://www.jianshu.com/p/abb173858faf  


局部广播  
https://blog.csdn.net/u010687392/article/details/49744579
http://www.trinea.cn/android/localbroadcastmanager-impl  
https://www.jianshu.com/p/e110d5860a6b  
http://gityuan.com/2017/04/23/local_broadcast_manager  
https://juejin.im/entry/589b1c342f301e00699910f3  
http://aspook.com/2017/10/12/LocalBroadcastManager原理分析及应用  
https://huangtianyu.gitee.io/2018/01/17/Android应用内部广播机制详解  

   