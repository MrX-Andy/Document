### 优化稳定性, 低功耗, 性能优化    
Application, Activity 启动的时候, 不能有过多的操作, 如果需要耗时操作, 要用多线程处理;  
尽量多处理异常, 保障应用不会出现 crash;  
网络请求之前, 先判断 网络是否通路,  否则不发起请求;  
大一点的对象, array, list, map, 在不用的时候, 需要及时clear;  
使用上下文相关, 可以结合 弱引用;  
广播, EventBus 之类的, 也要及时解除注册;  
最好不要静态持有 Activity, 或者有 较长生命周期的对象, 不要持有较短生命周期的对象引用, 会造成内存泄漏;  
在onStop 或者 onPause 暂停  
```
对于循环动画, GPU不断刷新视图也是很耗电的;
对于一些 sensors 如gps监听等, 也许要暂停;
```  
对于 Bitmap 或者本地的 Drawable, 最好先压缩处理再展示, 或者用 Glide等开源框架做处理;  
组合控件, 自定义控件,  使用 merge 优化层级结构;  
循环语句里面, 不要重复造对象的引用, 字符串拼接最好要使用 StringBuild;  
HashMap, ArrayList 初始化时, 最好预估计其容量, 扩容也是比较耗时的;

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

参考如何计算 App 的启动时间  
```
adb shell am start -S -W 包名/启动类的全限定名 ,  -S 表示重启当前应用;  
adb shell am start -S -W com.example.moneyqian.demo/com.example.moneyqian.demo.MainActivity  
```
ThisTime: 最后一个 Activity 的启动耗时;  
TotalTime : 启动一连串的 Activity 总耗时;  
WaitTime : 应用进程的创建过程 + TotalTime;  
最后总结一下 , 如果需要统计从点击桌面图标到 Activity 启动完毕, 可以用WaitTime作为标准, 但是系统的启动时间优化不了, 所以优化冷启动我们只要在意 ThisTime 即可;  

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


### 参考  
https://www.jianshu.com/p/d5a843cb7ab1  
http://landerlyoung.github.io/blog/2014/10/31/androidzhong-de-wakelockshi-yong/  
http://www.jianshu.com/p/09d878e4c6ab  
http://blog.csdn.net/wh_19910525/article/details/8287202  
http://blog.csdn.net/airk000/article/details/9121003  
https://www.jianshu.com/p/d71b51a0e29f  
https://blog.csdn.net/yanbober/article/details/48394201  
https://juejin.im/post/5a0d30e151882546d71ee49e  
https://juejin.im/post/5c4eb83cf265da613572ef56  
https://juejin.im/entry/5c3e9ef6e51d4539b927dfd1  
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

ANR  
https://juejin.im/post/5be698d4e51d452acb74ea4c  
