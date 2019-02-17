### 自定义LayoutManager  
主要涉及 RecyclerView、LayoutManager、RecyclerViewPool、Recycler  

### 学习自定义LayoutManager需要的铺垫知识    
RecyclerView的二级缓存概念  
androidx.recyclerview.widget.RecyclerView.Recycler#mRecyclerPool  
androidx.recyclerview.widget.RecyclerView.Recycler#mAttachedScrap  
androidx.recyclerview.widget.RecyclerView.Recycler#mChangedScrap  
androidx.recyclerview.widget.RecyclerView.Recycler#mCachedViews   
androidx.recyclerview.widget.RecyclerView.Recycler#mUnmodifiableAttachedScrap    
androidx.recyclerview.widget.RecyclerView.Recycler#tryGetViewHolderForPositionByDeadline   

### Detach vs. Remove  
布局更新时有两个方法处理已存在的子视图：detach 和 remove (分离和移除)；  
选择Datach方式，主要是在我们的代码执行结束之前，我们需要反复去将View移除并且马上又要添加进去时，  
比如：当我们对View进行重新排序的时候，可以选择Detach，因为屏幕上显示的就是这些position对应的View，  
我们并不需要重新去绑定数据，这明显可以提高效率，使用Detach方式可以通过函数 detachAndScrapView()实现。  
而使用 Remove 的方式，是当View不在屏幕中有任何显示的时候，你需要将它Remove掉，以备后面循环利用。可以通过函数 removeAndRecycleView()实现，  
通常来说，  
如果你想要临时整理并且希望稍后在同一布局中重新使用某个 view 的话， 可以对它调用 detachAndScrapView() 。  
如果基于当前布局 你不再需要某个 view 的话，对其调用 removeAndRecycleView()。    

### Scrap vs. Recycle  
RecyclerView 有两级视图缓存系统： scrap cache 和 recycle pool (废料区和回收池)，   
Scrap cache 是一个轻量的集合，视图可以不经过 adapter 直接返回给 LayoutManager 。通常被 detach 但会在同一布局重新使用的视图会临时储存在这里。  
Recycle pool 存放的 是那些没有通过 position 获取过的视图， 因此它们都要经过适配器重新绑定后才能返回给 LayoutManager。  
当要给 LayoutManager 提供一个新 view 时，Recycler 首先会 检查 scrap heap 有没有对应的 position/id；  
如果有对应的内容， 就直接返回数据不需要通过适配器重新绑定。  
如果没有的话， Recycler 就会从 recycle pool 里造一个合适的视图出来，   
         然后用 adapter 给它绑定必要的数据，并且会回调Adapter的 onBindViewHolder 方法 (就是调用 RecyclerView.Adapter.bindViewHolder()) 再返回。   
如果 recycle pool 中也不存在有效 view ，就会在绑定数据前 创建新的 view ，还会调用onCreateViewHolder方法(就是 RecyclerView.Adapter.createViewHolder())， 最后返回数据。  



在自定义LayoutManager过程的第一步，onLayoutChildren()方法里，就类似于自定义ViewGroup的onLayout()方法。  
但与自定义LayoutManager相比，自定义ViewGroup是一种静态的layout 子View的过程，因为ViewGroup内部不支持滑动，  
所以只需要无脑layout出所有的View，便不用再操心剩下的事。   
而自定义LayoutManager与之不同，在第一步layout时，千万不要layout出所有的子View，这里也是网上一些文章里的错误做法，  
他们带着老思想，在第一步就layout出了所有的childView，这会导致一个很严重的问题：你的自定义LayoutManager = 自定义ViewGroup。  
即，他们没有View复用机制。   
在Adapter的onCreateViewHolder()方法里增加打印语句，如果你的数据源有100000条数据，那么在RecyclerView第一次显示在屏幕上时，  
onCreateViewHolder()会执行100000次，你就可以尽情的欣赏ANR了。   
反观使用官方提供的三种LayoutManager，开始时屏幕上有n少个ItemView，一般就执行n次onCreateViewHolder()，（也有可能多执行1次），  
在后续滑动时，大部分情况都只是执行onBindViewHolder()方法，不会再执行onCreateViewHolder()。  


自定义一个LayoutManager就自动复用ItemView了吗  
当然不是，实际上这是自定义LayoutManager的重头戏之一，要做到在合适的时机 回收 不可见的旧子View ，复用子View layout 新的子View，  
以及之前提及的在LayoutManager的初始化时合理布局可见数量的子View等，才算是复用了ItemView。   
注意，这里的回收是recycle，而不是detach。   
如果你只detach了ItemView，并没有recycle它们，它们会一直被保存在Recycler的 mAttachedScrap 里，它是一个ArrayList，保存了被detach但还没有recycle的ViewHolder。  
实际上Recycler内部的缓存机制远不止一个 mAttachedScrap  


用 RecyclerView 就等于 ItemView 复用  
显然也不是，除了上述的因素外，这里还有一个很大的误区：很多人认为使用了 RecyclerView，ItemView 就都回收复用了。   
这里出个题：基本上APP都有个 TopBanner 在，它放在RecyclerView里作为 HeaderView（通过特殊的 ItemViewType 实现），剩下都是普通的 ItemView，那么列表滚动，
当 Banner 早已不可见时，它的 View(ViewHolder) 会被回收、被其他 ItemView 复用吗？ 
答案：Banner 的 ViewHolder 会被回收，但该 ViewHolder 的内存空间 不会被释放 ， 不会被其他的 ItemView 复用。 
回收都好理解，在屏幕上不可见时，LayoutManager 会把它回收至 RecyclerViewPool 里。 
然而却不会给 normalItem 复用，因为它们的 ItemViewType 不同。 
所以它的内存空间不会被释放，将一直被 RecyclerViewPool 持有着，等待着需求相同 ItemViewType 的 ViewHolder 的请求到来。 
即，当页面滚动回顶部，显示 Banner 时，这个View会被复用。 

detach 和recycle的时机  
当你需要从一个可能再生的前子视图中 回收旧的 view 或者 获取新的 view 时， 你的 LayoutManager 可以访问一个 Recycler 实例。  
Recycler 也免掉了直接访问 view 当前适配器方法的麻烦。当你的 LayoutManager 需要一个新的子视图时，只要调用 getViewForPosition() 这个方法，  
Recycler 会决定到底是从头创建一个新的视图 还是重用一个已存在的废弃视图。   
你的 LayoutManager 需要及时将不再显示的视图传递给 Recycler，避免 Recycler 创建不必要的 view 对象。  

一个 View 只是暂时被清除掉，稍后立刻就要用到，使用 detach 方法，它会被缓存进 scrap cache 中。   
一个 View 不再显示在屏幕上，需要被清除掉，并且下次再显示它的时机目前未知 ，使用 remove 。  
它会被以 viewType 分组，缓存进 RecyclerViewPool 里。   
注意：一个 View 只被 detach，没有被 recycle 的话，不会放进 RecyclerViewPool 里，会一直存在 recycler 的 scrap 中。  

我们要注意什么？  
答：即使是在写 onLayoutChildren()方法时，也要考虑将屏幕上的 View（如果有），detach 掉，否则屏幕初始化时，同一个 position 的 ViewHolder，  
也会 onCreateViewHolder 两次。因此 childCount 也会翻倍。  


先讲套路
指定默认的LayoutParams		generateDefaultLayoutParams
测量并记录每个item的信息			onLayoutChildren
回收以及放置各个item			detachAndScrapAttachedViews(recycler);
竖直滚动需要 重写canScrollVertically()和scrollVerticallyBy()  


简单描述自定义的流程  
计算每个Item的位置，并对Item布局。重写 onLayoutChildren()方法  
处理滑动事件（包括横向和竖向滚动、滑动结束、滑动到指定位置等）  
1.. 横向滚动：重写 scrollHorizontallyBy()方法  
2.. 竖向滚动：重写 scrollVerticallyBy()方法  
3.. 滑动结束：重写 onScrollStateChanged()方法  
4.. 指定滚动位置：重写 scrollToPosition()和 smoothScrollToPosition()方法    
重用和回收 Item  
重设 Adapter 重写 onAdapterChanged()方法  

### onLayoutChildren  在RecyclerView初始化时，会被调用两次  
参看RecyclerView源码，onLayoutChildren 会执行两次，一次RecyclerView的onMeasure() 一次onLayout()。  
第一次 RecyclerView 的onMeasure(),会调用dispatchLayoutStep2()方法，该方法内部会调用 mLayout.onLayoutChildren(mRecycler, mState);    
第二次 onLayout()方法会调用dispatchLayout();,该方法内部又调用了dispatchLayoutStep2();  
该方法是 LayoutManager的入口。它会在如下情况下被调用： 
1 在 RecyclerView初始化时，会被调用两次。 
2 在调用 adapter.notifyDataSetChanged()时，会被调用。 
3 在调用 setAdapter替换Adapter时,会被调用。   
4 在 RecyclerView执行动画时，它也会被调用。   
即 RecyclerView 初始化 、 数据源改变时 都会被调用。  

### 回收与复用  
第一级缓存 mCachedViews  
如果仍依赖于 RecyclerView ，比如已经滑动出可视范围，但还没有被移除掉，但已经被标记移除的 ItemView 集合会被添加到 mAttachedScrap 中；   
然后如果 mAttachedScrap 中不再依赖时会被加入到 mCachedViews 中；   
mChangedScrap 则是存储 notifXXX 方法时需要改变的 ViewHolder；  
第二级缓存  
ViewCacheExtension 是一个抽象静态类，用于充当附加的缓存池，当 RecyclerView 从第一级缓存找不到需要的 View 时，将会从 ViewCacheExtension 中找。  
不过这个缓存是由开发者维护的，如果没有设置它，则不会启用。  
通常我们也不会去设置他，系统已经预先提供了两级缓存了，除非有特殊需求，比如要在调用系统的缓存池之前，返回一个特定的视图，才会用到他。  
第三级缓存  
RecyclerView 缓存的是 ViewHolder，这也就是为什么在写 Adapter 的时候 必须继承一个固定的 ViewHolder 的原因；  
RecycledViewPool 实现上，是通过一个默认为 5 大小的 ArrayList 实现的；  

### 验收标准  
1.. 看 adapter  
onCreateViewHolder 执行的次数 和 一屏幕所见的item数量，差距不是很大；  
不停地滑动item，总是执行 onBindViewHolder，并不会执行 create；    
```
private int createCount = 0;
@Override
public SportCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LogTrack.w("create  " + (createCount++));
}

@Override
public void onBindViewHolder(final SportCardViewHolder holder, int position) {
    LogTrack.w("bind  " + position);
}
```
2.. 看 layoutManager  
childCount数量不应大于屏幕上显示的Item数量，而scrapCache缓存区域的Item数量应该是0.   

```
@Override
public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
    LogTrack.i("getChildCount = " + getChildCount() + "  getScrapList =  " + recycler.getScrapList().size());
}
```
官方的LayoutManager都是达标的，
这个也是达标的  https://github.com/Cleveroad/FanLayoutManager  
很多网上的demo，都是不达标的  

### 系统函数介绍  
[LayoutManager系统函数](BaseLayoutManager.md)  

findFirstVisibleItemPosition  
返回当前第一个可见 Item 的 position  

findFirstCompletelyVisibleItemPosition  
返回当前第一个完全可见 Item 的 position  

findLastVisibleItemPosition  
返回当前最后一个可见 Item 的 position  

findLastCompletelyVisibleItemPosition  
返回当前最后一个完全可见 Item 的 position  

### recycler 系统函数解释  

getViewForPosition  
```
/**
 * 找recycler要一个childItemView,我们不管它是从scrap里取，
 * 还是从RecyclerViewPool里取，亦或是onCreateViewHolder里拿。
 */
public View getViewForPosition(int position)
```

attachView  
将detachView(View child)回收的view拿回来  


detachAndScrapAttachedViews  
用指定的recycler临时移除所有添加的views  

detachAndScrapView  
用指定的recycler临时回收view  

detachView  
临时回收view  

getViewForPosition  
获取位置为position的View  
找recycler要一个childItemView,我们不管它是从scrap里取，还是从RecyclerViewPool里取，亦或是onCreateViewHolder里拿。  

getPosition  
获取view的位置  

layoutDecoratedWithMargins  
将child显示在RecyclerView上面，left，top，right，bottom规定了显示的区域  

measureChildWithMargins  
测量view的宽高，包括外边距  

offsetChildrenHorizontal  
offsetChildrenVertical
水平移动所有的view  

removeAndRecycleAllViews  
移除所有的view并且用给的recycler回收  

removeAndRecycleView  
移除指定的view并且用给的recycler回收  

### RecyclerView.State 
isPreLayout  
正在加载动画，做动画渲染  




