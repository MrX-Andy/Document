### Behavior  

1. AppBarLayout.Behavior;  
2. AppBarLayout.ScrollingViewBehavior;  
3. FloatingActionButton.Behavior;  
4. Snackbar.Behavior;  
5. BottomSheetBehavior;  
6. SwipeDismissBehavior;  
7. HeaderBehavior;  
8. ViewOffsetBehavior;  
9. HeaderScrollingViewBehavior;  
只能给 CoordinateLayout 的直接子控件使用  
```
/**
 * 如果 在xml 中使用   app:layout_behavior="xxx.xxx.xxx.MyBehavior" 必须要复写 这个方法
 * 如果需要在构造函数中 传递参数，只能给 使用 这个 behavior 添加
 */
public Behavior(Context context, AttributeSet attrs)
```
```
/**
 * 确定所提供的子视图是否有另一个特定的同级视图作为布局从属，确定你依赖哪些View。
 * 返回 true 即可确立依赖关系
 */
public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency);
```
```
/**
 * 子控件 需要 发生改变 返回 true
 * 当  dependency 发生改变的时候，这个方法会调用，
 * 而我们在 onDependentViewChanged 方法里做出相应的改变，就能做出我们想要的交互效果了！
 * 当我们改变了child的 size 或者 position 的时候我们需要返回true，
 * 同样的，child也需要发生改变，这个时候我们需要返回 true
 * 差不多可以理解为 当我们的 dependency 发生了改变，
 * child 是指使用该 Behavior的View
 * dependency担任触发behavior的角色，并与child进行互动。
 */
public boolean onDependentViewChanged(CoordinatorLayout parent, V child, View dependency) {
	return false;
}
```
layoutDependsOn 比 onLayoutChild 先执行， 在滑动过程中， layoutDependsOn  会多次执行，  

### 参考
Cyandev	http://www.jianshu.com/p/7f50faa65622	http://www.jianshu.com/u/c49454e0ae54  
ToDou		https://github.com/ToDou/appbarlayout-spring-behavior  
https://github.com/bherbst/QuickReturnFooterSample  
http://guides.codepath.com/android/Floating-Action-Buttons#using-coordinatorlayout  
https://github.com/githubwing/CustomBehavior  
https://github.com/cstew/CustomBehavior  
https://github.com/saulmm/CoordinatorBehaviorExample  
https://lab.getbase.com/introduction-to-coordinator-layout-on-android/  
https://github.com/ToDou/appbarlayout-spring-behavior  
https://github.com/zoonooz/simple-view-behavior  
https://github.com/miguelhincapie/CustomBottomSheetBehavior  
https://github.com/mugku/CoordinatorLayoutHelper  
https://github.com/BCsl/UcMainPagerDemo  
https://github.com/zzz40500/AndroidSweetBehavior  
https://github.com/CSnowStack/BehaviorDemo  
https://github.com/unixzii/android-FancyBehaviorDemo  
https://github.com/skimarxall/AnchorSheetBehavior  
https://github.com/godness84/appbar-snap-behavior  
https://github.com/DevExchanges/Custom-Coordinator-Behavior  
https://github.com/oubowu/CoordinateBehaviorLibrary  
https://github.com/Plinzen/BottomSheetBehaviorSample  
https://github.com/Wrdlbrnft/GravityBehavior  
https://github.com/ut2014/CustomBehavior  
https://github.com/XuDaojie/ScrollBehavior  
https://github.com/cocolove2/ViewSnackBarBehavitor  
https://github.com/codedavid/CoordinatorBehavior  
https://github.com/CSnowStack/CoordinatorBehaviorExample  
https://github.com/Blankeer/CoordinatorLayout_Behavior_Demo  
https://github.com/biggyBA/CustomBottomSheetBehavior  
https://github.com/zhengjingle/WuBaDemo  
https://github.com/HaobinXu/WeiboDiscoverLayout  
https://github.com/glmsb/MaterialDesigner  

