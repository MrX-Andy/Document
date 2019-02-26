[自定义LayoutManager](library/layoutmanager.md)   
[对源码的认识](library/recycler_source.md)   

取消上下拉, 阴影效果  
```
recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);  
```

### 禁用刷新动画  
```
/**
 * 打开局部刷新动画
 */
public void openItemAnimator() {
    isDefaultAnimatorOpen = true;
    this.getItemAnimator().setAddDuration(120);
    this.getItemAnimator().setChangeDuration(250);
    this.getItemAnimator().setMoveDuration(250);
    this.getItemAnimator().setRemoveDuration(120);
    ((SimpleItemAnimator) this.getItemAnimator()).setSupportsChangeAnimations(true);
}

/**
 * 关闭局部刷新动画
 */
public void closeItemAnimator() {
    isDefaultAnimatorOpen = false;
    this.getItemAnimator().setAddDuration(0);
    this.getItemAnimator().setChangeDuration(0);
    this.getItemAnimator().setMoveDuration(0);
    this.getItemAnimator().setRemoveDuration(0);
    ((SimpleItemAnimator) this.getItemAnimator()).setSupportsChangeAnimations(false);
}
```  
### 参考  
http://www.cnblogs.com/dasusu/p/7746946.html  
ListView 与 RecyclerView 简单对比  
https://blog.csdn.net/shu_lance/article/details/79566189  

RecyclerView的拖拽怎么实现的  
https://blog.csdn.net/aiynmimi/article/details/77744610   
