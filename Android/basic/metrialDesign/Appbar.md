### Appbar  
根据我所看到的，我首先想到的就是专辑封面是放到一个AppBarLayout里面，并且在滚动区域拖到边界的时候尺寸会发生变化。  
让我们假定这个猜想是正确的并且用“Behavior”这个术语表示它。依鄙人之见，如果我的猜想是正确的，  
谷歌应该会在Material Design文档的滚动部分提供一个越界滚动的使用说明。    
我们的目标就是保证AppBarLayout.Behavior的完整性，在此基础上再创建一个扩展的行为。因此：  
public class OverscrollScalingViewAppBarLayoutBehavior extends AppBarLayout.ScrollingViewBehavior  
因为这是默认的AppBarLayout.Behavior，所以建议只有在依赖视图是AppBarLayout的时候起作用。  
```
@Override
public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
   return dependency instanceof AppBarLayout;
}
```
接下来，我们需要获取想要在拖到边界时要改变尺寸的视图的一个实例。最好的方法就是在onLayoutChild()方法中获取：  
```

@Override
public boolean onLayoutChild(CoordinatorLayout parent ....) {
    boolean superLayout = super.onLayoutChild(parent, abl, layoutDirection);
    if (mTargetScalingView == null) {
        mTargetScalingView = parent.findViewByTag(TAG);
        if(mTargetScalingView != null){
             mScaleImpl.obtainInitialValues();
         }
     }
    return superLayout;
}
```
而且我们需要保证只有在垂直滚动的时候起作用：  
```
@Override
public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout,... int nestedScrollAxes) {
    return nestedScrollAxes == View.SCROLL_AXIS_VERTICAL;
}
```
如果我们先前没有在程序中显示设置，会设置ViewScaler为默认的Scaler。

在内容滚动的瞬间，真正重要的问题就有头绪了。CoordinatorLayout.Behavior提供了一个onNestedScroll()方法，当滚动进行的时候这个方法会被调用，  
并且当内容滚动到边界的时候也会调用。最后两个参数dyUnconsumed和dxUnconsumed提供了未被该行为的目标视图填满的像素值。  

这个方法对我们实现尺寸改变来说太重要了。所以我列出了哪些情况需要改变尺寸，哪些情况不需要：  
需要改变尺寸  

存在未填满的像素，如dyUnconsumed小于0  
AppBarLayout是展开的，getTopAndBottomOffset() >= mScaleImpl.getInitialParentBottom()  
不需要改变尺寸  

AppBarLayout中没有子视图可以改变尺寸  
有填充的像素，如dyConsumed不等于0  
```
@Override
public void onNestedScroll(CoordinatorLayout ... int dxUnconsumed, int dyUnconsumed) {
    if (mTargetScalingView == null || dyConsumed != 0) {
        mScaleImpl.cancelAnimations();
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        return;
    }

    if (dyUnconsumed < 0 && getTopAndBottomOffset() >= mScaleImpl.getInitialParentBottom()) {
        int absDyUnconsumed = Math.abs(dyUnconsumed);
        mTotalDyUnconsumed += absDyUnconsumed;
        mTotalDyUnconsumed = Math.min(mTotalDyUnconsumed, mTotalTargetDyUnconsumed);
        mScaleImpl.updateViewScale();
    } else {
        mTotalDyUnconsumed = 0;
        mScaleImpl.setShouldRestore(false);
        if (dyConsumed != 0) {
            mScaleImpl.cancelAnimations();
        }
        super.onNestedScroll(coordinatorLayout, .... dxUnconsumed, dyUnconsumed);
    }
}
```
当嵌套的overscroll停止的时候，我们需要将视图的边界和大小重置到它们的原始值。  

```
@Override
public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target) {
    mScaleImpl.retractScale();
    super.onStopNestedScroll(coordinatorLayout, child, target);

}
```
ViewScaler  

这个类实现了AppBarLayout应该如何改变它的底部以及视图应该如何改变尺寸的逻辑。大多数行为都依赖累积的未填充的像素。  
我们可以为最大累积值设置一个约束值，这样可以很容的找到要如何改变AppBarLayout底部和改变视图的尺寸。  
ParentScaler是ViewScaler的父类，它能让AppBarLayout近乎平滑的改变尺寸。我就不在这里贴大量代码了，如果你有兴趣，可以从这里获取代码。  

### 参考  
https://github.com/hehonghui/android-tech-frontier/blob/master/issue-32/AppBarLayout%E7%9A%84%E8%B6%8A%E7%95%8C%E6%BB%9A%E5%8A%A8%E8%A1%8C%E4%B8%BA.md  
