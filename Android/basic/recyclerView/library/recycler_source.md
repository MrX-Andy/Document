### 对 recyclerView 源码的认识  
RecyclerView, LayoutManager, Adapter, ViewHolder, ItemDecoration;  

recyclerView 还存在一级缓存 mScrapList, 是被 LayoutManager 持有, recycler是被RecyclerView持有;  
但是mScrapList其实一定程度上和动画有关;  
缓存的重头戏还是在RecyclerView中的内部类Recycler中;  
类的结构也比较清楚，这里可以清楚的看到我们后面讲到的四级缓存机制所用到的类都在这里可以看到：
1.. 一级缓存: mAttachedScrap  
2.. 二级缓存: mCacheViews  
3.. 三级缓存: mViewCacheExtension  
4.. 四级缓存: mRecyclerPool  

recycler.getViewForPosition(pos)  
1.. 从 mAttachedScrap 中通过匹配pos获取holder缓存, 如果成功, 得到 holder;  
2.. 从 mCacheViews 中通过匹配pos获取holder缓存, 如果成功, 得到 holder;  
3.. 从 mViewCacheExtensions 这个自定义缓存中, 获取holder缓存, 如果成功, 得到 holder;  

4.. 从 mRecyclerViewPool 中获取holder缓存, 如果成功, 得到 holder;  
       清除缓存 holder 所有的标志位,确保能重新 bindView;  
       调用 mAdapter.bindViewHolder(holder, pos);  

5.. 通过mAdapter.createViewHolder(parent, type)  创建holder;  
       调用mAdapter.bindViewHolder(holder, pos);  

第一次尝试, 从 mAttachedScrap 和 mCacheView 中  
根据 pos 从 mAttachedScrap 中获取 holder, 如果是对应当前 pos, 则为有效;  
如果无效, 则从  mAttachedScrap 中移除, 放在 mCacheViews 或者 mRecyclerPool 中, 继续下一个缓存, 如果下一个缓存中找到, 则从下一个缓存中移除, 并添加到 mAttachedScrap 缓存中;  

第二次尝试,  
第一次尝试, 对于 mAttachedScrap 和 mCacheViews 缓存, 是不区分 itemViewType 的, 第二次尝试, 流程和第一次基本一致, 多了对 viewType 的判断;  

第三次尝试  
从自定义 mViewCacheExtension 中获取 holder, 首先这个类基本上没有什么限制, 都由开发者自己决定, 

第四次尝试  
这次针对 mRecyclerPool 缓存, 支持多个 RecyclerView之间复用 View, 也就是说通过自定义Pool 我们甚至可以实现整个应用内的 RecyclerView 的 View 的复用;  
mRecyclerPool 内部使用 SparseArray, key就是 ViewType, 而 value 存放的是 ArrayList, 而默认的每个ArrayList的大小是5个;  
这里还有一个要注意的点就是 getRecycledView 这个方法可以看到拿到 holder 其实是通过 remove 拿到的, 也就是通过remove拿到的;  

总结:  
1.. RecyclerView 内部大体可以分为四级缓存: mAttachedScrap,mCacheViews,mViewCacheExtension,mRecyclerPool. 
2.. mAttachedScrap,mCacheViews 只是对 View 的复用, 并且不区分 type; ViewCacheExtension  RecycledViewPool 是对于 ViewHolder 的复用, 而且区分 type;  
3.. 如果缓存 ViewHolder 时发现超过了mCachedView 的限制, 会将最老的 ViewHolder(也就是 mCachedView 缓存队列的第一个 ViewHolder )移到 RecycledViewPool 中;  

mAttachedScrap的大小刚好为屏幕内可以显示的Item的数量, 不需要经过 bindHolder;  
mCachedViews 内的缓存在复用的时候不需要调用 bindHolder, 也就是在滑动的过程中, 免去了bind的过程, 提高滑动的效率;   

在自定义 LayoutManager 中:   
被 remove 的view, 对应的 holder, 会放在 mViewCacheExtension 或 mRecyclerPool 中;  
被 detach 的view, 对应的 holder, 会放在 mAttachedScrap 或 mCacheViews 中;  
因为 mAttachedScrap,mCacheViews 是对 view 的复用,  mViewCacheExtension, mRecyclerPool 是对 holder 的复用;  
mCacheViews 与 mAdapter 一致, 当 mAdapter 被更换时, mCacheViews 即被缓存至 mRecyclerPool 中, 上限是 2, 即被划出屏幕的两个 itemView;   
mViewCacheExtension 不直接使用, 默认不实现, 需要 RD 手动实现;  
mRecyclerPool 与自身生命周期一致, 不再被引用时, 被释放;  根据 viewType 存储, 默认支持5种 viewType;  

### dispatchLayout  
```

```
### 参考  
https://blog.csdn.net/sdfdzx/article/details/79981073  
https://segmentfault.com/a/1190000007331249  
https://blog.csdn.net/sdfdzx/article/details/79795174  
https://blog.csdn.net/sdfdzx/article/details/79981073  
