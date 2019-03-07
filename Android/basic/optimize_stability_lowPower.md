### 优化稳定性, 低功耗, 性能优化    
Application, Activity 启动的时候, 不能有过多的操作, 如果需要耗时操作, 要用多线程处理;  
尽量多处理异常, 保障应用不会出现 crash;  
网络请求之前, 先判断 网络是否通路,  否则不发起请求;  
大一点的对象, array, list, map, 在不用的时候, 需要及时clear;  
使用上下文相关, 可以结合弱引用;  
广播, EventBus 之类的, 也要及时解除注册;  
最好不要静态持有 Activity, 或者有 较长生命周期的对象, 不要持有较短生命周期的对象引用, 会造成内存泄漏;  
在onStop 或者 onPause 暂停  
```
对于循环动画, GPU不断刷新视图也是很耗电的;
对于一些 sensors 如gps监听等, 也许要暂停;
```  
对于 Bitmap 或者本地的 Drawable, 最好先压缩处理再展示, 或者用 Glide等开源框架做处理;  
循环语句里面, 不要重复造对象的引用, 字符串拼接最好要使用 StringBuild;  
HashMap, ArrayList 初始化时, 最好预估计其容量, 扩容也是比较耗时的;
自定义 view, 不要再 onDraw 中, 频繁初始化对象, 不要做耗时操作, 因为 onDraw 方法可能会经常被调用;  
移除默认的 window 背景色, 移除控件中不必要的背景色, 减少 view 的层级关系, 换一种实现方案, 减少 view 的层级;  
组合控件, 自定义控件,  使用 merge 优化层级结构;  
使用 ViewStub, 优化加载时机, 只有当 ViewStub 调用了ViewStub.inflate()时, ViewStub 所指向的布局文件才会被 inflate , 实例化, 最终 显示 <ViewStub> 指向的布局;  
布局简单时, 使用 FrameLayout-LinearLayout, 布局复杂时, 使用 RelativeLayout-ConstraintLayout;  
使用 include 提高布局的复用性, 优化测量和绘制的时间;  
尽量少使用 wrap_content, 减少 measure 时, 协商测量的时间;  


```
<item name="android:windowBackground">@android:color/transparent</item>
或者 
<item name="android:windowBackground">@null</item>

getWindow().setBackgroundDrawable(null);
或者  
getWindow().setBackgroundDrawableResource(android.R.color.transparent);  
```
原色: 没有过度绘制  
蓝色: 过度绘制 1 次   
绿色: 过度绘制 2 次  
粉色: 过度绘制 3 次  
红色: 过度绘制 4 次或更多  

代码混淆:  
移除不必要的库, 类, 方法, 
### IdleHandler  
```
// 将费时的不紧急的事务放到IdleHandler中执行
Looper.myLooper().getQueue().addIdleHandler(new MessageQueue.IdleHandler() {
    @Override
    public boolean queueIdle() {
        // 判断当前webView是否存在
        boolean webViewExist = true;
        try {
            String fakeAddress = "120 Main St NewYork,NY";
            // Try whether webView can be used or not.
            WebView.findAddress(fakeAddress);
        } catch (Exception e) {
            webViewExist = false;
        }
        ...
        return false;
    }
});

```
### 冷启动优化  

冷启动（Cold start）  
冷启动是指APP在手机启动后第一次运行, 或者APP进程被kill掉后在再次启动;  
可见冷启动的必要条件是该APP进程不存在, 这就意味着系统需要创建进程, APP需要初始化;  

温启动(Warm start)  
App进程存在, 当时Activity可能因为内存不足被回收。这时候启动App不需要重新创建进程, 但是Activity的onCrate还是需要重新执行的;  

热启动(Hot start)  
App进程存在, 并且Activity对象仍然存在内存中没有被回收。可以重复避免对象初始化, 布局解析绘制。  


设置闪屏图片主题  
为了更顺滑无缝衔接我们的闪屏页, 可以在启动 Activity 的 Theme中设置闪屏页图片, 这样启动窗口的图片就会是闪屏页图片, 而不是白屏;  
```
<style name="SplashTheme" parent="Theme.AppCompat.Light.NoActionBar">
    <item name="android:windowBackground">@drawable/lunch</item>  //闪屏页图片
    <item name="android:windowFullscreen">true</item>
    <item name="android:windowDrawsSystemBarBackgrounds">false</item><!--显示虚拟按键, 并腾出空间-->
</style>

<activity android:name=".SplashActivity"
    android:theme="@style/SplashTheme">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />

        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```
该方案注意要点  

给Preview Window设置的背景图如果不做处理, 图片就会一直存在于内存中, 所以, 当我们进入到欢迎页的时候, 不要忘了把背景图设置为空;   
```
//  SplashActivity   
@Override
protected void onCreate(@Nullable Bundle savedInstanceState) {
    //将window的背景图设置为空
    getWindow().setBackgroundDrawable(null);
    super.onCreate(savedInstanceState);
}
```
启动页面屏蔽返回按键  
```
//  SplashActivity   
@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
        return true;
    }
    return super.onKeyDown(keyCode, event);
}
```
对初始化做一下分类:  
必要的组件一定要在主线程中立即初始化(入口 Activity 可能立即会用到);  
组件一定要在主线程中初始化, 但是可以延迟初始化;  
组件可以在子线程中初始化;  

例如:  
1.. 将Bugly, x5内核初始化, SP的读写, 友盟等组件放到子线程中初始化, 
```
new Thread(new Runnable() {
    @Override
    public void run() {
        //设置线程的优先级, 不与主线程抢资源
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        //子线程初始化第三方组件
        Thread.sleep(5000);//建议延迟初始化, 可以发现是否影响其它功能, 或者是崩溃！
    }
}).start();
```
### 计算启动时间  
如何计算 App 的启动时间, adb 计算的事件有误差  
```
adb shell am start -S -W 包名/启动类的全限定名 ,  -S 表示重启当前应用;  
adb shell am start -S -W com.example.moneyqian.demo/com.example.moneyqian.demo.MainActivity  
```
ThisTime: 最后一个 Activity 的启动耗时;  
TotalTime : 启动一连串的 Activity 总耗时;  
WaitTime : 应用进程的创建过程 + TotalTime;  
最后总结一下 , 如果需要统计从点击桌面图标到 Activity 启动完毕, 可以用WaitTime作为标准, 但是系统的启动时间优化不了, 所以优化冷启动我们只要在意 ThisTime 即可;  

怎么精确统计?  
Application#attachBaseContext 是进程启动的回调, 冷启动开始的回调;  
Activity#onWindowFocusChanged 是Activity绘制完成的回调, 冷启动结束的回调;  

### ANR#分析  
KeyDispatchTimeout  
input 事件在5S内没有处理完成发生了ANR;  
logcat 日志关键字: Input event dispatching timed out;  

BroadcastTimeout  
前台 Broadcast, onReceive 在10S内没有处理完成发生ANR;  
后台 Broadcast, onReceive 在60s内没有处理完成发生ANR;  
logcat 日志关键字: Timeout of broadcast BroadcastRecord;  

ServiceTimeout  
前台 Service, onCreate-onStart-onBind 等生命周期在20s内没有处理完成发生ANR;  
后台 Service, onCreate-onStart-onBind 等生命周期在200s内没有处理完成发生ANR;  
logcat 日志关键字: Timeout executing service;  

ContentProviderTimeout  
ContentProvider 在10S内没有处理完成发生ANR;  
logcat 日志关键字: timeout publishing content providers;  

❀ ANR出现的原因  
1.. 主线程频繁进行耗时的IO操作, 如数据库读写;  
2.. 多线程操作的死锁, 主线程被block住;  
3.. 主线程被 Binder 对端block;  
4.. System Server中WatchDog出现ANR;  
5.. service binder的连接达到上线无法和和 System Server通信;  
6.. 系统资源已耗尽, 如: 管道-CPU-IO;   

### StrictMode  
StrictMode 自 API 9 开始引入, 某些API方法也从 API 11 引入, 使用时应该注意 API 级别;  
查看结果,  adb logcat | grep StrictMode  
通常情况下 StrictMode 给出的耗时相对实际情况偏高, 并不是真正的耗时数据;  
无法监控 JNI 中的磁盘 IO 和网络请求;  

ThreadPolicy  
线程策略检测的内容有  
自定义的耗时调用 使用 detectCustomSlowCalls() 开启  
磁盘读取操作 使用 detectDiskReads() 开启  
磁盘写入操作 使用 detectDiskWrites() 开启  
网络操作 使用 detectNetwork() 开启  

VmPolicy  
虚拟机策略检测的内容有  
Activity泄露 使用 detectActivityLeaks() 开启  
未关闭的 Closable 对象泄露 使用 detectLeakedClosableObjects() 开启  
泄露的 SqLite 对象 使用 detectLeakedSqlLiteObjects() 开启  
检测实例数量 使用 setClassInstanceLimit() 开启  
```
if (BuildConfig.DEBUG) {
    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
            .detectAll()  /*来检测所有想检测的东西*/
            .detectActivityLeaks()  /*检测Activity内存泄露*/
            .detectLeakedClosableObjects()  /*检测未关闭的Closable对象*/
            .detectLeakedSqlLiteObjects()   /*检测Sqlite对象是否关闭*/
            .penaltyLog().build());
    
    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
            .detectAll()  /*来检测所有想检测的东西*/
            .detectDiskReads()/*磁盘读取操作检测*/
            .detectDiskWrites()/*检测磁盘写入操作*/
            .detectNetwork() /*检测网络操作*/
            .penaltyLog().build());
}
```
### 性能优化工具  
Systrace  
Android 4.1 以上版本提供的性能数据采样 & 分析工具;  
检测 Android 系统各个组件随着时间的运行状态 & 提供解决方案;  
收集和检测事件信息, FPS-代码耗时;  
收集运行信息, 从而帮助开发者更直观地分析系统瓶颈, 改进性能;  
检测范围包括, Android 关键子系统, 如 WindowManagerService 等 Framework 部分关键模块, 服务, View 系统;  
跟踪系统的 I/O 操作, 内核工作队列, CPU 负载等, 在 UI 显示性能分析上提供很好的数据, 特别是在动画播放不流畅, 渲染卡等问题上;  

Hierarchy Viewer  
查看布局层级, 过度绘制检测, FPS, 方便查看 Activity 布局, 各个 View 的属性, 布局测量-布局-绘制的时间;  
TraceView: 图形化的代码执行时间分析工具, FPS-代码耗时;  
LeakCanary: 内存泄漏检测;  
Lint: 隐藏问题检测, 降低 cash 率;  

Profile GPU Rendering  
一个 图形监测工具, 渲染, 绘制性能追踪, 能实时反应当前绘制的耗时,  
横轴 = 时间, 纵轴 = 每帧的耗时, 随着时间推移, 从左到右的刷新呈现;  
提供一个标准的耗时, 如果高于标准耗时, 就表示当前这一帧丢失;  

MAT(Memory Analysis Tools)  
一个 Eclipse 的 Java Heap 内存分析工具,  查看当前内存占用情况,   
通过分析 Java 进程的内存快照 HPROF 分析, 快速计算出在内存中对象占用的大小, 查看哪些对象不能被垃圾收集器回收, 可通过视图直观地查看可能造成这种结果的对象;  

Memory Monitor  
Heap Viewer  
一个的 Java Heap 内存分析工具, 查看当前内存快照;  
可查看 分别有哪些类型的数据在堆内存总 & 各种类型数据的占比情况;  

Allocation Tracker  
一个内存追踪分析工具, 追踪内存分配信息，按顺序排列  

### 参考  
https://www.jianshu.com/p/f5514b1a826c  
https://www.jianshu.com/p/4f44a178c547  
https://www.jianshu.com/p/c4b283848970  
https://www.jianshu.com/p/e49ec7d053b3  
https://www.jianshu.com/p/17b0ac9b40fa  
https://www.jianshu.com/p/af13abc5f0c8  
https://www.jianshu.com/p/d5a843cb7ab1  
https://www.jianshu.com/p/5d83d8649c98  
https://www.jianshu.com/p/99f3c09982d4  
https://www.jianshu.com/p/5f99f0e51118  
https://www.jianshu.com/p/7eb182df06d3  
https://www.jianshu.com/p/f3d90be42719  
https://juejin.im/post/5c6b97e96fb9a049c85005c9  
https://blog.csdn.net/carson_ho/article/details/79549417  
https://blog.csdn.net/carson_ho/article/details/79674623  
https://chinagdg.org/google-videos/?vid=XMTQ5ODk1Njk4NA==&plid=26876905  
https://chinagdg.org/google-videos/?vid=XMTI2NDk2ODY2MA==&plid=23758799  
https://chinagdg.org/google-videos/?vid=XMTMxNDE5MjQwNA==&plid=25972284  
https://chinagdg.org/google-videos/?vid=XMTQwODc0MzE0MA==&plid=26144822  
https://chinagdg.org/google-videos/?vid=XMTQ4MDU3Nzc3Mg==&plid=26771407  
https://chinagdg.org/google-videos/?vid=XMTY4NzY1NjAyNA==&plid=27923639  

内存泄漏  
https://hk.saowen.com/a/f50ff716f65bb6a39e3bcc5f9cb3adfe9a13d6a1839196905b278348dc76e39b  
https://hk.saowen.com/a/7fbf7cc5ab331bd20b3ecbf353658f85364e98f26cbfaaad45623acb930ec1ef  
https://hk.saowen.com/a/640a8ebc0073b860b139a748ca73bffa7a27371599a655acade392210661055b  
https://hk.saowen.com/a/87e29c38c0bf3be74aab16376b704076034e61a90b752de617cbec73f93dd5b2  
https://hk.saowen.com/a/75acd3ae584edd55df9006d72e0f711c4905f3ded04b5865a7c2e4c21cdf9a81  
https://blog.csdn.net/self_study/article/details/61919483  
https://blog.csdn.net/self_study/article/details/66969064  
https://blog.csdn.net/self_study/article/details/68946441  

ANR  
https://juejin.im/post/5be698d4e51d452acb74ea4c  

StrictMode  
https://blog.csdn.net/u014099894/article/details/52917088  
https://droidyue.com/blog/2015/09/26/android-tuning-tool-strictmode  

性能优化  
https://blog.csdn.net/woyaowenzi/article/details/9273839  
https://blog.csdn.net/csdn_aiyang/article/details/74989318  
https://www.jianshu.com/p/bef74a4b6d5e  

启动时间  
https://www.jianshu.com/p/59a2ca7df681  

Profile GPU Rendering  
https://www.jianshu.com/p/061bb80025c7  

Systrace  
http://gityuan.com/2016/01/17/systrace/  

MAT  
https://www.eclipse.org/mat/  
https://blog.csdn.net/itomge/article/details/48719527  

Heap Viewer  
https://blog.csdn.net/zhangfei2018/article/details/49154479  

Allocation Tracker  
https://www.kancloud.cn/digest/itfootballprefermanc/100908  

Memory Monitor  
https://blog.csdn.net/true100/article/details/52604910  
