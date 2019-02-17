```
package org.alex.widget.recycler.layoutmanager.library;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 作者：Alex
 * 时间：2018-11-12 10:42
 * 简述：
 */
@SuppressWarnings({"unused", "NullableProblems"})
public class BaseLayoutManager extends RecyclerView.LayoutManager {

    /**
     * 指定 默认的 LayoutParams，一般情况下，就用这个属性，就可以了
     * 首先，得将我们自定义的LayoutManager继承RecyclerView.LayoutManager，而RecyclerView.LayoutManager是一个抽象类，
     * 但是抽象方法只有一个generateDefaultLayoutParams也就是说，我们只需要重新这一个方法就可以自定义我们自己的LayoutManager啦~，
     */
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * 将View添加至RecyclerView中，
     * childIndex为0，但是View的位置还是由layout的位置决定，该方法在逆序layout子View时有大用
     */
    @Override
    public void addView(View child) {
        if (child == null) {
            return;
        }
        super.addView(child);
    }

    /**
     * 将上个方法detach的View attach回来
     */
    @Override
    public void attachView(View child) {
        if (child == null) {
            return;
        }
        super.attachView(child);
    }


    /**
     * 超级轻量回收一个View,马上就要添加回来
     */
    @Override
    public void detachView(View child) {
        if (child == null) {
            return;
        }
        super.detachView(child);
    }

    /**
     * 可以纵向  滑动
     */
    @Override
    public boolean canScrollVertically() {
        return true;
    }

    /**
     * 可以横向  滑动
     */
    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    /**
     * 获取 子视图在 Y 轴方向上的 滚动空间
     */
    protected int getVerticalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    /**
     * 获取 子视图在 X 轴方向上的 滚动空间
     */
    protected int getHorizontalSpace() {
        return getWidth() - getPaddingRight() - getPaddingLeft();
    }

    /**
     * 获取某个childView在竖直方向所占的空间，加上 ItemDecorations 和 margin产生的额外尺寸
     */
    protected int getDecoratedMeasuredVertical(View view) {
        if (view == null) {
            return 0;
        }
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        return getDecoratedMeasuredHeight(view) + params.topMargin + params.bottomMargin;
    }

    /**
     * 测量一个子控件的高度，加上 ItemDecorations 产生的额外尺寸
     */
    @Override
    public int getDecoratedMeasuredHeight(View child) {
        if (child == null) {
            return 0;
        }
        return super.getDecoratedMeasuredHeight(child);
    }

    /**
     * 获取某个childView在水平方向所占的空间，加上 ItemDecorations 和 margin产生的额外尺寸
     */
    private int getDecoratedMeasuredHorizontal(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        return getDecoratedMeasuredWidth(view) + params.leftMargin + params.rightMargin;
    }

    /**
     * 测量一个子控件的宽度，加上 ItemDecorations 产生的额外尺寸
     */
    @Override
    public int getDecoratedMeasuredWidth(View child) {
        if (child == null) {
            return 0;
        }
        return super.getDecoratedMeasuredWidth(child);
    }

    /**
     * 得到子控件的底部，到 parent 的顶部，的距离， 包含 ItemDecorations 所产生的距离
     */
    @Override
    public int getDecoratedBottom(View child) {
        if (child == null) {
            return 0;
        }
        return super.getDecoratedBottom(child);
    }

    /**
     * 得得到子控件的顶部，到 parent 的顶部，的距离， 包含 ItemDecorations 所产生的距离
     */
    @Override
    public int getDecoratedTop(View child) {
        if (child == null) {
            return 0;
        }
        return super.getDecoratedTop(child);
    }

    /**
     * 得到一个 子视图
     * 仅仅包含所 展示出来的 子视图，并不包含 那些暂时被分离 或者 被废弃的子视图
     */
    @Override
    public View getChildAt(int index) {
        return super.getChildAt(index);
    }

    /**
     * 得到 子视图在 RecyclerView 的下标 （包含所有的子视图）
     */
    @Override
    public int getPosition(View view) {
        if (view == null) {
            return RecyclerView.NO_POSITION;
        }
        return super.getPosition(view);
    }

    /**
     * 获取 RecyclerView里面，一共有多少个子视图   包含 那些暂时被分离 或者 被废弃的子视图；
     * 也就是说，adapter 的list 里面，就没有数据
     */
    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    /**
     * 仅仅包含 展示出来的子控件， 并不包含 那些暂时被分离 或者 被废弃的子视图
     */
    @Override
    public int getChildCount() {
        return super.getChildCount();
    }

    /**
     * 将ViewLayout出来，显示在屏幕上，内部会自动追加上该View的ItemDecoration和Margin。此时我们的View已经可见了
     */
    @Override
    public void layoutDecoratedWithMargins(View child, int left, int top, int right, int bottom) {
        if (child == null) {
            return;
        }
        super.layoutDecoratedWithMargins(child, left, top, right, bottom);
    }

    /**
     * 移除一个子视图，并使用Recycler 堆栈回收它
     * recycle真的回收一个View ，该View再次回来需要执行onBindViewHolder方法
     */
    @Override
    public void removeAndRecycleView(View child, RecyclerView.Recycler recycler) {
        if (child == null || recycler == null) {
            return;
        }
        super.removeAndRecycleView(child, recycler);
    }

    /**
     * @param dy 手指滑动向量值
     *           dy > 0 :  手指从下往上滑动 ↑ ；
     *           dy < 0 :  手指从上往下滑动 ↓ ；
     */
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return super.scrollVerticallyBy(dy, recycler, state);
    }

    /**
     * 垂直方向， 位移所有 子 view
     *
     * @param dy 手指滑动向量值
     *           dy > 0 :  手指从下往上滑动 ↑ ；
     *           dy < 0 :  手指从上往下滑动 ↓ ；
     */
    @Override
    public void offsetChildrenVertical(int dy) {
        super.offsetChildrenVertical(dy);
    }

    /*detach轻量回收所有View
     * 在布局之前，将所有的子View先Detach掉，放入到Scrap缓存中；
     * 回收器 会先从 Scrap 缓存中 重新使用 view， 再从 Recycled 缓存中 重新使用 view；
     * */
    @Override
    public void detachAndScrapAttachedViews(@NonNull RecyclerView.Recycler recycler) {
        super.detachAndScrapAttachedViews(recycler);
    }

    /**
     * detach轻量回收指定View
     * 分离 一个子控件 并把它 添加到 RecyclerView的 scrap 堆栈中，废弃一个子视图， 在更新数据之后重新使用
     */
    @Override
    public void detachAndScrapView(View child, RecyclerView.Recycler recycler) {
        if (child == null || recycler == null) {
            return;
        }
        super.detachAndScrapView(child, recycler);
    }
}

```