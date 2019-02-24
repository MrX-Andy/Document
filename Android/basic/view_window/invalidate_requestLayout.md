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

#### requestLayout   
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
把 doTraversal 封装到一个 mTraversalRunnable 接口中, 再把 mTraversalRunnable 添加到 Choreographer 的任务队列中;  
实际上 doTraversal 直接调用 performTraversals 遍历整个 View 树; 
回调所有子 View 的 onMeasure, onLayout, onDraw 方法;  

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

#### invalidate  
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


#### 总结  
FrameDisplayEventReceiver 继承与 DisplayEventReceiver, 接收底层的 vSync 信号开始处理UI过程;  
vSync 信号由 SurfaceFlinger 实现并定时发送;  
FrameDisplayEventReceiver 收到信号后, 调用 onVsync 方法, 通过 handler 消息发送到主线程处理, 这个消息主要内容就是run方法里面的 doFrame 了;  
 
 
屏幕刷新机制#总结  
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

双缓冲技术一直贯穿整个 Android 系统, 因为实际上帧的数据, 就是保存在两个缓冲区中, A 缓冲用来显示当前帧, 那么 B 缓冲就用来缓存下一帧的数据,   
同理, B显示时, A就用来缓冲, 这样就可以做到一边显示一边处理下一帧的数据;  
但是, 由于某些原因, 比如我们应用代码上逻辑处理过于复杂, 或者布局过于复杂, 出现过度绘制(Overdraw), UI线程的复杂运算, 频繁的GC等,   
导致下一帧绘制的时间超过了16ms, 那么问题就来了, 用户很明显感知到了卡顿的出现, 也就是所谓的丢帧情况;  
当 Display 显示第 0 帧数据时, 此时 CPU 和 GPU 已经开始渲染第 1 帧画面, 并将数据缓存在缓冲 B 中, 但是由于某些原因, 导致系统处理该帧数据耗时过长或者未能及时处理该帧数据;  
当 vSync 信号来时, Display 向 B 缓冲要数据, 这时候 缓冲 B 的数据还没准备好, B缓冲区这时候是被锁定的, Display 表示你没准备好, 只能继续显示之前缓冲 A 的那一帧,   
此时缓冲 A 的数据也不能被清空和交换数据, 这种情况就是所谓的"丢帧", 也被称作"废帧";  
当第 1 帧数据(即缓冲 B 数据)准备完成后, 它并不会马上被显示, 而是要等待下一个 vSync, Display 刷新后, 这时用户才看到画面的更新;  
丢帧给用户感觉就是卡顿了, 最严重的直接造成ANR;  
既然丢帧的情况不可避免, Android 团队从未放弃对这块的优化处理, 于是便出现了Triple Buffer(三缓冲机制);  
在三倍缓冲机制中, 系统这个时候会创建一个缓冲 C, 用来缓冲下一帧的数据, 也就是说在显示完缓冲B中那一帧后, 下一帧就是显示缓冲 C 中的了,   
这样虽然还是不能避免会出现卡顿的情况, 但是 Android 系统还是尽力去弥补这种缺陷, 最终尽可能给用平滑的动效体验;  

### api  
获取手机屏幕的刷新频率  
```
Display display = getWindowManager().getDefaultDisplay();
float refreshRate = display.getRefreshRate();
```
onLayout 的坐标值, 是在他的直接父窗体的坐标系;  
android.view.IWindow  
android.view.IWindowSession  
android.view.Surface  
android.view.SurfaceSession  
android.view.SurfaceControl  
android.view.WindowManager  
android.view.WindowManagerGlobal  
android.view.WindowManagerImpl  
com.android.server.wm.WindowManagerService  


### ViewRootImpl#performTraversals  
API=18:  onMeasure-onMeasure-onLayout-onDraw  

当前Activity的所有子View的 onMeasure-onLayout-onDraw, 都是 performTraversals 直接或者间接触发的;  
1.. 预测量阶段, 调用 measureHierarchy, 对 View 树进行第一次测量, 测量的结果可以通过mView.getMeasureWidth-Height 得到;  
在此阶段, 计算出 view 树要展示的内容的尺寸-size, 即期望的窗口的尺寸,  
在此阶段, 所有 view-ViewGroup 的 onMeasure 方法, 将会沿着 view 树一次被回调;  
2.. 布局窗口阶段, 根据预测结果, 在 relayoutWindow 方法中, 通过 IWindowSession.relayout 方法, 向 WMS 请求调整窗口的尺寸等属性, 这将引发 WMS 对窗口进行重新布局, 并将布局结果返回给 ViewRootImpl.  
以下4大条件满足其一即进入布局窗口阶段:  
    a.. 第一次遍历时, 此时窗口尚未进行窗口布局, 没有有效的 Surface 进行内容绘制, 因此必须进行窗口布局;  
    b.. windowShouldResize, view 树的测量结果与窗口的当前尺寸有差异, 需要通过布局窗口阶段向 WMS 提出修改窗口尺寸的请求;  
    c.. insetsChanged, 表示 WMS 单方面改变了窗口的 ContentInsets, 这种情况一般发生在 SystemUI 的可见性发生了变化或输入法窗口弹出或关闭的情况下,  
    严格说, 此情况不需要重新进行窗口布局, 只不过当 ContentInsets 发生变化时, 需要执行一段渐变动画, 使窗口的内容过渡到新的 ContentInsets下, 而这段动画启动动作发生在窗口布局阶段;  
    d.. 'params != null', 在进入 performTraversals()方法时, params 变量被设置为 null, 当窗口的使用者通过 WindowManager.updateViewLayout() 函数修改窗口的 LayoutParams,  
    或者在预测量阶段通过 collectViewAttributes() 函数收集到的控件属性使得 LayoutParams 发生变化时, params 将被设置到新的 LayoutParams,  
    此时需要将该值通过窗口布局更新到 WMS 中使其对窗口依照新的属性进行重新布局;  
ViewRootImpl 使用 relayoutWindow()进行窗口布局;  

3.. 最终测量阶段, 预测的结果是 View 树, 所期望的窗口尺寸, 然而由于在 WMS 中影响窗口布局的因素有很多, WMS 不一定会将窗口布局到 View 树所要求的尺寸;  
因此在这个阶段, performTraversals 将用窗口的时机尺寸, 调用 performMeasure, 对 view 树进行最终测量;  
在此阶段, 所有 view-ViewGroup 的 onMeasure 方法, 将会沿着 view 树一次被回调;  
4.. 布局 View 树阶段, 完成最终测量之后, 调用 performLayout, 对 View 树进行布局, 拿到 view 最终的尺寸, 摆放 view 的位置, 在此阶段, view 的 onLayout 会被回调;  
5.. 绘制阶段, 这个 performTraversals 的最终阶段, 调用 performDraw 对 View 树进行绘制, 在此阶段 view#onDraw 会被执行;  

子控件的测量结果影响父控件的测量结果, 因此是先测量子控件;  
父控件的布局结果影响子控件的布局结果, 例如位置, 所以先摆放父控件;  

调用 requestLayout:    
预测量阶段, 与测量结果与窗口尺寸是否一致?  
    不一致: 布局窗口阶段-最终测量阶段-布局控件树阶段-绘制阶段;  
    一致: 布局控件树阶段-绘制阶段;  

```
private void performTraversals() {
    // mView就是DecorView根布局
    final View host = mView;
    if (host == null || !mAdded)
        return;
    //  是否正在遍历
    mIsInTraversal = true;
    //  是否马上绘制View
    mWillDrawSoon = true;
    boolean windowSizeMayChange = false;
    boolean newSurface = false;
    boolean surfaceChanged = false;
    WindowManager.LayoutParams lp = mWindowAttributes;
    //  DecorView所需要窗口的宽度和高度  
    int desiredWindowWidth;
    int desiredWindowHeight;

    final int viewVisibility = getHostVisibility();
    final boolean viewVisibilityChanged = !mFirst
            && (mViewVisibility != viewVisibility || mNewSurfaceNeeded
            // Also check for possible double visibility update, which will make current
            // viewVisibility value equal to mViewVisibility and we may miss it.
            || mAppVisibilityChanged);
    mAppVisibilityChanged = false;
    final boolean viewUserVisibilityChanged = !mFirst &&
            ((mViewVisibility == View.VISIBLE) != (viewVisibility == View.VISIBLE));

    WindowManager.LayoutParams params = null;
    if (mWindowAttributesChanged) {
        mWindowAttributesChanged = false;
        surfaceChanged = true;
        params = lp;
    }
    CompatibilityInfo compatibilityInfo =
            mDisplay.getDisplayAdjustments().getCompatibilityInfo();
    if (compatibilityInfo.supportsScreen() == mLastInCompatMode) {
        params = lp;
        mFullRedrawNeeded = true;
        mLayoutRequested = true;
        if (mLastInCompatMode) {
            params.privateFlags &= ~WindowManager.LayoutParams.PRIVATE_FLAG_COMPATIBLE_WINDOW;
            mLastInCompatMode = false;
        } else {
            params.privateFlags |= WindowManager.LayoutParams.PRIVATE_FLAG_COMPATIBLE_WINDOW;
            mLastInCompatMode = true;
        }
    }
    mWindowAttributesChangesFlag = 0;
    Rect frame = mWinFrame;
    //  在构造方法中mFirst已经设置为true, 表示是否是第一次绘制 DecorView
    if (mFirst) {
        mFullRedrawNeeded = true;
        mLayoutRequested = true;

        final Configuration config = mContext.getResources().getConfiguration();
        if (shouldUseDisplaySize(lp)) {
            Point size = new Point();
            mDisplay.getRealSize(size);
            desiredWindowWidth = size.x;
            desiredWindowHeight = size.y;
        } else {
            desiredWindowWidth = mWinFrame.width();
            desiredWindowHeight = mWinFrame.height();
        }
        mAttachInfo.mUse32BitDrawingCache = true;
        mAttachInfo.mHasWindowFocus = false;
        mAttachInfo.mWindowVisibility = viewVisibility;
        mAttachInfo.mRecomputeGlobalAttributes = false;
        mLastConfigurationFromResources.setTo(config);
        mLastSystemUiVisibility = mAttachInfo.mSystemUiVisibility;
        // Set the layout direction if it has not been set before (inherit is the default)
        if (mViewLayoutDirectionInitial == View.LAYOUT_DIRECTION_INHERIT) {
            host.setLayoutDirection(config.getLayoutDirection());
        }
        host.dispatchAttachedToWindow(mAttachInfo, 0);
        mAttachInfo.mTreeObserver.dispatchOnWindowAttachedChange(true);
        dispatchApplyInsets(host);
    } else {
        desiredWindowWidth = frame.width();
        desiredWindowHeight = frame.height();
        if (desiredWindowWidth != mWidth || desiredWindowHeight != mHeight) {
            if (DEBUG_ORIENTATION) Log.v(mTag, "View " + host + " resized to: " + frame);
            mFullRedrawNeeded = true;
            mLayoutRequested = true;
            windowSizeMayChange = true;
        }
    }

    if (viewVisibilityChanged) {
        mAttachInfo.mWindowVisibility = viewVisibility;
        host.dispatchWindowVisibilityChanged(viewVisibility);
        if (viewUserVisibilityChanged) {
            host.dispatchVisibilityAggregated(viewVisibility == View.VISIBLE);
        }
        if (viewVisibility != View.VISIBLE || mNewSurfaceNeeded) {
            endDragResizing();
            destroyHardwareResources();
        }
        if (viewVisibility == View.GONE) {
            // After making a window gone, we will count it as being
            // shown for the first time the next time it gets focus.
            mHasHadWindowFocus = false;
        }
    }

    // Non-visible windows can't hold accessibility focus.
    if (mAttachInfo.mWindowVisibility != View.VISIBLE) {
        host.clearAccessibilityFocus();
    }

    // Execute enqueued actions on every traversal in case a detached view enqueued an action
    getRunQueue().executeActions(mAttachInfo.mHandler);

    boolean insetsChanged = false;

    boolean layoutRequested = mLayoutRequested && (!mStopped || mReportNextDraw);
    if (layoutRequested) {

        final Resources res = mView.getContext().getResources();

        if (mFirst) {
            mAttachInfo.mInTouchMode = !mAddedTouchMode;
            ensureTouchModeLocally(mAddedTouchMode);
        } else {
            if (!mPendingOverscanInsets.equals(mAttachInfo.mOverscanInsets)) {
                insetsChanged = true;
            }
            if (!mPendingContentInsets.equals(mAttachInfo.mContentInsets)) {
                insetsChanged = true;
            }
            if (!mPendingStableInsets.equals(mAttachInfo.mStableInsets)) {
                insetsChanged = true;
            }
            if (!mPendingDisplayCutout.equals(mAttachInfo.mDisplayCutout)) {
                insetsChanged = true;
            }
            if (!mPendingVisibleInsets.equals(mAttachInfo.mVisibleInsets)) {
                mAttachInfo.mVisibleInsets.set(mPendingVisibleInsets);
            }
            if (!mPendingOutsets.equals(mAttachInfo.mOutsets)) {
                insetsChanged = true;
            }
            if (mPendingAlwaysConsumeNavBar != mAttachInfo.mAlwaysConsumeNavBar) {
                insetsChanged = true;
            }
            if (lp.width == ViewGroup.LayoutParams.WRAP_CONTENT
                    || lp.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                windowSizeMayChange = true;

                if (shouldUseDisplaySize(lp)) {
                    Point size = new Point();
                    mDisplay.getRealSize(size);
                    desiredWindowWidth = size.x;
                    desiredWindowHeight = size.y;
                } else {
                    Configuration config = res.getConfiguration();
                    desiredWindowWidth = dipToPx(config.screenWidthDp);
                    desiredWindowHeight = dipToPx(config.screenHeightDp);
                }
            }
        }

        // Ask host how big it wants to be
        windowSizeMayChange |= measureHierarchy(host, lp, res,
                desiredWindowWidth, desiredWindowHeight);
    }

    if (collectViewAttributes()) {
        params = lp;
    }
    if (mAttachInfo.mForceReportNewAttributes) {
        mAttachInfo.mForceReportNewAttributes = false;
        params = lp;
    }

    if (mFirst || mAttachInfo.mViewVisibilityChanged) {
        mAttachInfo.mViewVisibilityChanged = false;
        int resizeMode = mSoftInputMode &
                WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST;
        if (resizeMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED) {
            final int N = mAttachInfo.mScrollContainers.size();
            for (int i=0; i<N; i++) {
                if (mAttachInfo.mScrollContainers.get(i).isShown()) {
                    resizeMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
                }
            }
            if (resizeMode == 0) {
                resizeMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
            }
            if ((lp.softInputMode &
                    WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST) != resizeMode) {
                lp.softInputMode = (lp.softInputMode &
                        ~WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST) |
                        resizeMode;
                params = lp;
            }
        }
    }

    if (params != null) {
        if ((host.mPrivateFlags & View.PFLAG_REQUEST_TRANSPARENT_REGIONS) != 0) {
            if (!PixelFormat.formatHasAlpha(params.format)) {
                params.format = PixelFormat.TRANSLUCENT;
            }
        }
        mAttachInfo.mOverscanRequested = (params.flags
                & WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN) != 0;
    }

    if (mApplyInsetsRequested) {
        mApplyInsetsRequested = false;
        mLastOverscanRequested = mAttachInfo.mOverscanRequested;
        dispatchApplyInsets(host);
        if (mLayoutRequested) {
            // Short-circuit catching a new layout request here, so
            // we don't need to go through two layout passes when things
            // change due to fitting system windows, which can happen a lot.
            windowSizeMayChange |= measureHierarchy(host, lp,
                    mView.getContext().getResources(),
                    desiredWindowWidth, desiredWindowHeight);
        }
    }

    if (layoutRequested) {
        // Clear this now, so that if anything requests a layout in the
        // rest of this function we will catch it and re-run a full
        // layout pass.
        mLayoutRequested = false;
    }

    boolean windowShouldResize = layoutRequested && windowSizeMayChange
        && ((mWidth != host.getMeasuredWidth() || mHeight != host.getMeasuredHeight())
            || (lp.width == ViewGroup.LayoutParams.WRAP_CONTENT &&
                    frame.width() < desiredWindowWidth && frame.width() != mWidth)
            || (lp.height == ViewGroup.LayoutParams.WRAP_CONTENT &&
                    frame.height() < desiredWindowHeight && frame.height() != mHeight));
    windowShouldResize |= mDragResizing && mResizeMode == RESIZE_MODE_FREEFORM;
    windowShouldResize |= mActivityRelaunched;

    // Determine whether to compute insets.
    // If there are no inset listeners remaining then we may still need to compute
    // insets in case the old insets were non-empty and must be reset.
    final boolean computesInternalInsets =
            mAttachInfo.mTreeObserver.hasComputeInternalInsetsListeners()
            || mAttachInfo.mHasNonEmptyGivenInternalInsets;

    boolean insetsPending = false;
    int relayoutResult = 0;
    boolean updatedConfiguration = false;

    final int surfaceGenerationId = mSurface.getGenerationId();

    final boolean isViewVisible = viewVisibility == View.VISIBLE;
    final boolean windowRelayoutWasForced = mForceNextWindowRelayout;
    if (mFirst || windowShouldResize || insetsChanged ||
            viewVisibilityChanged || params != null || mForceNextWindowRelayout) {
        mForceNextWindowRelayout = false;

        if (isViewVisible) {
            insetsPending = computesInternalInsets && (mFirst || viewVisibilityChanged);
        }

        if (mSurfaceHolder != null) {
            mSurfaceHolder.mSurfaceLock.lock();
            mDrawingAllowed = true;
        }

        boolean hwInitialized = false;
        boolean contentInsetsChanged = false;
        boolean hadSurface = mSurface.isValid();

        try {
            if (mAttachInfo.mThreadedRenderer != null) {
                if (mAttachInfo.mThreadedRenderer.pauseSurface(mSurface)) {
                    mDirty.set(0, 0, mWidth, mHeight);
                }
                mChoreographer.mFrameInfo.addFlags(FrameInfo.FLAG_WINDOW_LAYOUT_CHANGED);
            }
            relayoutResult = relayoutWindow(params, viewVisibility, insetsPending);
            if (!mPendingMergedConfiguration.equals(mLastReportedMergedConfiguration)) {
                if (DEBUG_CONFIGURATION) Log.v(mTag, "Visible with new config: "
                        + mPendingMergedConfiguration.getMergedConfiguration());
                performConfigurationChange(mPendingMergedConfiguration, !mFirst,
                        INVALID_DISPLAY /* same display */);
                updatedConfiguration = true;
            }

            final boolean overscanInsetsChanged = !mPendingOverscanInsets.equals(
                    mAttachInfo.mOverscanInsets);
            contentInsetsChanged = !mPendingContentInsets.equals(
                    mAttachInfo.mContentInsets);
            final boolean visibleInsetsChanged = !mPendingVisibleInsets.equals(
                    mAttachInfo.mVisibleInsets);
            final boolean stableInsetsChanged = !mPendingStableInsets.equals(
                    mAttachInfo.mStableInsets);
            final boolean cutoutChanged = !mPendingDisplayCutout.equals(
                    mAttachInfo.mDisplayCutout);
            final boolean outsetsChanged = !mPendingOutsets.equals(mAttachInfo.mOutsets);
            final boolean surfaceSizeChanged = (relayoutResult
                    & WindowManagerGlobal.RELAYOUT_RES_SURFACE_RESIZED) != 0;
            surfaceChanged |= surfaceSizeChanged;
            final boolean alwaysConsumeNavBarChanged =
                    mPendingAlwaysConsumeNavBar != mAttachInfo.mAlwaysConsumeNavBar;
            if (contentInsetsChanged) {
                mAttachInfo.mContentInsets.set(mPendingContentInsets);
                if (DEBUG_LAYOUT) Log.v(mTag, "Content insets changing to: "
                        + mAttachInfo.mContentInsets);
            }
            if (overscanInsetsChanged) {
                mAttachInfo.mOverscanInsets.set(mPendingOverscanInsets);
                if (DEBUG_LAYOUT) Log.v(mTag, "Overscan insets changing to: "
                        + mAttachInfo.mOverscanInsets);
                // Need to relayout with content insets.
                contentInsetsChanged = true;
            }
            if (stableInsetsChanged) {
                mAttachInfo.mStableInsets.set(mPendingStableInsets);
                if (DEBUG_LAYOUT) Log.v(mTag, "Decor insets changing to: "
                        + mAttachInfo.mStableInsets);
                // Need to relayout with content insets.
                contentInsetsChanged = true;
            }
            if (cutoutChanged) {
                mAttachInfo.mDisplayCutout.set(mPendingDisplayCutout);
                if (DEBUG_LAYOUT) {
                    Log.v(mTag, "DisplayCutout changing to: " + mAttachInfo.mDisplayCutout);
                }
                // Need to relayout with content insets.
                contentInsetsChanged = true;
            }
            if (alwaysConsumeNavBarChanged) {
                mAttachInfo.mAlwaysConsumeNavBar = mPendingAlwaysConsumeNavBar;
                contentInsetsChanged = true;
            }
            if (contentInsetsChanged || mLastSystemUiVisibility !=
                    mAttachInfo.mSystemUiVisibility || mApplyInsetsRequested
                    || mLastOverscanRequested != mAttachInfo.mOverscanRequested
                    || outsetsChanged) {
                mLastSystemUiVisibility = mAttachInfo.mSystemUiVisibility;
                mLastOverscanRequested = mAttachInfo.mOverscanRequested;
                mAttachInfo.mOutsets.set(mPendingOutsets);
                mApplyInsetsRequested = false;
                dispatchApplyInsets(host);
            }
            if (visibleInsetsChanged) {
                mAttachInfo.mVisibleInsets.set(mPendingVisibleInsets);
                if (DEBUG_LAYOUT) Log.v(mTag, "Visible insets changing to: "
                        + mAttachInfo.mVisibleInsets);
            }

            if (!hadSurface) {
                if (mSurface.isValid()) {
                    newSurface = true;
                    mFullRedrawNeeded = true;
                    mPreviousTransparentRegion.setEmpty();

                    // Only initialize up-front if transparent regions are not
                    // requested, otherwise defer to see if the entire window
                    // will be transparent
                    if (mAttachInfo.mThreadedRenderer != null) {
                        try {
                            hwInitialized = mAttachInfo.mThreadedRenderer.initialize(
                                    mSurface);
                            if (hwInitialized && (host.mPrivateFlags
                                    & View.PFLAG_REQUEST_TRANSPARENT_REGIONS) == 0) {
                                mSurface.allocateBuffers();
                            }
                        } catch (OutOfResourcesException e) {
                            handleOutOfResourcesException(e);
                            return;
                        }
                    }
                }
            } else if (!mSurface.isValid()) {
                if (mLastScrolledFocus != null) {
                    mLastScrolledFocus.clear();
                }
                mScrollY = mCurScrollY = 0;
                if (mView instanceof RootViewSurfaceTaker) {
                    ((RootViewSurfaceTaker) mView).onRootViewScrollYChanged(mCurScrollY);
                }
                if (mScroller != null) {
                    mScroller.abortAnimation();
                }
                // Our surface is gone
                if (mAttachInfo.mThreadedRenderer != null &&
                        mAttachInfo.mThreadedRenderer.isEnabled()) {
                    mAttachInfo.mThreadedRenderer.destroy();
                }
            } else if ((surfaceGenerationId != mSurface.getGenerationId()
                    || surfaceSizeChanged || windowRelayoutWasForced)
                    && mSurfaceHolder == null
                    && mAttachInfo.mThreadedRenderer != null) {
                mFullRedrawNeeded = true;
                try {
                    mAttachInfo.mThreadedRenderer.updateSurface(mSurface);
                } catch (OutOfResourcesException e) {
                    handleOutOfResourcesException(e);
                    return;
                }
            }

            final boolean freeformResizing = (relayoutResult
                    & WindowManagerGlobal.RELAYOUT_RES_DRAG_RESIZING_FREEFORM) != 0;
            final boolean dockedResizing = (relayoutResult
                    & WindowManagerGlobal.RELAYOUT_RES_DRAG_RESIZING_DOCKED) != 0;
            final boolean dragResizing = freeformResizing || dockedResizing;
            if (mDragResizing != dragResizing) {
                if (dragResizing) {
                    mResizeMode = freeformResizing
                            ? RESIZE_MODE_FREEFORM
                            : RESIZE_MODE_DOCKED_DIVIDER;
                    // TODO: Need cutout?
                    startDragResizing(mPendingBackDropFrame,
                            mWinFrame.equals(mPendingBackDropFrame), mPendingVisibleInsets,
                            mPendingStableInsets, mResizeMode);
                } else {
                    // We shouldn't come here, but if we come we should end the resize.
                    endDragResizing();
                }
            }
            if (!mUseMTRenderer) {
                if (dragResizing) {
                    mCanvasOffsetX = mWinFrame.left;
                    mCanvasOffsetY = mWinFrame.top;
                } else {
                    mCanvasOffsetX = mCanvasOffsetY = 0;
                }
            }
        } catch (RemoteException e) {
        }

        mAttachInfo.mWindowLeft = frame.left;
        mAttachInfo.mWindowTop = frame.top;
        if (mWidth != frame.width() || mHeight != frame.height()) {
            mWidth = frame.width();
            mHeight = frame.height();
        }

        if (mSurfaceHolder != null) {
            // The app owns the surface; tell it about what is going on.
            if (mSurface.isValid()) {
                // XXX .copyFrom() doesn't work!
                //mSurfaceHolder.mSurface.copyFrom(mSurface);
                mSurfaceHolder.mSurface = mSurface;
            }
            mSurfaceHolder.setSurfaceFrameSize(mWidth, mHeight);
            mSurfaceHolder.mSurfaceLock.unlock();
            if (mSurface.isValid()) {
                if (!hadSurface) {
                    mSurfaceHolder.ungetCallbacks();

                    mIsCreating = true;
                    SurfaceHolder.Callback callbacks[] = mSurfaceHolder.getCallbacks();
                    if (callbacks != null) {
                        for (SurfaceHolder.Callback c : callbacks) {
                            c.surfaceCreated(mSurfaceHolder);
                        }
                    }
                    surfaceChanged = true;
                }
                if (surfaceChanged || surfaceGenerationId != mSurface.getGenerationId()) {
                    SurfaceHolder.Callback callbacks[] = mSurfaceHolder.getCallbacks();
                    if (callbacks != null) {
                        for (SurfaceHolder.Callback c : callbacks) {
                            c.surfaceChanged(mSurfaceHolder, lp.format,
                                    mWidth, mHeight);
                        }
                    }
                }
                mIsCreating = false;
            } else if (hadSurface) {
                mSurfaceHolder.ungetCallbacks();
                SurfaceHolder.Callback callbacks[] = mSurfaceHolder.getCallbacks();
                if (callbacks != null) {
                    for (SurfaceHolder.Callback c : callbacks) {
                        c.surfaceDestroyed(mSurfaceHolder);
                    }
                }
                mSurfaceHolder.mSurfaceLock.lock();
                try {
                    mSurfaceHolder.mSurface = new Surface();
                } finally {
                    mSurfaceHolder.mSurfaceLock.unlock();
                }
            }
        }

        final ThreadedRenderer threadedRenderer = mAttachInfo.mThreadedRenderer;
        if (threadedRenderer != null && threadedRenderer.isEnabled()) {
            if (hwInitialized
                    || mWidth != threadedRenderer.getWidth()
                    || mHeight != threadedRenderer.getHeight()
                    || mNeedsRendererSetup) {
                threadedRenderer.setup(mWidth, mHeight, mAttachInfo,
                        mWindowAttributes.surfaceInsets);
                mNeedsRendererSetup = false;
            }
        }

        if (!mStopped || mReportNextDraw) {
            boolean focusChangedDueToTouchMode = ensureTouchModeLocally(
                    (relayoutResult&WindowManagerGlobal.RELAYOUT_RES_IN_TOUCH_MODE) != 0);
            if (focusChangedDueToTouchMode || mWidth != host.getMeasuredWidth()
                    || mHeight != host.getMeasuredHeight() || contentInsetsChanged ||
                    updatedConfiguration) {
                //  获得 view 宽高的测量规格, mWidth 和 mHeight 表示窗口的宽高, lp.widthhe 和 lp.height 表示 DecorView 根布局宽和高  
                int childWidthMeasureSpec = getRootMeasureSpec(mWidth, lp.width);
                int childHeightMeasureSpec = getRootMeasureSpec(mHeight, lp.height);
                //  执行测量操作  
                performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
                int width = host.getMeasuredWidth();
                int height = host.getMeasuredHeight();
                boolean measureAgain = false;

                if (lp.horizontalWeight > 0.0f) {
                    width += (int) ((mWidth - width) * lp.horizontalWeight);
                    childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width,
                            MeasureSpec.EXACTLY);
                    measureAgain = true;
                }
                if (lp.verticalWeight > 0.0f) {
                    height += (int) ((mHeight - height) * lp.verticalWeight);
                    childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                            MeasureSpec.EXACTLY);
                    measureAgain = true;
                }

                if (measureAgain) {
                    //  执行测量操作
                    performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
                }
                layoutRequested = true;
            }
        }
    } else {
        maybeHandleWindowMove(frame);
    }

    final boolean didLayout = layoutRequested && (!mStopped || mReportNextDraw);
    boolean triggerGlobalLayoutListener = didLayout
            || mAttachInfo.mRecomputeGlobalAttributes;
    if (didLayout) {
        //  执行布局操作
        performLayout(lp, mWidth, mHeight);
        if ((host.mPrivateFlags & View.PFLAG_REQUEST_TRANSPARENT_REGIONS) != 0) {
            host.getLocationInWindow(mTmpLocation);
            mTransparentRegion.set(mTmpLocation[0], mTmpLocation[1],
                    mTmpLocation[0] + host.mRight - host.mLeft,
                    mTmpLocation[1] + host.mBottom - host.mTop);

            host.gatherTransparentRegion(mTransparentRegion);
            if (mTranslator != null) {
                mTranslator.translateRegionInWindowToScreen(mTransparentRegion);
            }

            if (!mTransparentRegion.equals(mPreviousTransparentRegion)) {
                mPreviousTransparentRegion.set(mTransparentRegion);
                mFullRedrawNeeded = true;
                try {
                    mWindowSession.setTransparentRegion(mWindow, mTransparentRegion);
                } catch (RemoteException e) {
                }
            }
        }
    }

    if (triggerGlobalLayoutListener) {
        mAttachInfo.mRecomputeGlobalAttributes = false;
        mAttachInfo.mTreeObserver.dispatchOnGlobalLayout();
    }

    if (computesInternalInsets) {
        final ViewTreeObserver.InternalInsetsInfo insets = mAttachInfo.mGivenInternalInsets;
        insets.reset();
        mAttachInfo.mTreeObserver.dispatchOnComputeInternalInsets(insets);
        mAttachInfo.mHasNonEmptyGivenInternalInsets = !insets.isEmpty();
        if (insetsPending || !mLastGivenInsets.equals(insets)) {
            mLastGivenInsets.set(insets);
            final Rect contentInsets;
            final Rect visibleInsets;
            final Region touchableRegion;
            if (mTranslator != null) {
                contentInsets = mTranslator.getTranslatedContentInsets(insets.contentInsets);
                visibleInsets = mTranslator.getTranslatedVisibleInsets(insets.visibleInsets);
                touchableRegion = mTranslator.getTranslatedTouchableArea(insets.touchableRegion);
            } else {
                contentInsets = insets.contentInsets;
                visibleInsets = insets.visibleInsets;
                touchableRegion = insets.touchableRegion;
            }

            try {
                mWindowSession.setInsets(mWindow, insets.mTouchableInsets,
                        contentInsets, visibleInsets, touchableRegion);
            } catch (RemoteException e) {
            }
        }
    }

    if (mFirst) {
        if (sAlwaysAssignFocus || !isInTouchMode()) {
            if (mView != null) {
                if (!mView.hasFocus()) {
                    mView.restoreDefaultFocus();
                    if (DEBUG_INPUT_RESIZE) {
                        Log.v(mTag, "First: requested focused view=" + mView.findFocus());
                    }
                } else {
                    if (DEBUG_INPUT_RESIZE) {
                        Log.v(mTag, "First: existing focused view=" + mView.findFocus());
                    }
                }
            }
        } else {
            View focused = mView.findFocus();
            if (focused instanceof ViewGroup
                    && ((ViewGroup) focused).getDescendantFocusability()
                            == ViewGroup.FOCUS_AFTER_DESCENDANTS) {
                focused.restoreDefaultFocus();
            }
        }
    }

    final boolean changedVisibility = (viewVisibilityChanged || mFirst) && isViewVisible;
    final boolean hasWindowFocus = mAttachInfo.mHasWindowFocus && isViewVisible;
    final boolean regainedFocus = hasWindowFocus && mLostWindowFocus;
    if (regainedFocus) {
        mLostWindowFocus = false;
    } else if (!hasWindowFocus && mHadWindowFocus) {
        mLostWindowFocus = true;
    }

    if (changedVisibility || regainedFocus) {
        // Toasts are presented as notifications - don't present them as windows as well
        boolean isToast = (mWindowAttributes == null) ? false
                : (mWindowAttributes.type == WindowManager.LayoutParams.TYPE_TOAST);
        if (!isToast) {
            host.sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
        }
    }

    mFirst = false;
    mWillDrawSoon = false;
    mNewSurfaceNeeded = false;
    mActivityRelaunched = false;
    mViewVisibility = viewVisibility;
    mHadWindowFocus = hasWindowFocus;

    if (hasWindowFocus && !isInLocalFocusMode()) {
        final boolean imTarget = WindowManager.LayoutParams
                .mayUseInputMethod(mWindowAttributes.flags);
        if (imTarget != mLastWasImTarget) {
            mLastWasImTarget = imTarget;
            InputMethodManager imm = InputMethodManager.peekInstance();
            if (imm != null && imTarget) {
                imm.onPreWindowFocus(mView, hasWindowFocus);
                imm.onPostWindowFocus(mView, mView.findFocus(),
                        mWindowAttributes.softInputMode,
                        !mHasHadWindowFocus, mWindowAttributes.flags);
            }
        }
    }

    // Remember if we must report the next draw.
    if ((relayoutResult & WindowManagerGlobal.RELAYOUT_RES_FIRST_TIME) != 0) {
        reportNextDraw();
    }

    boolean cancelDraw = mAttachInfo.mTreeObserver.dispatchOnPreDraw() || !isViewVisible;

    if (!cancelDraw && !newSurface) {
        if (mPendingTransitions != null && mPendingTransitions.size() > 0) {
            for (int i = 0; i < mPendingTransitions.size(); ++i) {
                mPendingTransitions.get(i).startChangingAnimations();
            }
            mPendingTransitions.clear();
        }
        //  执行绘制操作
        performDraw();
    } else {
        if (isViewVisible) {
            scheduleTraversals();
        } else if (mPendingTransitions != null && mPendingTransitions.size() > 0) {
            for (int i = 0; i < mPendingTransitions.size(); ++i) {
                mPendingTransitions.get(i).endChangingAnimations();
            }
            mPendingTransitions.clear();
        }
    }

    mIsInTraversal = false;
}
```

### ViewRootImpl#getRootMeasureSpec  
DecorView 根布局宽和高都是 MATCH_PARENT, 因此 DecorView 根布局的测量模式就是 MeasureSpec.EXACTLY, 测量大小一般都是整个屏幕大小,  
所以一般我们的 Activity 窗口都是全屏的;  
```
private static int getRootMeasureSpec(int windowSize, int rootDimension) {
    int measureSpec;
    switch (rootDimension) {

    case ViewGroup.LayoutParams.MATCH_PARENT:
        // Window can't resize. Force root view to be windowSize.
        //  window 不可以重置大小, 强制 root view 设置为 windowSize  
        measureSpec = MeasureSpec.makeMeasureSpec(windowSize, MeasureSpec.EXACTLY);
        break;
    case ViewGroup.LayoutParams.WRAP_CONTENT:
        // Window can resize. Set max size for root view.
        //  window 可以重置大小, 给 rootView 设置 max size  
        measureSpec = MeasureSpec.makeMeasureSpec(windowSize, MeasureSpec.AT_MOST);
        break;
    default:
        // Window wants to be an exact size. Force root view to be that size.
        //  window 希望是一个 确认的 size, 强制 root view 被设置成那个 size  
        measureSpec = MeasureSpec.makeMeasureSpec(rootDimension, MeasureSpec.EXACTLY);
        break;
    }
    return measureSpec;
}
```
### ViewRootImpl#measureHierarchy  
```
private boolean measureHierarchy(final View host, final WindowManager.LayoutParams lp,
		final Resources res, final int desiredWindowWidth, final int desiredWindowHeight) {
	int childWidthMeasureSpec;
	int childHeightMeasureSpec;
	//  表示测量结果是否可能导致窗口的尺寸发生变化
	boolean windowSizeMayChange = false;
	//  goodMeasure表示了测量是否能满足 View 树充分显示内容的要求
	boolean goodMeasure = false;
	//  测量协商仅发生在 LayoutParams.width 被指定为 WRAP_CONTENT 的情况下
	if (lp.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
		//  第一次协商, measureHierarchy() 使用它最期望的宽度限制进行测量。
		//  这一宽度限制定义为一个系统资源。
		//  可以在frameworks/base/core/res/res/values/config.xml找到它的定义
		final DisplayMetrics packageMetrics = res.getDisplayMetrics();
		res.getValue(com.android.internal.R.dimen.config_prefDialogWidth, mTmpValue, true);
		// 宽度限制被存放在 baseSize中
		int baseSize = 0;
		if (mTmpValue.type == TypedValue.TYPE_DIMENSION) {
			baseSize = (int)mTmpValue.getDimension(packageMetrics);
		}
		if (baseSize != 0 && desiredWindowWidth > baseSize) {
			childWidthMeasureSpec = getRootMeasureSpec(baseSize, lp.width);
			childHeightMeasureSpec = getRootMeasureSpec(desiredWindowHeight, lp.height);
			//  第一次测量, 调用performMeasure()进行测量  
			performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
			
			//  View 树的测量结果可以通过 mView 的 getmeasuredWidthAndState()方法获取;
			//  View 树对这个测量结果不满意, 则会在返回值中添加 MEASURED_STATE_TOO_SMALL 标记位
			if ((host.getMeasuredWidthAndState()&View.MEASURED_STATE_TOO_SMALL) == 0) {
				goodMeasure = true;  //  控件树对测量结果满意, 测量完成
			} else {
			    //  第二次协商, 上次的测量结果表明 View 树认为 measureHierarchy() 给予的宽度太小, 在此
			    //  在此适当地放宽对宽度的限制, 使用最大宽度与期望宽度的中间值作为宽度限制  
				baseSize = (baseSize+desiredWindowWidth)/2;
				childWidthMeasureSpec = getRootMeasureSpec(baseSize, lp.width);
			    //  第二次测量
				performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
			    //  再次检查控件树是否满足此次测量
				if ((host.getMeasuredWidthAndState()&View.MEASURED_STATE_TOO_SMALL) == 0) {
				   //   控件树对测量结果满意, 测量完成
					goodMeasure = true;
				}
			}
		}
	}
	
	if (!goodMeasure) {
	    //  最终测量, 当 View 树对上述两次协商的结果都不满意时, measureHierarchy()放弃所有限制
	    //  做最终测量, 这一次将不再检查控件树是否满意了, 因为即便其不满意, measurehierarchy()也没有更多的空间供其使用了
		childWidthMeasureSpec = getRootMeasureSpec(desiredWindowWidth, lp.width);
		childHeightMeasureSpec = getRootMeasureSpec(desiredWindowHeight, lp.height);
		performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
	    //  如果测量结果与 ViewRootImpl 中当前的窗口尺寸不一致, 则表明随后可能有必要进行窗口尺寸的调整;  
		if (mWidth != host.getMeasuredWidth() || mHeight != host.getMeasuredHeight()) {
			windowSizeMayChange = true;
		}
	}

	 // 返回窗口尺寸是否可能需要发生变化
	return windowSizeMayChange;
}
```
### View#measure  
首先, 调用 View.measure()方法时, View 并不是立即就去测量, 而是先判断一下是否有必要进行测量操作,  
如果不是强制测量或者 MeasureSpec 与上次的 MeasureSpec 相同的时候, 那么 View 就不需要重新测量了.

如果不满足上面条件, View 就考虑去做测量工作了, 但在测量之前, View还想偷懒, 如果能在缓存中找到上次的测量结果, 那直接从缓存中获取就可以了.  
它会以 MeasureSpec 的值作为 key, 去成员变量 mMeasureCache 中查找是否缓存过对应 key 的测量结果, 如果能找到, 那么就简单调用一下 setMeasuredDimensionRaw 方法,  
将从缓存中读到的测量结果, 保存到成员变量 mMeasuredWidth 和 mMeasuredHeight 中;  

如果不能从 mMeasureCache 中读到缓存过的测量结果, 则调用 onMeasure()方法去完成实际的测量工作, 并且将尺寸限制条件 widthMeasureSpec 和 heightMeasureSpec 传递给 onMeasure()方法;  

View 有一个成员变量 mPrivateFlags, 用以保存 View 的各种状态位, 在测量开始前, 会将其设置为未测量状态, 在测量完成后会将其设置为已测量状态;  

```
public final void measure(int widthMeasureSpec, int heightMeasureSpec) {
    //首先判断当前View的layoutMode是不是特例LAYOUT_MODE_OPTICAL_BOUNDS
	boolean optical = isLayoutModeOptical(this);
	if (optical != isLayoutModeOptical(mParent)) {
	    //LAYOUT_MODE_OPTICAL_BOUNDS是特例情况, 比较少见,不分析
		Insets insets = getOpticalInsets();
		int oWidth  = insets.left + insets.right;
		int oHeight = insets.top  + insets.bottom;
		widthMeasureSpec  = MeasureSpec.adjust(widthMeasureSpec,  optical ? -oWidth  : oWidth);
		heightMeasureSpec = MeasureSpec.adjust(heightMeasureSpec, optical ? -oHeight : oHeight);
	}

	//  根据 widthMeasureSpec和 heightMeasureSpec 计算 key, , 缓存我们测量得到的结果
	long key = (long) widthMeasureSpec << 32 | (long) heightMeasureSpec & 0xffffffffL;
	//  mMeasureCache 是 LongSparseLongArray 类型的 成员变量, 
	//  其缓存着 View 在不同 widthMeasureSpec, heightMeasureSpec 下测量过的结果
	if (mMeasureCache == null) mMeasureCache = new LongSparseLongArray(2);

	//  mOldWidthMeasureSpec 和 mOldHeightMeasureSpec 分别表示上次对 View 进行测量时的 widthMeasureSpec 和 heightMeasureSpec
	//  执行 View 的 measure 方法时, View 总是先检查一下, 是不是需要做复杂的测量工作
	//  mPrivateFlags 是一个 int 类型的值, 其记录了 View 的各种状态位
	//  如果(mPrivateFlags & PFLAG_FORCE_LAYOUT) == PFLAG_FORCE_LAYOUT, 
	//  那么表示当前 View 需要强制进行 layout(比如执行了View的forceLayout方法), 所以这种情况下要尝试进行测量
	//  如果新传入的widthMeasureSpec/heightMeasureSpec与上次测量时的mOldWidthMeasureSpec/mOldHeightMeasureSpec不等, 
	//  那么也就是说该 View 的父控件对该 View 的尺寸的限制情况有变化, 这种情况下要尝试进行测量
	if ((mPrivateFlags & PFLAG_FORCE_LAYOUT) == PFLAG_FORCE_LAYOUT ||
			widthMeasureSpec != mOldWidthMeasureSpec ||
			heightMeasureSpec != mOldHeightMeasureSpec) {

		//  通过按位操作, 重置 View 的状态标志 mPrivateFlags, 将其标记为未测量状态
		mPrivateFlags &= ~PFLAG_MEASURED_DIMENSION_SET;
		
		//  对阿拉伯语, 希伯来语等从右到左书写, 布局的语言进行特殊处理
		resolveRtlPropertiesIfNeeded();

	    //  在 View 真正进行测量之前, View还想进一步确认能不能从已有的缓存 mMeasureCache 中读取缓存过的测量结果
	    //  如果是强制 layout 导致的测量, 那么将 cacheIndex 设置为-1, 即不从缓存中读取测量结果
	    //  如果不是强制 layout 导致的测量, 那么我们就用上面根据 measureSpec 计算出来的 key 作为缓存索引 cacheIndex,
	    //  这时候有可能找到相应的值, 找到就返回对应索引, 也可能找不到, 找不到就返回 -1
		int cacheIndex = (mPrivateFlags & PFLAG_FORCE_LAYOUT) == PFLAG_FORCE_LAYOUT ? -1 :
				mMeasureCache.indexOfKey(key);

		if (cacheIndex < 0 || sIgnoreMeasureCache) {
		    //  在缓存中找不到相应的值, 或者需要忽略缓存结果的时候, 重新测量一次
		    //  此处调用 onMeasure方法, 并把尺寸限制条件 widthMeasureSpec 和 heightMeasureSpec 传入进去
		    //  onMeasure 方法中将会进行实际的测量工作, 并把测量的结果保存到成员变量中
			onMeasure(widthMeasureSpec, heightMeasureSpec);
		    //  onMeasure 执行完后, 通过位操作, 重置 View 的状态 mPrivateFlags, 将其标记为在 layout 之前不必再进行测量的状态
			mPrivateFlags3 &= ~PFLAG3_MEASURE_NEEDED_BEFORE_LAYOUT;
		} else {
		    //  如果运行到此处, 那么表示当前的条件允许 View 从缓存成员变量 mMeasureCache 中读取测量过的结果
		    //  用上面得到的 cacheIndex 从缓存 mMeasureCache 中取出值, 不必在调用 onMeasure 方法进行测量了
			long value = mMeasureCache.valueAt(cacheIndex);
		    //  一旦我们从缓存中读到值, 我们就可以调用 setMeasuredDimensionRaw 方法将当前测量的结果保存到成员变量中
			setMeasuredDimensionRaw((int) (value >> 32), (int) value);
			mPrivateFlags3 |= PFLAG3_MEASURE_NEEDED_BEFORE_LAYOUT;
		}
        //  如果我们自定义的 View 重写了 onMeasure 方法, 但是没有调用 setMeasuredDimension()方法,
        //  那么此处就会抛出异常, 提醒开发者在 onMeasure方法中调用 setMeasuredDimension()方法,  
        // setMeasuredDimension()方法中会将 mPrivateFlags 设置为 PFLAG_MEASURED_DIMENSION_SET 状态, 即已测量状态 ,  
        //  此处就检查 mPrivateFlags 是否含有 PFLAG_MEASURED_DIMENSION_SET 状态  
		if ((mPrivateFlags & PFLAG_MEASURED_DIMENSION_SET) != PFLAG_MEASURED_DIMENSION_SET) {
			throw new IllegalStateException("View with id " + getId() + ": "
					+ getClass().getName() + "#onMeasure() did not set the"
					+ " measured dimension by calling"
					+ " setMeasuredDimension()");
		}
	    //  到了这里, View 已经测量完了并且将测量的结果保存在 View 的 mMeasuredWidth 和 mMeasuredHeight 中,  将标志位置为可以 layout 的状态
		mPrivateFlags |= PFLAG_LAYOUT_REQUIRED;
	}
    //  mOldWidthMeasureSpec 和 mOldHeightMeasureSpec 保存着最近一次测量时的 MeasureSpec,  
    //  在测量完成后将这次新传入的 MeasureSpec 赋值给它们
	mOldWidthMeasureSpec = widthMeasureSpec;
	mOldHeightMeasureSpec = heightMeasureSpec;
    //  最后用上面计算出的 key, 把测量结果存入 mMeasureCache 中,  
    //  这样就实现了对本次测量结果的缓存, 以便在下次 measure 方法执行的时候, 有可能将其从中直接读出, 
    // 从而省去实际测量的步骤
	mMeasureCache.put(key, ((long) mMeasuredWidth) << 32 |
			(long) mMeasuredHeight & 0xffffffffL); // suppress sign extension
}
```
### View#resolveSizeAndState  
```
public static int resolveSizeAndState(int size, int measureSpec, int childMeasuredState) {
	final int specMode = MeasureSpec.getMode(measureSpec);
	final int specSize = MeasureSpec.getSize(measureSpec);
	final int result;
	switch (specMode) {
		case MeasureSpec.AT_MOST:
		    //当 specMode为 AT_MOST时, 这时候 specSize 是父 ViewGroup 给该 View 指定的最大尺寸  
			if (specSize < size) {
			    //  如果父 ViewGroup 指定的最大尺寸比 View 想要的尺寸还要小, 这时候会使用 MEASURED_STATE_TOO_SMALL 这个掩码,  
			    //  向已经测量出来的尺寸 specSize 加入尺寸太小的标志, 然后将这个带有标志的 specSize 返回  
				//  父 ViewGroup 通过该标志, 就可以知道分配给 View的空间太小了, 在窗口协商测量的时候会根据这个标志位来做窗口大小的决策;  
				result = specSize | MEASURED_STATE_TOO_SMALL;
			} else {
			    // 如果父控件指定最大尺寸 大于等于 子 View 想要的尺寸小, 这时候就放弃之前已经给 View 赋值的 specSize, 用 View 自己想要的尺寸就可以了;
				result = size;
			}
			break;
		case MeasureSpec.EXACTLY:
			result = specSize;
			break;
		case MeasureSpec.UNSPECIFIED:
		default:
			result = size;
	}
	return result | (childMeasuredState & MEASURED_STATE_MASK);
}
```
### ViewGroup#getChildMeasureSpec  
```
public static int getChildMeasureSpec(int spec, int padding, int childDimension) {
	//  取得 SpecMode 和 SpecSize
	int specMode = MeasureSpec.getMode(spec);
	int specSize = MeasureSpec.getSize(spec);
	//  子元素的可用大小为父容器的尺寸减去padding
	int size = Math.max(0, specSize - padding);

	int resultSize = 0;
	int resultMode = 0;

	switch (specMode) {
	//  父容器是 EXACTLY 模式, 表明父容器本身的尺寸已经是确定的了
	case MeasureSpec.EXACTLY:
		//  childDimension 是子元素的属性值, 如果大于等于0, 就说明该子元素是指定宽/高尺寸的, 比如20dp
		// 因为 MATCH_PARENT 的值为 -1, WRAP_CONTENT 的值为 -2, 都是小于0的, 所以大于等于0肯定是指定固定尺寸的。
		//  既然子元素都指定固定大小了, 就直接取指定的尺寸,
		//  然后将子元素的测量模式定为EXACTLY模式, 表明子元素的尺寸也确定了
		if (childDimension >= 0) {
			resultSize = childDimension;
			resultMode = MeasureSpec.EXACTLY;
		} else if (childDimension == LayoutParams.MATCH_PARENT) {
			// 如果子元素是 MATCH_PARENT, 也就是希望占满父容器的空间, 那子元素的尺寸就取父容器的可用空间大小, 模式也是 EXACTLY, 表明子元素的尺寸也确定了
			resultSize = size;
			resultMode = MeasureSpec.EXACTLY;
		} else if (childDimension == LayoutParams.WRAP_CONTENT) {
		  //  如果子元素是 WRAP_CONTENT, 也就是宽/高希望能包裹自身的内容就可以了;
		  //  但由于这时子元素自身还没测量, 无法知道自己想要多大的尺寸, 
		  //  所以这时就先取父容器给子元素留下的最大空间, 模式为AT_MOST, 表示子元素的宽/高不能超过该最大值
			resultSize = size;
			resultMode = MeasureSpec.AT_MOST;
		}
		break;

	//  父容器的尺寸还没确定, 但是不能超过最大值
	case MeasureSpec.AT_MOST:
		if (childDimension >= 0) {
			//  子元素指定了大小, 就取子元素的尺寸, 模式为EXACTLY, 表明该子元素确定了尺寸
			//  这时父容器的限制对子元素来说是不起作用的, 子元素的尺寸是可以超出了父容器的大小, 超出的部分是显示不出来的
			resultSize = childDimension;
			resultMode = MeasureSpec.EXACTLY;
		} else if (childDimension == LayoutParams.MATCH_PARENT) {
		   //  子元素是 MATCH_PARENT, 表明子元素希望占满父容器, 
		   // 但是父容器自身的大小还没确定, 也无法给子元素确切的尺寸, 
		   //  这时就先取父容器给子元素留下的最大空间, 模式为AT_MOST, 表示子元素不能超过该最大值
			resultSize = size;
			resultMode = MeasureSpec.AT_MOST;
		} else if (childDimension == LayoutParams.WRAP_CONTENT) {
			//  子元素的尺寸只希望能包裹自身的内容就可以了, 这时子元素还没测量, 无法知道具体尺寸, 
			//  就先取父容器给子元素留下的最大空间, 模式为 AT_MOST, 表示子元素不能超过该最大值
			resultSize = size;
			resultMode = MeasureSpec.AT_MOST;
		}
		break;

	// 父容器没有对子元素的大小进行约束
	case MeasureSpec.UNSPECIFIED:
		if (childDimension >= 0) {
			// 子元素指定了大小, 就取子元素的尺寸, 模式为 EXACTLY, 表明该子元素确定了尺寸
			resultSize = childDimension;
			resultMode = MeasureSpec.EXACTLY;
		} else if (childDimension == LayoutParams.MATCH_PARENT) {
			//  子元素想要占满父容器, 先判断下子元素是否需要取0, 
			//  如果不需要取0, 就先取父容器给子元素留下的最大空间, 模式为 UNSPECIFIED, 表示子元素并没有受到约束
			resultSize = View.sUseZeroUnspecifiedMeasureSpec ? 0 : size;
			resultMode = MeasureSpec.UNSPECIFIED;
		} else if (childDimension == LayoutParams.WRAP_CONTENT) {
			//  子元素的尺寸只希望能包裹自身的内容就可以了, 判断下需不需要取0, 
			//  如果不需要就先取父容器给子元素留下的最大空间, 模式为 UNSPECIFIED, 表示子元素并没有受到约束
			resultSize = View.sUseZeroUnspecifiedMeasureSpec ? 0 : size;
			resultMode = MeasureSpec.UNSPECIFIED;
		}
		break;
	}
	// 根据得到的大小和模式返回一个MeasureSpec
	return MeasureSpec.makeMeasureSpec(resultSize, resultMode);
}
```
### View#layout  
```
public void layout(int l, int t, int r, int b) {
	//  成员变量 mPrivateFlags3 中的一些比特位存储着和 layout 相关的信息
	if ((mPrivateFlags3 & PFLAG3_MEASURE_NEEDED_BEFORE_LAYOUT) != 0) {
		//  如果在 mPrivateFlags3 的低位字节的第4位(从最右向左数第4位)的值为1, 
		//  那么就表示在 layout 布局前需要先对 View 进行量算, 
		//  这种情况下就会执行 View 的 onMeasure 方法对 View 进行量算
		onMeasure(mOldWidthMeasureSpec, mOldHeightMeasureSpec);
		//  量算完成后就会将 mPrivateFlags3 低位字节的第4位重置为 0, 
		//  移除掉标签 PFLAG3_MEASURE_NEEDED_BEFORE_LAYOUT
		mPrivateFlags3 &= ~PFLAG3_MEASURE_NEEDED_BEFORE_LAYOUT;
	}

	int oldL = mLeft;
	int oldT = mTop;
	int oldB = mBottom;
	int oldR = mRight;

	// 如果 isLayoutModeOptical()返回 true, 那么就会执行 setOpticalFrame()方法, 
	//  否则会执行 setFrame()方法, 并且 setOpticalFrame()内部会调用 setFrame(),
	//  所以无论如何都会执行 setFrame()方法。
	//  setFrame()方法会将 View 新的 left, top, right, bottom 存储到 View 的成员变量中
	//  并且返回一个 boolean 值, 如果返回 true 表示 View 的位置或尺寸发生了变化, 
	//  否则表示未发生变化
	boolean changed = isLayoutModeOptical(mParent) ?
			setOpticalFrame(l, t, r, b) : setFrame(l, t, r, b);


	if (changed || (mPrivateFlags & PFLAG_LAYOUT_REQUIRED) == PFLAG_LAYOUT_REQUIRED) {
		//  如果 View 的布局发生了变化, 或者 mPrivateFlags 有需要 LAYOUT 的标记 PFLAG_LAYOUT_REQUIRED, 
		//  那么就会执行以下代码
		//  首先会触发 onLayout 方法的执行, View 中默认的 onLayout 方法是个空方法
		//  不过继承自 ViewGroup 的类都需要实现 onLayout 方法, 从而在 onLayout 方法中依次循环子 View, 
		//  并调用子 View 的 layout 方法
		onLayout(changed, l, t, r, b);
		// 在执行完 onLayout 方法之后, 从 mPrivateFlags 中移除标签 PFLAG_LAYOUT_REQUIRED
		mPrivateFlags &= ~PFLAG_LAYOUT_REQUIRED;

		//  我们可以通过 View 的 addOnLayoutChangeListener(View.OnLayoutChangeListener listener)方法
		//  向 View 中添加多个 Layout 发生变化的事件监听器
		//  这些事件监听器都存储在 mListenerInfo.mOnLayoutChangeListeners 这个 ArrayList 中
		ListenerInfo li = mListenerInfo;
		if (li !=  && li.mOnLayoutChangeListeners != ) {
			//  首先对 mOnLayoutChangeListeners 中的事件监听器进行拷贝
			ArrayList<OnLayoutChangeListener> listenersCopy =
					(ArrayList<OnLayoutChangeListener>)li.mOnLayoutChangeListeners.clone();
			int numListeners = listenersCopy.size();
			for (int i = 0; i < numListeners; ++i) {
				//  遍历注册的事件监听器, 依次调用其 onLayoutChange 方法, 这样 Layout 事件监听器就得到了响应
				listenersCopy.get(i).onLayoutChange(this, l, t, r, b, oldL, oldT, oldR, oldB);
			}
		}
	}

	//  从 mPrivateFlags 中移除强制 Layout 的标签 PFLAG_FORCE_LAYOUT
	mPrivateFlags &= ~PFLAG_FORCE_LAYOUT;
	//  向 mPrivateFlags3 中加入 Layout 完成的标签 PFLAG3_IS_LAID_OUT
	mPrivateFlags3 |= PFLAG3_IS_LAID_OUT;
}
```
### View#setFrame  
```
protected boolean setFrame(int left, int top, int right, int bottom) {
	boolean changed = false;

	if (mLeft != left || mRight != right || mTop != top || mBottom != bottom) {
		//  将新旧 left, right, top, bottom 进行对比, 只要不完全相对就说明 View 的布局发生了变化, 
		// 则将 changed 变量设置为 true
		changed = true;

		//  先保存一下 mPrivateFlags 中的 PFLAG_DRAWN 标记
		int drawn = mPrivateFlags & PFLAG_DRAWN;

		//  分别计算 View 的新旧尺寸
		int oldWidth = mRight - mLeft;
		int oldHeight = mBottom - mTop;
		int newWidth = right - left;
		int newHeight = bottom - top;
		//  比较 View 的新旧尺寸是否相同, 如果尺寸发生了变化, 那么 sizeChanged 的值为 true
		boolean sizeChanged = (newWidth != oldWidth) || (newHeight != oldHeight);

		// Invalidate our old position
		invalidate(sizeChanged);

		//  将新的 left, top, right, bottom存储到 View 的成员变量中
		mLeft = left;
		mTop = top;
		mRight = right;
		mBottom = bottom;
		//  mRenderNode.setLeftTopRightBottom()  方法会调用  RenderNode 中原生方法的 nSetLeftTopRightBottom()方法, 
		//  该方法会根据 left, top, right, bottom 更新用于渲染的显示列表
		mRenderNode.setLeftTopRightBottom(mLeft, mTop, mRight, mBottom);

		//  向 mPrivateFlags 中增加标记 PFLAG_HAS_BOUNDS, 表示当前 View 具有了明确的边界范围
		mPrivateFlags |= PFLAG_HAS_BOUNDS;


		if (sizeChanged) {
			//  如果 View 的尺寸和之前相比发生了变化, 那么就执行 sizeChange()方法, 
			//  该方法中又会调用 onSizeChanged()方法, 并将 View 的新旧尺寸传递进去
			sizeChange(newWidth, newHeight, oldWidth, oldHeight);
		}

		if ((mViewFlags & VISIBILITY_MASK) == VISIBLE || mGhostView != ) {
			//  有可能在调用 setFrame 方法之前, invalidate方法就被调用了, 
			//  这会导致 mPrivateFlags 移除了 PFLAG_DRAWN 标签。
			//  如果当前 View 处于可见状态就将 mPrivateFlags 强制添加 PFLAG_DRAWN 状态位, 
			//  这样会确保下面的 invalidate()方法会执行到其父控件级别。
			mPrivateFlags |= PFLAG_DRAWN;
			invalidate(sizeChanged);
			// invalidateParentCaches()方法会移除其父控件的 PFLAG_INVALIDATED 标记, 
			//  这样其父控件就会重建用于渲染的显示列表
			invalidateParentCaches();
		}

		// 重新恢复 mPrivateFlags 中原有的 PFLAG_DRAWN  标记信息
		mPrivateFlags |= drawn;

		mBackgroundSizeChanged = true;
		if (mForegroundInfo != ) {
			mForegroundInfo.mBoundsChanged = true;
		}

		notifySubtreeAccessibilityStateChangedIfNeeded();
	}
	return changed;
}
```
### 测量流程  
第一次预测量-调用栈流程:  performTraversals-measureHierarchy-performMeasure
ViewRootImpl#performTraversals  
```
boolean layoutRequested = mLayoutRequested && (!mStopped || mReportNextDraw);
if (layoutRequested) {

    final Resources res = mView.getContext().getResources();

    if (mFirst) {
        // make sure touch mode code executes by setting cached value
        // to opposite of the added touch mode.
        mAttachInfo.mInTouchMode = !mAddedTouchMode;
        ensureTouchModeLocally(mAddedTouchMode);
    } else {
        if (!mPendingOverscanInsets.equals(mAttachInfo.mOverscanInsets)) {
            insetsChanged = true;
        }
        if (!mPendingContentInsets.equals(mAttachInfo.mContentInsets)) {
            insetsChanged = true;
        }
        if (!mPendingStableInsets.equals(mAttachInfo.mStableInsets)) {
            insetsChanged = true;
        }
        if (!mPendingDisplayCutout.equals(mAttachInfo.mDisplayCutout)) {
            insetsChanged = true;
        }
        if (!mPendingVisibleInsets.equals(mAttachInfo.mVisibleInsets)) {
            mAttachInfo.mVisibleInsets.set(mPendingVisibleInsets);
            if (DEBUG_LAYOUT) Log.v(mTag, "Visible insets changing to: "
                    + mAttachInfo.mVisibleInsets);
        }
        if (!mPendingOutsets.equals(mAttachInfo.mOutsets)) {
            insetsChanged = true;
        }
        if (mPendingAlwaysConsumeNavBar != mAttachInfo.mAlwaysConsumeNavBar) {
            insetsChanged = true;
        }
        if (lp.width == ViewGroup.LayoutParams.WRAP_CONTENT
                || lp.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            windowSizeMayChange = true;

            if (shouldUseDisplaySize(lp)) {
                // NOTE -- system code, won't try to do compat mode.
                Point size = new Point();
                mDisplay.getRealSize(size);
                desiredWindowWidth = size.x;
                desiredWindowHeight = size.y;
            } else {
                Configuration config = res.getConfiguration();
                desiredWindowWidth = dipToPx(config.screenWidthDp);
                desiredWindowHeight = dipToPx(config.screenHeightDp);
            }
        }
    }

    // Ask host how big it wants to be
    //  第一次测量  
    windowSizeMayChange |= measureHierarchy(host, lp, res,
            desiredWindowWidth, desiredWindowHeight);
}
```
ViewRootImpl#measureHierarchy  
```
//  performMeasure  
if (!goodMeasure) {
    childWidthMeasureSpec = getRootMeasureSpec(desiredWindowWidth, lp.width);
    childHeightMeasureSpec = getRootMeasureSpec(desiredWindowHeight, lp.height);
    //  开始测量  
    performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
    if (mWidth != host.getMeasuredWidth() || mHeight != host.getMeasuredHeight()) {
        windowSizeMayChange = true;
    }
}
```
第二次最终测量-调用栈流程:  performTraversals-performMeasure
ViewRootImpl#performTraversals  
```
if (!mStopped || mReportNextDraw) {
    boolean focusChangedDueToTouchMode = ensureTouchModeLocally(
            (relayoutResult&WindowManagerGlobal.RELAYOUT_RES_IN_TOUCH_MODE) != 0);
    if (focusChangedDueToTouchMode || mWidth != host.getMeasuredWidth()
            || mHeight != host.getMeasuredHeight() || contentInsetsChanged ||
            updatedConfiguration) {
        int childWidthMeasureSpec = getRootMeasureSpec(mWidth, lp.width);
        int childHeightMeasureSpec = getRootMeasureSpec(mHeight, lp.height);

         // Ask host how big it wants to be
         //  第二次 测量  
        performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);

        // Implementation of weights from WindowManager.LayoutParams
        // We just grow the dimensions as needed and re-measure if
        // needs be
        int width = host.getMeasuredWidth();
        int height = host.getMeasuredHeight();
        boolean measureAgain = false;

        if (lp.horizontalWeight > 0.0f) {
            width += (int) ((mWidth - width) * lp.horizontalWeight);
            childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width,
                    MeasureSpec.EXACTLY);
            measureAgain = true;
        }
        if (lp.verticalWeight > 0.0f) {
            height += (int) ((mHeight - height) * lp.verticalWeight);
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                    MeasureSpec.EXACTLY);
            measureAgain = true;
        }

        if (measureAgain) {
            performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
        layoutRequested = true;
    }
}
```
### 测量模式的表现  
子控件 size = width = height = 200dp;  
父控件 size = width = height = 100dp;  
子控件画出来的是, 内切的实心圆;  
1.. 父控件是 LinearLayout, 子控件值展示 1/4 圆;  
2.. 父控件是 FrameLayout, 子控件值展示 1/4 圆;
3.. 父控件是 RelativeLayout, 子控件值展示的是, 和父控件内切的实心圆;

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
http://aspook.com/2017/11/01/Android-Choreographer  

双缓冲机制  
https://blog.csdn.net/zhangbijun1230/article/details/80376181  

 
view#绘制的原理  
https://www.jianshu.com/p/150ddb223fff  
https://www.jianshu.com/p/4a68f9dc8f7c  
https://blog.csdn.net/feiduclear_up/article/details/46772477  
https://blog.csdn.net/iispring/article/details/49203945  
深入理解Android卷3-第6章-深入理解控件系统(邓凡平)   

https://www.jianshu.com/p/733c7e9fb284(Alex_Cin 留言, 记得删掉)  

