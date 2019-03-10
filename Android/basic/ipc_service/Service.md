### Service#Thread  
Service 与 Thread没有任何关系;  
Service 是工作在主线程, Thread工作在子线程;  
Service 的运行使用 start, stop,  或者 bind, unbind;  
Thread 的运行使用 start 和外部控制变量;  

Service的基本描述  
服务是运行在后台的, 没有UI的组件, 必须要在清单文件注册;  
服务运行在主线程;  
服务的启动方式有两种:  bind 和 start;  
服务的寄存方式有两种: LocalService 和 RemoteService; 

### 远程服务#RemoteService  
远程服务也被称作为, 独立的进程(pid不一样), 不受其他进程, 主进程的影响;  
将一个普通的 Service 转换成远程 Service, 只需要在注册 Service 添加属性 android:process=":remote" 即可;  
当然, 是以 : 开头, 后面是名字, 可以不叫 remote;  
MainActivity  
```
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState ?: Bundle())
    setContentView(R.layout.activity_main)
    LogTrack.w("进程id = " + Process.myPid())  // 进程id = 8328
}
```
LocalDownloadService  
```
@Override
public void onCreate() {
    super.onCreate();
    LogTrack.w("进程id = " + Process.myPid());  // 进程id = 8328
}
```
RemoteDownloadService  
```
@Override
public void onCreate() {
    super.onCreate();
    LogTrack.w("进程id = " + Process.myPid());  //  进程id = 8374
}
```
优点:  
1.. 远程服务有自己的独立进程, 不会受到其它进程的影响;  
2.. 可以被其它进程复用, 提供公共服务;  
3.. 具有很高的灵活性;  

缺点:  
相对普通服务, 占用系统资源较多, 需要使用 AIDL 进行 IPC 也相对麻烦;  

### 前台服务#ForegroundService   
Service 几乎都是在后台运行, 一直默默地做着辛苦的工作, 但这种情况下, 后台运行的 Service 系统优先级相对较低, 当系统内存不足时, 在后台运行的 Service 就有可能被回收;  
那么, 如果我们希望 Service 可以一直保持运行状态, 且不会在内存不足的情况下被回收时, 可以选择将需要保持运行的 Service 设置为前台服务;  
App 中的音乐播放服务, 应被设置在前台运行(前台服务), 在App后台运行时, 便于用户明确知道它的当前操作, 在状态栏中指明当前歌曲信息, 提供对应操作;  
```
<!-- api >= 28 startForeground 需要权限-->  
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```
### Service#启动方式  
启动一个服务  
启动一个服务有两种方式, start 与 bind;  
如果需要在运行时, context 和 service传递参数, 要用 bind 方式, 如果是一个远程服务, 还需要结合 AIDL;  
如果只需要在启动时, 给 service 传递参数, start 和 bind 都是可以的;  
start 方式, 调用简单, 方便控制, 无法对服务内部状态进行操控, 缺乏灵活性;    
bind 方式, 调用稍难, 运用灵活, 可以通过 IBinder 接口中获取 Service 的句柄, 对 Service 状态进行检测;  
从 Android 系统设计的架构上看, startService() 是用于启动本地服务, bindService() 更多是用于对远程服务进行绑定;  
两中启动方式可以混合使用;  

#### start 方式  
start 一个服务, onCreate 方法, 只会被调用一次;  
onStartCommand 方法执行的次数, 等于 startService 被调用的次数; 


隐式意图  
如果启动 service 的 intent 的 component 和 package 都为空并且版本大于LOLLIPOP(5.0)的时候,直接抛出异常;  
调用 setPackage, 解决问题;  
```
private void startService(String serviceAction) {
    Context context = BaseUtil.context();
    Intent intent = new Intent();
    intent.setAction(serviceAction);
    intent.setPackage(context.getPackageName());
    context.startService(intent);
}
```

#### bindService  
bind 一个服务, onCreate 和 onBind 方法, 只会被调用一次;  
多次调用 bindService, 不会引起任何生命周期的回调;  
unBindService 只能调用一次, 多次调用会报错;  

```
var downloadEntityAidl: IDownloadEntityAidlInterface? = null  
bindService(remoteServiceIntent, rdsConnection, Context.BIND_AUTO_CREATE)  
```
发消息  
```
downloadEntityAidl?.addMessage(DownloadMessageEntity(
        time = "2017-12-11  = ${Random().nextInt(100)}",
        content = "hello in Activity",
        title = "消息来了"))
        ?: ToastUtil.shortCenter("请先开启RemoteService")
```

Connection  
```
private inner class RemoteDownloadServiceConnection : ServiceConnection {
    override fun onServiceDisconnected(name: ComponentName?) {
        //LogTrack.w(name)
    }

    override fun onServiceConnected(name: ComponentName?, iBinder: IBinder?) {
        downloadEntityAidl = IDownloadEntityAidlInterface.Stub.asInterface(iBinder)
    }
}

```
绑定与解绑的时机  
如果只需要在 Activity 可见时与服务交互, 则应在 onStart() 期间绑定, 在 onStop() 期间取消绑定;  
如果希望 Activity 在后台停止运行状态下仍可接收响应, 则可在 onCreate() 期间绑定, 在 onDestroy() 期间取消绑定;  
通常情况下(注意), 切勿在 Activity 的 onResume() 和 onPause() 期间绑定和取消绑定, 因为每一次生命周期转换都会发生这些回调, 这样反复绑定与解绑是不合理的;  
此外, 如果应用内的多个 Activity 绑定到同一服务, 并且其中两个 Activity 之间发生了转换, 则如果当前 Activity 在下一次绑定(恢复期间), 之前取消绑定(暂停期间);  
系统可能会销毁服务并重建服务, 因此服务的绑定不应该发生在 Activity 的 onResume() 和 onPause()中;  



### Service#生命周期  
只使用 startService  
startService  ⤑  onCreate  ⤑   onStartCommand  ⤑  【service running】  ⤑  stopService  ⤑  onDestroy  

只使用 bindService  
bindService  ⤑  onCreate  ⤑  onBind  ⤑  【client are bind to service】  ⤑  onUnbind  ⤑  onDestroy  

关于 startService 与 bindService 之间的转换问题  
不管是 startService 还是 bindService, 操作的是同一个 Service 实例;  

先 bind 后 start  
如果当前 Service 实例先以 bind 方式运行, 然后再以 start 方式运行, 那么 bind 状态将会转为  start 状态运行,   
这时如果之前绑定的宿主(Activity)被销毁了, 也不会影响服务的运行, 服务还是会一直运行下去, 直到收到调用停止服务, 或者内存不足时才会销毁该服务;  

先 start 后 bind  
如果当前 Service 实例先以 start 方式运行, 然后再以 bind 方式运行, 当前 start 状态并不会转为 bind 状态,   
但是还是会与宿主绑定, 即使宿主解除绑定后, 服务依然按 start 方式的生命周期在后台运行,   
直到有 Context 调用了stopService(), 或是服务本身调用了stopSelf()方法, 或内存不足时才会销毁服务;  

### onDestroy  

以 startService 启动 service, 调用 stopService 结束时, 触发此方法;  
以 bindService 启动 service, 调用 unbindService 结束时, 触发此方法;  
先以 startService 启动服务, 再用 bindService 绑定服务, 结束时必须, 先调用 unbindService 解绑, 再调用 stopService 结束 service 才会触发此方法;  

### onStartCommand 方法
由于手机的 RAM, 内部资源有限, 所以很多 Service 都会因为资源不足而被 Kill 掉, 这时候返回值就决定了 Service 被Kill后的处理方式,  
一般 int onStartCommand(intent,flags,startId)的返回值分为以下几种:  
```
START_STICKY = 1  
START_CONTINUATION_MASK = 15  
START_NOT_STICKY = 2  
START_REDELIVER_INTENT = 3  
START_STICKY_COMPATIBILITY = 0  
START_FLAG_RETRY = 2  
START_FLAG_REDELIVERY = 1  
```
START_STICKY  
如果 service 进程被kill掉, 系统会尝试重新创建 Service, 如果在此期间没有任何启动命令被传递到 Service, 那么参数 intent 将为 null;  

START_NOT_STICKY  
使用这个返回值时, 如果在执行完 onStartCommand()后, 服务被异常 kill 掉, 系统不会自动重启该服务;  

START_REDELIVER_INTENT  
使用这个返回值时, 如果在执行完 onStartCommand()后, 服务被异常 kill 掉, 系统会自动重启该服务, 并将 intent 的值传入;  

START_STICKY_COMPATIBILITY  
START_STICKY 的兼容版本, 但不保证服务被 kill 后一定能重启;  
而输入参数 flags 正是代表此次 onStartCommand()方法的启动方式, 正常启动时, flags 默认为0, 被 kill 后重新启动, 参数分为以下两种:   
```
START_FLAG_RETRY  
代表 service 被 kill 后重新启动, 由于上次返回值为 START_STICKY, 所以参数 intent 为 null  

START_FLAG_REDELIVERY  
代表 service 被 kill 后重新启动, 由于上次返回值为 START_REDELIVER_INTENT, 所以带输入参数 intent  
```
### ServiceConnection  
//  与服务器端交互的接口方法, 绑定服务的时候被回调, 在这个方法获取绑定 Service 传递过来的 IBinder 对象,  
//  通过这个IBinder对象, 实现宿主和Service的交互;  
onServiceConnected  

//  当取消绑定的时候被回调;  
//  但正常情况下是不被调用的, 它的调用时机是当 Service 被意外销毁时, 例如内存的资源不足时, 这个方法才被自动调用;
//  Service 被强杀, 也不会回调;  
onServiceDisconnected  


### 向Service传递参数  
context  
```
startService(serviceIntent.apply {
    putExtra("name", "Alex")
})
```
Service    
```
@Override
public int onStartCommand(Intent intent, int flags, int startId) {
    LogTrack.i("onStartCommand");
    LogTrack.i(intent.getStringExtra("name"));
    return super.onStartCommand(intent, flags, startId);
}
```

bind传递参数    
原来, 想要跟给service传递数据, 从service中拿数据, 就要用bind, 远程service还要结合AIDL;  
这里说的给service传递数据, 并不仅仅限制与在start的时候, 用intent传递数据, 还可以是在运行时的某个节点上, 传递数据;    
### IntentService  
startService 会触发一次 onCreate 和 多次 onStartCommand;  
onHandleIntent 执行在 子线程, 当耗时操作结束以后, 会触发 onDestroy 回调, 再次 startService 会再次触发完整的生命周期;  

### Service#清单文件#属性  
//  服务全类名;  
android:name  
//  服务的名字, 如果为空, 默认显示的服务名为类名  
android:label  
//  服务的图标  
android:icon  
//  申明此服务的权限, 这意味着只有提供了该权限的应用才能控制或连接此服务  
android:permission  

android:process  
```
默认为空, 表示服务在当前进程, 即主进程运行;  
自定义时, 表示服务在新的进程运行, 所以 运行时 传递数据, 需要AIDL;  
android:process=":name"  一般用:remote表示远程服务;  

```
//  如果此项设置为 true, 那么 Service 将会默认被系统启动, 默认值为 false  
android:enabled  

//  代表是否能被隐式调用, 需要配合intent-filter使用
//  可以被其他 app 调用  
android:exported  
```
<intent-filter>
    <action android:name="com.alex.andfun.service.back.LocalDownloadService" />
</intent-filter>
```  
### JobService  
1.. 版本要求  Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP  
2.. 权限  
```
<service
    android:name=".huggles.LollipopDownloadJobService"
    android:permission="android.permission.BIND_JOB_SERVICE"/>
```
3.. startService 没有效果, 必须使用 jobScheduler.schedule(jobInfoBuilder.build()), 才能让 service 工作;  
4.. JobInfo.Builder 必备参数, 要配置完成, 否则会爆  You're trying to build a job with no constraints, this is not allowed.  

//  返回 false, 系统认为, 这个方法结束时, 任务已经执行完毕;  
//  返回 true, 需要手动维护任务状态, 调用 jobFinished;  
onStartJob  

//  其中 needsReschedule 表示是否重复执行, 任务执行完毕后调用, 该方法不会回调 onStopJob(),会回调 onDestroy()  
jobFinished  

//  循环触发, 设置任务每三秒定期运行一次  
// 和 setMinimumLatency |  setOverrideDeadline 这两个方法, 均互斥, 会报异常;  
setPeriodic(AlarmManager.INTERVAL_DAY)   //  循环执行, 循环时长为一天(最小为15分钟)  

//  设置任务的最小延迟执行时间(单位是毫秒),  
jobBuilder.setMinimumLatency();  

//  设置任务最晚的延迟时间;  
//  如果到了规定的时间, 其他条件还未满足, 任务也会被启动;  
jobBuilder.setOverrideDeadline();    

//  设置当设备重启之后, 该任务是否还要继续执行;  
// 需要 RECEIVE_BOOT_COMPLETED 权限, 否则会报错;  
jobBuilder.setPersisted();  

//  设置任务只有在, 满足指定的网络条件时才会被执行;  
//  JobInfo.NETWORK_TYPE_NONE,无论是否有网络均可触发, 这个是默认值;  
//  JobInfo.NETWORK_TYPE_ANY, 有网络连接时就触发;  
//  JobInfo.NETWORK_TYPE_UNMETERED, 非蜂窝网络中触发;  
//  JobInfo.NETWORK_TYPE_NOT_ROAMING, 非漫游网络时才可触发;  
jobBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);  


//  设置手机充电状态下, 是否执行任务  
jobBuilder.setRequiresCharging(true);  

//  设置空闲状态时, 是否执行任务(7.0上新加的)  
jobBuilder.setRequiresDeviceIdle(true);  

//  设置监控ContentUri 发生改变时, 是否执行任务(7.0上新加的)  
addTriggerContentUri  

//  设置开始安排任务, 它将返回一个状态码  
//  JobScheduler.RESULT_SUCCESS, 成功  
//  JobScheduler.RESULT_FAILURE, 失败  
```
if (mJobScheduler.schedule(jobInfo) == JobScheduler.RESULT_FAILURE) {
    //安排任务失败  
}
```

//  停止指定JobId的工作服务  
mJobScheduler.cancel(JOB_ID);  

//  停止全部的工作服务  
mJobScheduler.cancelAll();  

### 其他  
```
if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
    // Attach component of GCMIntentService that will handle the intent in background thread
    ComponentName componentName = new ComponentName(context.getPackageName(), MyFirebaseMessageService.class.getName());
    // Start the service, keeping the device awake while it is launching.
    startWakefulService(context, intent.setComponent(componentName));
    setResultCode(Activity.RESULT_OK);
}
```


### 参考  
原理  
https://www.jianshu.com/p/8170b9f1e4af  
https://www.jianshu.com/p/37f366064b98  
https://www.jianshu.com/p/411b504902db  
https://www.jianshu.com/p/37f366064b98  
https://blog.csdn.net/newhope1106/article/details/53843809  

全面剖释 Service 服务  
https://blog.csdn.net/javazejian/article/details/52709857  
https://www.cnblogs.com/leslies2/p/5401813.html  
http://www.jianshu.com/p/28c5377c77c4  
https://www.jianshu.com/p/c1a9e3e86666      

保活  
https://www.jianshu.com/p/76af1e7503a5  
https://www.jianshu.com/p/b5371df6d7cb  
https://www.jianshu.com/p/06a1a434e057  
https://blog.csdn.net/two_water/article/details/52126855  
https://www.jianshu.com/p/63aafe3c12af  
https://www.jianshu.com/p/b16631a2fe3c  
https://blog.csdn.net/zgzhaobo/article/details/51462292  
https://www.jianshu.com/p/cb2deed0f2d8  
https://www.jianshu.com/p/2ad105f54d07  
https://www.jianshu.com/p/8f9b44302139  
https://blog.csdn.net/andrexpert/article/details/75045678  
https://blog.csdn.net/guojin08/article/details/79623311  
https://blog.csdn.net/yi_master/article/details/80025706  
https://blog.csdn.net/weixin_42580207/article/details/80873106  
https://www.jianshu.com/p/53c4d8303e19  
https://www.jianshu.com/p/7cdae4f7763a  
https://bbs.csdn.net/topics/392382673  
https://blog.csdn.net/hlq19901005/article/details/53818186  
https://blog.csdn.net/hellojackjiang2011/article/details/85283746  
https://blog.csdn.net/hello_json/article/details/84954039  
https://blog.csdn.net/andrexpert/article/details/75045678  
https://blog.csdn.net/bboyfeiyu/article/details/44809395  
https://blog.csdn.net/hlq19901005/article/details/53818186  
https://blog.csdn.net/desireyaoo/article/details/78904681  
https://blog.csdn.net/CrazyMo_/article/details/83026230  
https://blog.csdn.net/aa642531/article/details/83687381  
https://blog.csdn.net/WHB20081815/article/details/68943545  
https://blog.csdn.net/Dream_go888/article/details/79566946  
https://blog.csdn.net/desireyaoo/article/details/78904681  
https://blog.csdn.net/haienzi/article/details/81154903  
https://blog.csdn.net/hpc19950723/article/details/70175927  


JobService  
https://blog.csdn.net/findsafety/article/details/80388430  
https://www.jianshu.com/p/9fb882cae239  

AlarmManager  
https://juejin.im/entry/588628e8128fe10065eb62a9  
