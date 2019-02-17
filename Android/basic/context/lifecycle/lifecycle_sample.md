### 打开与关闭一个新的 Activity

打开 A 页面  
```
AActivity#onCreate
AActivity#onStart
AActivity#onResume

// 点击跳转到  start BActivity  
AActivity#onPause

BActivity#onCreate
BActivity#onStart
BActivity#onResume

AActivity#onSaveInstanceState
AActivity#onStop
```

B 页面按下 Home  
```
BActivity#onPause
BActivity#onSaveInstanceState
BActivity#onStop 
``` 

点击图标, 再次回到 B 页面  
```
BActivity#onRestart
BActivity#onStart
BActivity#onResume
```

B 页面返回 A 页面  
```
BActivity#onPause

AActivity#onRestart
AActivity#onStart 
AActivity#onResume

BActivity#onStop
BActivit#onDestroy  
```
A 页面退出  
```
AActivity#onPause
AActivity#onStop
AActivity#onDestroy
```

### onActivityResult 与 生命周期函数  
A 页面, 跳转到 B页面, B 页面调用 setResult 并 finish;  
```
BActivity#onPause

AActivity#onActivityResult

AActivity#onRestart

AFragment#onStart
AActivity#onStart

AActivity#onResume
AFragment#onResume

BActivity#onStop
BActivity#onDestroy
```
AActivity launchMode=single_task;  
B 页面, setResult 并 finish;  
结果是一样的, 因为都是 BActivity 先出栈;  

### BActivity 通过 single_tak 再次启动  AActivity  
AActivity 通过startActivityForResult 启动 BActivity, BActivity setResult, 但是加上了 single_task 的标记;  

```
BActivity#onPause

AActivity#onActivityResult   //  intent 对象为 null
AActivity#onNewIntent  //  intent 对象非空  
AActivity#onRestart

AFragment#onStart
AActivity#onStart

AActivity#onResume
AFragment#onResume

BActivity#onStop
BActivity#onDestroy
```
### 打开与关闭一个新的 Activity + Fragment  
假设 AActivity 在 onCreate 方法里面, 按顺序 add AFragment 和 BFragment;  
```
AActivity#onCreate  

AFragment#onAttach 
AFragment#onCreate 

BFragment#onAttach 
BFragment#onCreate 

AFragment#onCreateView 
AFragment#onViewCreated
AFragment#onActivityCreated

BFragment#onCreateView
BFragment#onViewCreated
BFragment#onActivityCreated

AFragment#onStart  
BFragment#onStart  
AActivity#onStart  

AActivity#onResume  
AFragment#onResume
BFragment#onResume
```

从 AActivity 跳转到 BActivity  
```
AFragment#onPause
BFragment#onPause
AActivity#onPause  

BActivity#onCreate  
BActivity#onStart  
BActivity#onResume  

AFragment#onStop    
AFragment#onStop    
AActivity#onStop  
```
在 BActivity 点击返回  
```
BActivity#onPause  
AActivity#onRestart  

AFragment#onStart
BFragment#onStart
AActivity#onStart  

AActivity#onResume  
AFragment#onResume
BFragment#onResume

BActivity#onStop
BActivity#onDestory
```
在 AActivity 点击返回  
```
AFragment#onPause
BFragment#onPause
AActivity#onPause  

AFragment#onStop
BFragment#onStop
AActivity#onStop  

AFragment#onDestoryView  
AFragment#onDestory  
AFragment#onDetach

BFragment#onDestoryView  
BFragment#onDestory  
BFragment#onDetach  

AActivity#onDestory  

```
### 切换 show hide  
如果是 fragment 结合 FrameLayout, 回调 onHiddenChanged;  
如果是 fragment 结合 ViewPager, 回调 setUserVisibleHint;  

如果是通过 add-show-hide, 控制展示和隐藏 Fragment, 那么在调用 show-hide 的时候, 不会引起 Fragment 的任何生命周期变化, 但是会回调 onHiddenChanged 方法;  
如果是进行了 Activity 页面的跳转操作, 则不会回调 onHiddenChanged 方法, 会引起所有 Fragment 的生命周期方法;  

如果在 Activity#onCreate, 调用 add 和 hide, 那么 fragment 会在 onActivityCreated 之后, 回调 onHiddenChanged 方法;  

### 切换 replace  
1.. 先 replace AFragment, 再 replace BFragment, AFragment 没有添加到回退栈;  
```
BFragment#onAttach  
BFragment#onCreate  

AFragment#onPause  
AFragment#onStop  
AFragment#onDestoryView  
AFragment#onDestory  
AFragment#onDetach  

BFragment#onCreateView
BFragment#onViewCreated
BFragment#onActivityCreated
BFragment#onStart
BFragment#onResume
```

2.. 先 replace AFragment, 再 replace BFragment, AFragment 添加到回退栈;  
```
BFragment#onAttach  
BFragment#onCreate  

AFragment#onPause  
AFragment#onStop  
AFragment#onDestoryView  

BFragment#onCreateView
BFragment#onViewCreated
BFragment#onActivityCreated
BFragment#onStart
BFragment#onResume
```
2.. 显示 BFragment, 点击返回  
```
BFragment#onPause
BFragment#onStop
BFragment#onStop
BFragment#onDestroyView
BFragment#onDestroy
BFragment#onDetach

AFragment#onCreateView
AFragment#onViewCreated
AFragment#onActivityCreated
AFragment#onStart
AFragment#onResume
```

### Viewpager + Fragment  
假设AActivity 里面有 20个 Fragment  A01Fragment 到 A20Fragment;  
1.. 当前展示 A01Fragment;  
```
MainActivity#onPause 
A01Fragment#setUserVisibleHint isVisibleToUser = false
A02Fragment#setUserVisibleHint isVisibleToUser = false
A01Fragment#setUserVisibleHint isVisibleToUser = true

A01Fragment#onAttach 
A01Fragment#onCreate 

A02Fragment#onAttach 
A02Fragment#onCreate 

A01Fragment#onCreateView 
A01Fragment#onViewCreated 
A01Fragment#onActivityCreated 
A01Fragment#onStart 
A01Fragment#onResume 

A02Fragment#onCreateView 
A02Fragment#onViewCreated 
A02Fragment#onActivityCreated 
A02Fragment#onStart 
A02Fragment#onResume 

MainActivity#onStop 
```
2.. 把 A02Fragment 划出来  
```
A03Fragment#setUserVisibleHint isVisibleToUser = false
A01Fragment#setUserVisibleHint isVisibleToUser = false
A02Fragment#setUserVisibleHint isVisibleToUser = true

A03Fragment#onAttach
A03Fragment#onCreate
A03Fragment#onCreateView
A03Fragment#onViewCreated
A03Fragment#onActivityCreated
A03Fragment#onStart
A03Fragment#onResume
```
3.1. 假设再把 A01Fragment 划出来  
```
A02Fragment#setUserVisibleHint isVisibleToUser = false
A01Fragment#setUserVisibleHint isVisibleToUser = true

A03Fragment#onPause
A03Fragment#onStop
A03Fragment#onDestroyView
```
3.2. 假设没有执行 3.1, 而是执行3.2, 把 A03Fragment 划出来  
```
A04Fragment#setUserVisibleHint isVisibleToUser = false
A02Fragment#setUserVisibleHint isVisibleToUser = false
A03Fragment#setUserVisibleHint isVisibleToUser = true

A04Fragment#onAttach
A04Fragment#onCreate

A01Fragment#onPause
A01Fragment#onStop
A01Fragment#onDestroyView

A04Fragment#onCreateView
A04Fragment#onViewCreated
A04Fragment#onActivityCreated
A04Fragment#onStart
A04Fragment#onResume

```

总结下来:  
假设, 下标从 01...20;  
默认 viewpager 的缓存是 3;  
setUserVisibleHint 回调, 是最先执行的, 早于所有的生命周期方法;  
首次加载, 01 和 02 都会执行到 onResume;  

当把右侧的 Fragment 划出来的时候, 也就是递增展示新页面的时候, 当前展示 01, 现在把 02 展示出来,  
那么会先回调 03-02-01 setUserVisibleHint 方法, 再执行 03Fragment 的 onAttach 到 onResume 过程;  

继续递增下去, 当前展示 02, 把 03 划出来,  
那么会先回调 04-02-03 setUserVisibleHint 方法, 再执行 04Fragment 的 onAttach-onCreate, 再执行 01Fragment 的 onPause-onDestroyView, 再执行 04Fragment 的 onCreateView-onResume;  


递增:  
...   
当前展示 18, 16执行 onDestroyView, 创建 19, viewPager: 17-18-19  
当前展示 19, 17执行 onDestroyView, 创建 20, viewPager: 18-19-20  
当前展示 20, 18执行 onDestroyView, viewPager: 19-20  

递减  
当前展示 19, 18执行 onCreateView, viewPager: 19-20-18  
当前展示 18, 17执行 onCreateView, 20执行 onDestroyView, viewPager: 19-18-17  
...  
当前展示 03, 02执行 onCreateView, 05执行 onDestroyView, viewPager: 02-03-04  
当前展示 02, 01执行 onCreateView, 04执行 onDestroyView, viewPager: 01-02-03  
当前展示 02, 01执行 onCreateView, 04执行 onDestroyView, viewPager: 01-02-03  
当前展示 01, 03执行 onDestroyView, viewPager: 01-02  
### StatePager+Fragment  
递增  
01-02 onAttach-onResume  
当前展示 02, 03执行 创建, viewPager: 01-02-03  
当前展示 03, 01执行 onDetach, 04执行创建, ViewPager: 02-03-04  

FragmentStatePagerAdapter 和 FragmentPagerAdapter 的区别  
FragmentPagerAdapter 在 destroyItem 方法中, 只执行 FragmentTraction#detach 方法;  
FragmentStatePagerAdapter 在 destroyItem 中, 会把 list 中的 Fragment 置空, 再执行 FragmentTraction#remove 方法;  


### 切换横竖屏  
由竖屏切换成横屏  
```
onPause  
onSaveInstanceState
onStop
onDestroy
onCreate  
onStart
onRestoreInstanceState
onResume
```
由横屏切换成竖屏  
```
onPause
onSaveInstanceState 
onStop
onDestroy
onCreate
onStart
onRestoreInstanceState  
onResume
```
### 切换横竖屏#Fragment  
```
AFragment#onPause
BFragment#onPause
AActivity#onPause

AFragment#onSaveInstanceState
BFragment#onSaveInstanceState
AActivity#onSaveInstanceState

AFragment#onStop
BFragment#onStop
AActivity#onStop

AFragment#onDestroyView
AFragment#onDetach
BFragment#onDestroyView
BFragment#onDetach
AActivity#onDestroy

AFragment#onAttach
BFragment#onAttach

AActivity#onCreate

AFragment#onCreateView
AFragment#onViewCreated
AFragment#onActivityCreated

BFragment#onCreateView
BFragment#onViewCreated
BFragment#onActivityCreated

AFragment#onStart
BFragment#onStart
AActivity#onStart

AActivity#onRestoreInstanceState

AActivity#onResume
AFragment#onResume
BFragment#onResume
```
### 可以通过在清单文件的 Activity 中指定如下属性  
android:configChanges = "orientation|screenSize"    
```
onCreate...  
onStart...  
onResume..  
```
由竖屏切换成横屏  
```
AFragment#onConfigurationChanged  
BFragment#onConfigurationChanged
AActivity#onConfigurationChanged
```
由横屏切换成竖屏  
```
AFragment#onConfigurationChanged  
BFragment#onConfigurationChanged
AActivity#onConfigurationChanged
```

### 屏幕锁定下的生命周期  
屏幕锁定
```
MainActivity            onCreate
MainActivity            onStart
MainActivity            onResume
//  这个时候按下, 电源键-锁屏 
MainActivity            onPause
MainActivity            onStop
```
屏幕解锁  
```
MainActivity            onRestart
MainActivity            onStart
MainActivity            onResume
```


### onDestroy 一定会执行吗  
假设当前在页面是 AActivity, 这个时候如果突然强杀 app, 那么 AActivity 的 onStop 和 onDestroy 一定不会执行的;  
