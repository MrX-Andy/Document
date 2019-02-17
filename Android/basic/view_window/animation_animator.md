### Animation 与 Animator   
android 的动画, 分为 视图动画(View Animation) 和 属性动画(Property Animation);  
视图动画, 分为 补间动画(Tween Animation) 和 逐帧动画(Frame Animation);  
补间动画, 主要有:  平移(Translate), 缩放(Scale), 旋转(Rotate), 透明(Alpha);  

[屏幕刷新机制-invalidate](/Android/basic/view_window/invalidate_requestLayout.md)   

### Animation  
大概的原理是:  
他的实现机制是, 在每次进行绘图的时候,通过对整块画布的矩阵进行变换, 从而实现一种视图坐标的移动, 但实际上其在 View 内部真实的坐标位置及其他相关属性始终恒定;  

startAnimation 会触发 invalidateParentCaches 和 invalidate;  
在 draw 方法体, 中调用 getTransformation 来根据当前时间计算动画进度, 紧接着调用 applyTransformation, 并传入动画进度来应用动画;  
1.. 如果使用的是硬件绘制, 将 Transform 变化结果应用到 renderNode;  
      调用 renderNode.setAnimationMatrix 执行动画;  
2.. 如果使用的是软件绘制, 将 Transform 变化结果应用到 Canvas;  
      调用 canvas.concat 方法, 执行动画; 
所以, Animation 产生的动画数据, 实际上并不是 View 本身的, 而是应用在 RenderNode 或者 Canvas 上的;  
所以 Animation 不会改变 View 的属性;  

★AlphaAnimation:  
重写父类 Animation applyTransformation 方法,  
```
@Override
protected void applyTransformation(float interpolatedTime, Transformation t) {
    final float alpha = mFromAlpha;
    // 根据变化率求出当前 alpha 值，在设置给 Transformation。
    t.setAlpha(alpha + ((mToAlpha - alpha) * interpolatedTime));
}
```
View#draw  
```
if (drawingWithRenderNode) {
    //  如果是硬件加速, 则通过 RenderNode.setAlpha 方法设置透明度;  
    renderNode.setAlpha(alpha * getAlpha() * getTransitionAlpha());
} else if (layerType == LAYER_TYPE_NONE) {
    //  如果是软件绘制, 则通过 Canvas.saveLayerApla 方法时启用画布新层时, 设置透明度;
    canvas.saveLayerAlpha(sx, sy, sx + getWidth(), sy + getHeight(),multipliedAlpha);
}
```
★ ScaleAnimation:  
关键通过 Matrix.setScale 完成;  
重写父类 Animation applyTransformation 方法,  
```
@Override
protected void applyTransformation(float interpolatedTime, Transformation t) {
    //  最后设置到矩阵上
    if (mPivotX == 0 && mPivotY == 0) {
        t.getMatrix().setScale(sx, sy);
    } else {
        //  同时设置中心点
        t.getMatrix().setScale(sx, sy, scale * mPivotX, scale * mPivotY);
    }
}
```
### Animator  
ObjectAnimator 继承与 ValueAnimator;  
ValueAnimator 继承与 Animator;  

属性动画, 相对于补间动画, 多了一些 暂停和恢复, 可以调整进度, 让动画有了视频播放的概念;  

Animator 动画的实现机制说起来其实更加简单一点, 因为他其实只是计算动画开启之后, 结束之前, 到某个时间点得时候, 某个属性应该有的值, 然后通过回调接口去设置具体值,    
其实 Animator 内部并没有针对某个 view 进行刷新, 来实现动画的行为, 动画的实现是在设置具体值的时候, 方法内部自行调取的类似 invalidate 之类的方法实现的.  
也就是说, 使用 Animator, 内部的属性发生了变化;  

ValueAnimator#start  
ValueAnimator#addAnimationCallback  
AnimationHandler#addAnimationFrameCallback  
```
if (mAnimationCallbacks.size() == 0) {
    getProvider().postFrameCallback(mFrameCallback);
}
```
AnimationHandler#getProvider  
```
if (mProvider == null) {
    mProvider = new MyFrameCallbackProvider();
}
```
AnimationHandler.MyFrameCallbackProvider#postFrameCallback  
```
mChoreographer.postFrameCallback(callback);
```
Choreographer#postFrameCallback  
Choreographer#postCallbackDelayedInternal  
```
synchronized (mLock) {
    final long now = SystemClock.uptimeMillis();
    final long dueTime = now + delayMillis;
    mCallbackQueues[callbackType].addCallbackLocked(dueTime, action, token);
    if (dueTime <= now) {
        scheduleFrameLocked(now);
    } else {
        Message msg = mHandler.obtainMessage(MSG_DO_SCHEDULE_CALLBACK, action);
        msg.arg1 = callbackType;
        msg.setAsynchronous(true);
        mHandler.sendMessageAtTime(msg, dueTime);
    }
}
// 等待 VSYNC 垂直刷新信号到来;  回调 这个 callback;  
```
Choreographer.FrameCallback#doFrame  
AnimationHandler#doAnimationFrame  

### Animator#总结  
Animation 实现了 AnimationFrameCallback 接口;  
Animator 在start 方法体内部, 调用 addAnimationCallback 方法, 然后得到一个 AnimationHandler;  
调用 AnimationHandler#addAnimationFrameCallback, 将 Animator 对象, 作为一个 callback, 添加到回调队列中;  
如果当前存在要运行的动画, 那么 AnimationHandler 会去通过 Choreographer 向底层注册监听下一个屏幕刷新信号;  
当接收到信号时, 它的 mFrameCallback 会开始进行工作, 工作的内容包括遍历列表来分别处理每个属性动画在当前帧的行为;  
处理完列表中的所有动画后, 如果列表还不为 0, 那么它又会通过 Choreographer 再去向底层注册监听下一个屏幕刷新信号事件, 如此反复, 直至所有的动画都结束;  

AnimationHandler 遍历列表处理动画是在 doAnimationFrame() 中进行, 而具体每个动画的处理逻辑则是在各自, 也就是 ValueAnimator 的 doAnimationFrame() 中进行;  
各个动画如果处理完自身的工作后, 发现动画已经结束了, 那么会将其在列表中的引用赋值为空, AnimationHandler 最后会去将列表中所有为 null 的都移除掉, 来清理资源;  

### 参考#Animation    
https://juejin.im/post/5acb95b16fb9a028dc4152ba  
https://www.jianshu.com/p/fcd9c7e9937e  

### 参考#Animator    
https://www.cnblogs.com/dasusu/p/8595422.html  
https://www.jianshu.com/p/ee7e3d79006d  
http://dandanlove.com/2018/05/02/Android-animation-source  

### 用法#Animator    
https://juejin.im/post/5c595158f265da2d9710cb6e  
