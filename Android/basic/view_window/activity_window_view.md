[Activity-Window-View层级嵌套结构](../context/ImageFiles/awv_001.jpg)    
PhoneWindow 是在 Activity#attach 方法中创建的;  
DecorView 是在 PhoneWindow#generateDecor 中创建的;  
Activity#setContentView 和 PhoneWindow#setContentView 的入口参数都是, 布局文件的 id;  
在 PhoneWindow#generateLayout 方法中, 调用 DecorView#onResourcesLoaded 把xml布局文件, 解析成View, 添加到 DecorView 上;  
再把xml根布局对应的view, 赋值给 mContentParent;  
每个 Activity 会在 attach 方法中, 初始化并持有一个 PhoneWindow;
Activity#setContentView  
```
public void setContentView(@LayoutRes int layoutResID) {
    getWindow().setContentView(layoutResID);
    initWindowDecorActionBar();
}
```
Activity#attach  
```
final void attach(Context context, ActivityThread aThread,
        Instrumentation instr, IBinder token, int ident,
        Application application, Intent intent, ActivityInfo info,
        CharSequence title, Activity parent, String id,
        NonConfigurationInstances lastNonConfigurationInstances,
        Configuration config, String referrer, IVoiceInteractor voiceInteractor,
        Window window, ActivityConfigCallback activityConfigCallback) {
    attachBaseContext(context);
    //  window 初始化  
    mWindow = new PhoneWindow(this, window, activityConfigCallback);
}
```
ActivityThread#performLaunchActivity  
```
private Activity performLaunchActivity(ActivityClientRecord r, Intent customIntent) {
    ContextImpl appContext = createBaseContextForActivity(r);
    Activity activity = null;
    try {
        java.lang.ClassLoader cl = appContext.getClassLoader();
        activity = mInstrumentation.newActivity(cl, component.getClassName(), r.intent);
    } catch (Exception e) {
    }
    try {
        Application app = r.packageInfo.makeApplication(false, mInstrumentation);
        if (activity != null) {
            activity.attach(appContext, this, getInstrumentation(), r.token,
                    r.ident, app, r.intent, r.activityInfo, title, r.parent,
                    r.embeddedID, r.lastNonConfigurationInstances, config,
                    r.referrer, r.voiceInteractor, window, r.configCallback);
        }
        r.setState(ON_CREATE);
        mActivities.put(r.token, r);
    } catch (SuperNotCalledException e) {
        throw e;
    } catch (Exception e) {
    }
    return activity;
}
```
PhoneWindow#setContentView  
```
@Override
public void setContentView(int layoutResID) {
    // Note: FEATURE_CONTENT_TRANSITIONS may be set in the process of installing the window
    // decor, when theme attributes and the like are crystalized. Do not check the feature
    // before this happens.
    if (mContentParent == null) {
        //  需要初始化 
        installDecor();
    } else if (!hasFeature(FEATURE_CONTENT_TRANSITIONS)) {
        mContentParent.removeAllViews();
    }

    if (hasFeature(FEATURE_CONTENT_TRANSITIONS)) {
        final Scene newScene = Scene.getSceneForLayout(mContentParent, layoutResID, getContext());
        transitionTo(newScene);
    } else {
        mLayoutInflater.inflate(layoutResID, mContentParent);
    }
}
```
PhoneWindow#installDecor  
```
private void installDecor() {
    mForceDecorInstall = false;
    if (mDecor == null) {
        //  初始化 decorView, decorView 是一个 FrameLayout    
        mDecor = generateDecor(-1);
    } else {
        mDecor.setWindow(this);
    }
    if (mContentParent == null) {
        //  初始化 mContentParent, 这个是 xml 的根布局
        mContentParent = generateLayout(mDecor);
    }    
}
```
每一个 Activity 都包含了唯一一个 PhoneWindow, 这个就是 Activity 根 Window;  
在它上面可以增加更多其他的 Window, 如dialog等;  

为什么要设计Activity-View-Window?  
View 是视图,  window是面板, 视图需要挂在到面板上, Activity负责管理面板的生命周期;  

Activity 有存在的必要吗?  
Window 已经是系统管理的窗口界面。那么为什么还需要Activity呢?  
其实, 本质上讲, 我们要显示一个窗口出来, 的确可以不需要 Activity, 悬浮窗口可以不依赖 Activity;  
Android 中的应用中, 对各个窗口的管理相当复杂, 包括窗口的入栈出栈, 焦点问题, 状态管理, 内存回收等等问题, 为了简化开发难度, 引入 Activity 的概念;  

Window 是什么? 它的职能是什么?  
Activity 内部持有 PhoneWindow 对象, 并通过 Window 的 addView(), removeView(), updateViewLayout() 等方法来管理 View 的;  
只给 Window 提供可以在这块区域上绘制图形的 Surface对象, 换句话说, 站在系统的角度上看, 系统是"不知道"有View对象的;  

Activity 的启动需要通过 AMS 完成;  
Window 的添加过程需要通过 WindowSession 完成;  

DecorView  
PhoneWindow  
WindowManagerService  
WindowManager  

### DecorView添加到Window上  
ActivityThread#handleResumeActivity  
```
@Override
public void handleResumeActivity(IBinder token, boolean finalStateRequest, boolean isForward,
        String reason) {
    final ActivityClientRecord r = performResumeActivity(token, finalStateRequest, reason);
    final Activity a = r.activity;
    final int forwardBit = isForward
            ? WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION : 0;
    if (r.window == null && !a.mFinished && willBeVisible) {
        //  获得当前Activity的PhoneWindow对象
        r.window = r.activity.getWindow();
        //  获得当前phoneWindow内部类DecorView对象
        View decor = r.window.getDecorView();
        decor.setVisibility(View.INVISIBLE);
        //  得当当前 Activity 的 WindowManagerImpl 对象
        ViewManager wm = a.getWindowManager();
        WindowManager.LayoutParams l = r.window.getAttributes();
        a.mDecor = decor;
        if (a.mVisibleFromClient) {
            if (!a.mWindowAdded) {
                //  标记根布局DecorView已经添加到窗口
                a.mWindowAdded = true;
                //  将根布局DecorView添加到当前Activity的窗口上面
                wm.addView(decor, l);
            } else {
                a.onWindowAttributesChanged(l);
            }
        }
        
    } else if (!willBeVisible) {
        r.hideForNow = true;
    }
}
```
WindowManagerImpl#addView  
WindowManagerGlobal#addView  
```
public void addView(View view, ViewGroup.LayoutParams params,
        Display display, Window parentWindow) {
    synchronized (mLock) {
        //  获得 ViewRootImpl 对象 root
        root = new ViewRootImpl(view.getContext(), display);
        try {
            //  将 DecorView 关联到 ViewRootImpl 上;
            root.setView(view, wparams, panelParentView);
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
```
ViewRootImpl#setView  
```
public void setView(View view, WindowManager.LayoutParams attrs, View panelParentView) {
    synchronized (this) {
        if (mView == null) {
            //  将顶层视图DecorView赋值给全局的mView
            mView = view;
            //  标记已添加DecorView
            mAdded = true;  
            // 请求布局
            requestLayout();
        }
    }
}
```
requestLayout 的原理  
[链接](/Android/basic/view_window/invalidate_requestLayout.md)  
### 参考  
https://www.jianshu.com/p/3872219cc07a  
http://blog.csdn.net/huachao1001/article/details/51866287  
http://liuwangshu.cn/framework/wm/1-windowmanager.html  
http://liuwangshu.cn/framework/wm/2-window-property.html  
http://liuwangshu.cn/framework/wm/3-add-window.html  
http://liuwangshu.cn/framework/wms/1-wms-produce.html  
http://liuwangshu.cn/framework/wms/2-wms-member.html  
http://liuwangshu.cn/framework/wms/3-wms-remove.html  
https://blog.csdn.net/AndrExpert/article/details/81349343  
https://blog.csdn.net/feiduclear_up/article/details/46711921  

