[关于展示吐司和通知](library/open_toast_notification.md)     
[Activity启动模式与任务栈](library/launchMode.md)    
[清单文件Activity标签属性](library/manifest_tag.md)    
[状态栏高度24dp](ImageFiles/status_bar_height.png)    
[Activity常见方法](library/function.md)   
[Fragment重叠异常](library/solution_001.md)  

不管 Activity 是不是被回收, 只要执行 onStop 就一定会先执行 onSaveInstanceState;  

Activity 会通过 android:id 逐个恢复View的State;  
也就是说, 如果 android:id 为空, View 将不具备恢复 State 的能力了;  
所有的自定义控件, 都应该实现State相关方法, onSaveInstanceState and onRestoreInstanceState;  
一旦Fragment从回退栈出来，Fragment本身还在，View却是重新创建的;  
但是给 TextView, EditText 设置 android:freezeText="true" 会让其在 Fragment 内, 自动保存State;     

启动一个 Activity 和 Fragment, 他们的生命周期方法, 调用顺序;  
切换横竖屏;  屏幕锁;  
[链接](library/lifecycle_sample.md)  

### Fragment  

setRetainInstance  
Fragment 具有属性 retainInstance, 默认值为 false;   
当设备旋转时, fragment 会随托管 activity 一起销毁并重建;  

如果 retainInstance 属性值为 false, FragmentManager会立即销毁该fragment实例;   
随后, 为适应新的设备配置, 新的Activity的新的FragmentManager会创建一个新的fragment及其视图;  

如果retainInstance属性值为true, 则该fragment的视图立即被销毁, 但fragment本身不会被销毁;   
为适应新的设备配置, 当新的Activity创建后, 新的FragmentManager会找到被保留的fragment, 并重新创建其视图；  
一旦发生Activity重启现象, Fragment会跳过onDestroy直接进行onDetach（界面消失、对象还在）,   
而Fragment重启时也会跳过onCreate, 而onAttach和onActivityCreated还是会被调用;  
需要注意的是, 要使用这种操作的Fragment不能加入backstack后退栈中;  
并且, 被保存的Fragment实例不会保持太久, 若长时间没有容器承载它, 也会被系统回收掉的;  

[FragmentManagerImpl, Api21](library/FragmentManagerImplApi21.md)  
getActivity()空指针问题  
❀ 搞清楚, 为什么f.mActivity() 为空    
如果app长时间在后台运行, 再次进入app的时候可能会出现crash, 而且fragment会有重叠现象;  
如果系统内存不足、切换横竖屏、app长时间在后台运行, Activity都可能会被系统回收然后重建,   
但Fragment并不会随着Activity的回收而被回收, 创建的所有 Fragment会被保存到Bundle里面, 从而导致Fragment丢失对应的Activity;  
Fragment放在ViewPager中, ViewPager只预加载三个, 在跳转到未被预加载的Item的时候, 目标Fragment也重新创建, 这个时候, 通过getActivity()获取不到context;  
❀ 解决办法  
Fragment中维护一个 全局的 Activity 对象, 在 onAttach 方法中 给其赋值, 在 onDetach 中, 把它置空;  
管理好Fragment 的生命周期, 在onCreate 开始监听, 在onDestroy时, 解除监听, 忽略掉耗时操作的回调;  



#### commit  

❀ commit()  
在主线程中异步执行, 其实也是 Handler 抛出任务, 等待主线程调度执行;  
commit() 需要在宿主 Activity 保存状态之前调用, 否则会报错;   
这是因为如果 Activity 出现异常需要恢复状态, 在保存状态之后的 commit() 将会丢失, 这和调用的初衷不符, 所以会报错;  

❀ commitAllowingStateLoss()  
commitAllowingStateLoss() 也是异步执行, 但它的不同之处在于, 允许在 Activity 保存状态之后调用, 也就是说它遇到状态丢失不会报错;  

❀ commitNow()  
commitNow() 是同步执行的, 立即提交任务;  
FragmentManager.executePendingTransactions() 也可以实现立即提交事务;  
但我们一般建议使用 commitNow(), 因为另外那位是一下子执行所有待执行的任务, 可能会把当前所有的事务都一下子执行了, 这有可能有副作用;  
此外, 这个方法提交的事务可能不会被添加到 FragmentManger 的后退栈, 因为你这样直接提交, 有可能影响其他异步执行任务在栈中的顺序;  
和 commit() 一样, commitNow() 也必须在 Activity 保存状态前调用, 否则会抛异常;  

attach 与 detach  
transaction.attach(fragment); 对应 onCreateView-onViewCreated-onActivityCreated-onStart-onResume    
transaction.detach(fragment);  对应 onPause-onStop-onDestroyView  


#### transaction  
1.. replace  加入回退栈, Fragment不销毁, 但是切换回销毁视图和重新创建视图;  
2.. replace  未加回退栈, Fragment销毁掉;  
3.. hide. show. Fragment不销毁，也不销毁视图, 隐藏和显示不走生命周期;  

replace, AFragment 加入回退栈  
在同一个位置, 第一次 replace AFragment, 第二次replace BFragment;    
```
A: onAttach -> onCreate -> onCreateView -> onActivityCreated -> onStart -> onResume;  
A: onPause -> onStop -> onDestroyView   
B: onAttach -> onCreate -> onCreateView -> onActivityCreated -> onStart -> onResume;  
```

replace, AFragment 未加回退栈  
在同一个位置, 第一次 replace AFragment, 第二次replace BFragment;    
```
A: onAttach -> onCreate -> onCreateView -> onActivityCreated -> onStart -> onResume;  
A: onPause -> onStop -> onDestroyView -> onDestroy -> onDetach  
B: onAttach -> onCreate -> onCreateView -> onActivityCreated -> onStart -> onResume;  
```


detach 与 attach  
fragmentTransaction.detach(fragmentA);  
```
onPause -> onStop -> onDestroyView  
```
fragmentTransaction.attach(fragmentA);  
```
onCreateView -> onActivityCreated -> onStart -> onResume  
```

#### fragmentManager  

popBackStack(String tag,int flags)  
```
如果  tag = null, flags = 0, 弹出回退栈中最上层的那个fragment  
如果  tag = null, flags = 1, 弹出回退栈中所有fragment  
如果  tag != null, flags = 0, 弹出该fragment以上的所有Fragment, 不包括 tag
如果  tag != null, flags = 1, 弹出该fragment以上的所有Fragment, 包括 tag  
原本 D -> C -> B -> A ;  
执行 
```

未挂载异常  
```
//  "Fragment " + this + " not attached to Activity"
transaction.add(routerFragment, RouterConfig.FM_TAG);
transaction.commitAllowingStateLoss();
manager.executePendingTransactions();
```

###  参考  
https://inthecheesefactory.com/blog/fragment-state-saving-best-practices/en  
https://github.com/nuuneoi/StatedFragment  

❀  Activity 参考  
http://liuwangshu.cn/framework/ams/2-activitytask.html  
http://liuwangshu.cn/framework/component/6-activity-start-1.html  
http://liuwangshu.cn/framework/component/7-activity-start-2.html  
https://developer.android.com/training/basics/activity-lifecycle/recreating?utm_campaign=adp_series_processes_012016&utm_source=medium&utm_medium=blog  


❀ fragment 参考  
http://toughcoder.net/blog/2015/04/30/android-fragment-the-bad-parts/  
http://www.jianshu.com/p/825eb1f98c19  
https://github.com/AlanCheen/Android-Resources/blob/master/Fragment.md    
https://github.com/JustKiddingBaby/FragmentRigger  
http://blog.csdn.net/u011240877/article/details/78132990#fragment-的使用  
https://www.jianshu.com/p/f2fcc670afd6  
https://www.jianshu.com/p/d9143a92ad94  
https://www.jianshu.com/p/fd71d65f0ec6  
https://www.jianshu.com/p/38f7994faa6b  
https://www.jianshu.com/p/9dbb03203fbc  
https://www.jianshu.com/p/78ec81b42f92  
https://www.jianshu.com/p/c12a98a36b2b  
http://toughcoder.net/blog/2015/04/30/android-fragment-the-bad-parts/  
https://github.com/YoKeyword/Fragmentation/blob/master/README_CN.md  
https://meta.tn/a/15e2d3292a521d700b4fef2f4ebaaa331b8df551431a766ff85b9a5b50c851fc  
https://meta.tn/a/b5b52d1cf21bb9f929cfc2b78b03927c97e74d076d094b12656c6c8c661d3072  

❀ getActivity()空指针问题  参考  
http://blog.csdn.net/goodlixueyong/article/details/48715661  

❀ application 参考  
http://www.jianshu.com/p/f665366b2a47  

❀ context 参考  
http://liuwangshu.cn/framework/context/2-activity-service.html  
https://blog.csdn.net/guolin_blog/article/details/47028975  


❀ onActivityResult  
请放弃使用 类似的库, 因为在 页面被回收, 页面重启后, 回调不会被执行的；  
https://github.com/VictorAlbertos/RxActivityResult  
https://github.com/florent37/InlineActivityResult  
https://github.com/NateWickstrom/RxActivityResult  
https://github.com/nekocode/RxActivityResult  

❀ 启动模式 IntentFilter  
https://juejin.im/post/5c5d85da6fb9a049fd104d8f  
