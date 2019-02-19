自定义控件相关 基础知识   

### onLayout layout  
继承 View, 可以重写 onLayout 和 layout;  
继承 ViewGroup, 只可以重写 onLayout, layout 方法是 final 类型的,  不可以重写;  

onLayout 收到的是, 在父窗体的坐标;  
自定义 ViewGroup 需要在 onMeasure 里面调用 measureChildren 或者, 子 view.measure 方法, 否则, 子 view 的 onMeasure 不会被执行;  

### 渲染性能  
大多数用户感知到的卡顿等性能问题的最主要根源都是因为渲染性能。从设计师的角度，他们希望App能够有更多的动画，图片等时尚元素来实现流畅的用户体验。  
但是Android系统很有可能无法及时完成那些复杂的界面渲染操作。Android系统每隔16ms发出VSYNC信号，触发对UI进行渲染，  
如果每次渲染都成功，这样就能够达到流畅的画面所需要的60fps，为了能够实现60fps，这意味着程序的大多数操作都必须在16ms内完成。  
如果你的某个操作花费时间是24ms，系统在得到VSYNC信号的时候就无法进行正常渲染，这样就发生了丢帧现象。那么用户在32ms内看到的会是同一帧画面。  
用户容易在UI执行动画或者滑动ListView的时候感知到卡顿不流畅，是因为这里的操作相对复杂，容易发生丢帧的现象，从而感觉卡顿。  
有很多原因可以导致丢帧，也许是因为你的layout太过复杂，无法在16ms内完成渲染，有可能是因为你的UI上有层叠太多的绘制单元，还有可能是因为动画执行的次数过多。  
这些都会导致CPU或者GPU负载过重。  

### Why 60fps?  
我们通常都会提到60fps与16ms，可是知道为何会是以程序是否达到60fps来作为App性能的衡量标准吗？这是因为人眼与大脑之间的协作无法感知超过60fps的画面更新。  
12fps大概类似手动快速翻动书籍的帧率，这明显是可以感知到不够顺滑的。24fps使得人眼感知的是连续线性的运动，这其实是归功于运动模糊的效果。  
24fps是电影胶圈通常使用的帧率，因为这个帧率已经足够支撑大部分电影画面需要表达的内容，同时能够最大的减少费用支出。  
但是低于30fps是无法顺畅表现绚丽的画面内容的，此时就需要用到60fps来达到想要的效果，当然超过60fps是没有必要的。  
开发app的性能目标就是保持60fps，这意味着每一帧你只有16ms=1000/60的时间来处理所有的任务。  

参考  
http://hukai.me/android-performance-patterns/  

###  getWidth 与 getMeasuredWidth 
getMeasuredWidth 是measure阶段获得的View的原始宽度;  
getWidth 是layout阶段完成后，其在父容器中所占的最终宽度;  

### 修改ScrollView 的阴影效果  
```
在xml中添加：
//  删除android ScrollView边界阴影  
android:fadingEdge="none"
//  删除ScrollView拉到尽头（顶部、底部），然后继续拉出现的阴影效果  
android:overScrollMode="never"
 
scrollView.setHorizontalFadingEdgeEnabled(false);  
 
```
### merge  
使用merge标签, 可以减少过度绘制, 但是问题是, 如果不加上配置, 你看到的preview 是比较奇怪的;  
使用 tools:parentTag 属性, 可以解决这个问题;  
```
<?xml version="1.0" encoding="utf-8"?>
<merge xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    tools:ignore="HardcodedText"
    tools:parentTag="android.widget.RelativeLayout">

    <TextView
        android:id="@+id/title"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:paddingLeft="@dimen/padding_horizontal"
        android:paddingTop="48dp"
        android:paddingRight="@dimen/padding_horizontal"
        android:textColor="@color/text_color"
        android:textSize="32sp"
        android:textStyle="bold"
        tools:text="Marquee" />

    <TextView
        android:id="@+id/subtitle"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_below="@+id/title"
        android:paddingLeft="@dimen/padding_horizontal"
        android:paddingTop="4dp"
        android:paddingRight="@dimen/padding_horizontal"
        android:text="hello"
        android:textSize="16sp" />

    <Space
        android:layout_width="0dp"
        android:layout_height="24dp" />

</merge>
```

### 系统屏幕密度  
```
ldpi---120---240X320分辨率
mdpi---160---320X480分辨率
hdpi---240---480X800分辨率
xhdpi---320---720X1280分辨率
xxhdpi---480---1080X1920分辨率
```

### View#post  
```
override fun onResume() {
    super.onResume()
    
    //  0  
    LogTrack.w(recyclerView.measuredHeight)

    recyclerView.post {
        //  精确值  
        LogTrack.w(recyclerView.measuredHeight)
    }

    Handler().postDelayed(Runnable {
        //  0  
        //  和 delay 的时间, 有关系, 但是 delay 多久, 无法预测   
        LogTrack.w(recyclerView.measuredHeight)
    }, 10)
}
```
❀ 参考  
https://blog.csdn.net/scnuxisan225/article/details/49815269  
https://www.jianshu.com/p/7f5342ceba34  

### 参考  
https://juejin.im/post/5c0e1dca6fb9a049c30b12e9  
https://hk.saowen.com/a/185130ca1a868cf4f6746bc998c897fd3ec1d189930d53bf91d000bdcaa6da85  
https://hk.saowen.com/a/88c274f18d44b90a649a68fb39eba3b6ae1e03fd9469bc6177e6d8117f5c0088  

onMeasure  
https://huangtianyu.gitee.io/2018/01/03/自定义控件View之onMeasure调用时机源码分析  

为什么 onMeasure 执行多次  
https://blog.csdn.net/jewleo/article/details/39547631  

Android 屏幕刷新机制  
https://www.jianshu.com/p/0d00cb85fdf3   


