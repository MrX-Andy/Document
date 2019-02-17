```
package org.alex.layoutmanager;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import org.alex.util.LogTrack;

/**
 * 作者：Alex
 * 时间：2016/12/19 17 47
 * 简述：
 */
@SuppressWarnings({"JavaDoc", "UnusedAssignment", "UnusedParameters", "unused", "FieldCanBeLocal"})
public class SimpleLayoutManager extends RecyclerView.LayoutManager {
    private int verticalScrollOffset = 0;
    private int totalHeight = 0;

    /**
     * 保存所有的Item的上下左右的偏移量信息
     */
    private SparseArray<Rect> rectSparseArray;
    private int left;
    private int top;
    private int right;
    private int bottom;
    /**
     * 屏幕可见的第一个View的Position
     */
    private int firstVisiblePosition;
    /**
     * 屏幕可见的最后一个View的Position
     */
    private int lastVisiblePosition;
    private int fillCount;
    private int currentPosition;

    public SimpleLayoutManager() {
        rectSparseArray = new SparseArray<>();
        totalHeight = 0;
        left = 0;
        top = 0;
        right = 0;
        bottom = 0;
        fillCount = 0;
    }

    /**
     * 测量并记录每个item的信息
     *
     * @param recycler
     * @param state
     */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        /*如果没有item，直接返回*/
        if (getItemCount() <= 0) {
            detachAndScrapAttachedViews(recycler);
            return;
        }
        /*跳过 preLayout，preLayout 主要用于支持动画*/
        if (state.isPreLayout()) {
            return;
        }
        /* onLayoutChildren 方法在 RecyclerView 初始化时 会执行两遍  系统 就是这样的*/
        detachAndScrapAttachedViews(recycler);
        setVerticalScrollOffset(0);
        setFirstVisiblePosition(0);
        setLastVisiblePosition(getItemCount());
        fill(0, recycler, state);
    }

    /**
     * @param recycler
     * @param state
     * @param dy       手指 ↑为正； ↓ 为负
     * @return
     */
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        /*位移0、没有子View 当然不移动*/
        if (dy == 0 || getChildCount() == 0) {
            return 0;
        }
        /*实际滑动的距离， 可能会在边界处被修复*/
        int offset = dy;
        /*边界修复代码*/
        if (offset < 0 && getVerticalScrollOffset() + offset < 0) {
            /*上边界*/
            offset = -getVerticalScrollOffset();
        } else if (offset > 0) {
            /*下边界*/
            /*利用最后一个子View比较修正*/
            View lastChild = getChildAt(getChildCount() - 1);
            if (getPosition(lastChild) == getItemCount() - 1) {
                int gap = getHeight() - getPaddingBottom() - getDecoratedBottom(lastChild);
                if (gap > 0) {
                    offset = 0;
                } else if (gap == 0) {
                    offset = 0;
                } else {
                    offset = Math.min(offset, -gap);
                }
            }
        }
        /*先填充，再位移。*/
        fill(offset, recycler, state);
        /*滑动*/
        offsetChildrenVertical(-offset);
        return offset;
    }

    /**
     * 回收不需要的Item，并且将需要显示的Item从缓存中取出
     * 1 回收所有屏幕不可见的子View
     * 2 layout所有可见的子View
     *
     * @param dy 手指 ↑为正； ↓ 为负
     */
    @SuppressWarnings("UnnecessaryLocalVariable")
    private void fill(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int paddingTop = getPaddingTop();
        /*回收越界子View*/
        for (int i = getChildCount() - 1; getChildCount() > 0 && i >= 0; i--) {
            View child = getChildAt(i);
            int childDecoratedBottom = getDecoratedBottom(child);
            /*需要回收当前屏幕，上越界的View（视图从手机屏幕顶部跑出去了）*/
            if (dy > 0 && (childDecoratedBottom - dy < paddingTop)) {
                removeAndRecycleView(child, recycler);
                setFirstVisiblePosition(getFirstVisiblePosition() + 1);
            }
            int childDecoratedTop = getDecoratedTop(child);
            /*回收当前屏幕，下越界的View（视图从手机屏幕底部跑出去了）*/
            if (dy < 0 && (childDecoratedTop - dy > getHeight() - getPaddingBottom())) {
                removeAndRecycleView(child, recycler);
                setLastVisiblePosition(getLastVisiblePosition() - 1);
            }
        }
        int paddingLeft = getPaddingLeft();
        int maxHeight = 0;
        /*布局子 View 阶段*/
        int minPosition = getFirstVisiblePosition();
        setLastVisiblePosition(getItemCount() - 1);
        if (getChildCount() > 0) {
            View lastView = getChildAt(getChildCount() - 1);
            minPosition = getPosition(lastView) + 1;
            paddingTop = getDecoratedTop(lastView);
            //maxHeight = Math.max(maxHeight, getDecoratedMeasurementVertical(lastView));
            maxHeight = getDecoratedMeasurementVertical(lastView);
        }
       /*顺序addChildView*/
        for (int i = minPosition; i <= getLastVisiblePosition(); i++) {
            View child = recycler.getViewForPosition(i);
            addView(child);
            measureChildWithMargins(child, 0, 0);
            paddingTop += maxHeight;
            if (paddingTop - dy > getHeight() - getPaddingBottom()) {
                /*越界了 就回收*/
                removeAndRecycleView(child, recycler);
                setLastVisiblePosition(i - 1);
                maxHeight = 0;
                continue;
            }
            int right = paddingLeft + getDecoratedMeasurementHorizontal(child);
            int bottom = paddingTop + getDecoratedMeasurementVertical(child);
            layoutDecoratedWithMargins(child, paddingLeft, paddingTop, right, bottom);
            Rect rect = new Rect(paddingLeft, paddingTop + getVerticalScrollOffset(), right, bottom + getVerticalScrollOffset());
            rectSparseArray.put(i, rect);
            maxHeight = getDecoratedMeasurementVertical(child);
        }
        int maxPosition = getItemCount() - 1;
        setFirstVisiblePosition(0);
        if (getChildCount() > 0) {
            View firstView = getChildAt(0);
            maxPosition = getPosition(firstView) - 1;
            LogTrack.w(maxPosition);
        }

        /*在手指 ↓ 滑动 的时候 修正 子视图的位置*/
        for (int i = maxPosition; dy < 0 && i >= getFirstVisiblePosition(); i--) {
            Rect rect = rectSparseArray.get(i);
            //LogTrack.w(i + "  " + rect.top + "  " + rect.bottom + "  " + getVerticalScrollOffset() + "  " + dy + "  " + getPaddingTop());
            if (rect.bottom - getVerticalScrollOffset() - dy < getPaddingTop()) {
                //LogTrack.w("重新 标记 firstVisiblePosition = " + (i + 1));
                setFirstVisiblePosition(i + 1);
                break;
            }
            View child = recycler.getViewForPosition(i);
            //LogTrack.w("手指 ↓ 重新 添加 子视图 " + i + "  " + getPosition(child));
            addView(child, 0);
            measureChildWithMargins(child, 0, 0);
            layoutDecoratedWithMargins(child, rect.left, rect.top - getVerticalScrollOffset(), rect.right, rect.bottom - getVerticalScrollOffset());
        }
        LogTrack.i("childCount = " + getChildCount() + "  itemCount = " + getItemCount() + "  minPosition = " + minPosition + "  maxPosition = " + maxPosition + "  firstVisiblePosition = " + getFirstVisiblePosition() + "  lastVisiblePosition = " + getLastVisiblePosition() + "  paddingTop = " + paddingTop + "  maxHeight = " + maxHeight);
        setVerticalScrollOffset(getVerticalScrollOffset() + dy);
    }

    /**
     * @return true if there are more items in the data adapter
     */
    protected boolean hasMore(RecyclerView.State state) {
        return currentPosition >= 0 && currentPosition < state.getItemCount();
    }

    /**
     * 获取 子视图在 Y 轴方向上的 滚动空间
     */
    private int getVerticalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    /**
     * 获取 子视图在 X 轴方向上的 滚动空间
     */
    private int getHorizontalSpace() {
        return getWidth() - getPaddingRight() - getPaddingLeft();
    }

    /**
     * 获取某个childView在水平方向所占的空间
     *
     * @param view
     * @return
     */
    private int getDecoratedMeasurementHorizontal(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        return getDecoratedMeasuredWidth(view) + params.leftMargin + params.rightMargin;
    }


    /**
     * 测量一个子控件的高度，加上 ItemDecorations 和 margin产生的额外尺寸
     */
    @Override
    public int getDecoratedMeasuredHeight(View child) {
        return super.getDecoratedMeasuredHeight(child);
    }

    /**
     * 获取某个childView在竖直方向所占的空间
     *
     * @param view
     * @return
     */
    private int getDecoratedMeasurementVertical(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        return getDecoratedMeasuredHeight(view) + params.topMargin + params.bottomMargin;
    }

    /**
     * 得到一个 子视图
     *
     * @param index 仅仅包含所 展示出来的 子视图，并不包含 那些暂时被分离 或者 被废弃的子视图
     * @return
     */
    @Override
    public View getChildAt(int index) {
        return super.getChildAt(index);
    }

    /**
     * 得到 子视图在 RecyclerView 的下标 （包含所有的子视图）
     *
     * @param view
     * @return 子视图在 RecyclerView 的下标
     */
    @Override
    public int getPosition(View view) {
        return super.getPosition(view);
    }

    /**
     * 获取 RecyclerView里面，一共有多少个子视图   包含 那些暂时被分离 或者 被废弃的子视图
     *
     * @return 一共有多少个子视图
     */
    @Override
    public int getItemCount() {
        return super.getItemCount();
    }


    /**
     * 仅仅包含 展示出来的子控件， 并不包含 那些暂时被分离 或者 被废弃的子视图
     *
     * @return 仅仅包含 展示出来的子控件
     */
    @Override
    public int getChildCount() {
        return super.getChildCount();
    }

    private int getFirstVisiblePosition() {
        return firstVisiblePosition;
    }

    private void setFirstVisiblePosition(int firstVisiblePosition) {
        this.firstVisiblePosition = firstVisiblePosition;
    }

    private int getLastVisiblePosition() {
        return lastVisiblePosition;
    }

    private void setLastVisiblePosition(int lastVisiblePosition) {
        this.lastVisiblePosition = lastVisiblePosition;
    }

    private int getVerticalScrollOffset() {
        return verticalScrollOffset;
    }

    /**
     * 累加实际滑动距离
     */
    private void setVerticalScrollOffset(int verticalScrollOffset) {
        this.verticalScrollOffset = verticalScrollOffset;
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    /**
     * 指定 默认的 LayoutParams
     *
     * @return RecyclerView.LayoutParams
     */
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}

```
