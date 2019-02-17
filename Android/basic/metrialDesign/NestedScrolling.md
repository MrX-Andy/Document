### NestedScrolling  
作为子view                                           作为父view
startNestedScroll                                 onStartNestedScroll、onNestedScrollAccepted  
dispatchNestedPreScroll                  onNestedPreScroll  
dispatchNestedScroll                         onNestedScroll  
stopNestedScroll                                 onStopNestedScroll  


★先讲 套路  
父控件 实现NestedScrollingParent接口 并重写，  
NestedScrollingParentHelper 是嵌套滑动中，父控件接口的代理实现  
子控件 实现NestedScrollingChild接口 并重写  
NestedScrollingChildHelper 是嵌套滑动中，子控件接口的代理实现  

★ 注意点  
在嵌套滑动中的一些规则：子控件是嵌套滑动的发起者，父控件是嵌套滑动的处理者  
在使用调用嵌套滑动相关的方法时，应该总是使用：ViewCompat，ViewGroupCompat， ViewParentCompat的静态方法来实现兼容  
应该使用NestedScrollingParentHelper或NestedScrollingChildHelper的对应方法  
   来实现NestedScrollingParent或NestedScrollingChild接口中的方法。  

★ 大致流程  
在一个可以滑动的子控件中开启嵌套滑动，调用 setNestedScrollingEnabled  

如果要开始 一次嵌套滑动，在子控件需要滑动的时候例如ACTION_DOWN的时候就要调用  
startNestedScroll(ViewCompat.SCROLL_AXIS_HORIZONTAL);  
startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);  
startNestedScroll(ViewCompat.SCROLL_AXIS_HORIZONTAL | ViewCompat.SCROLL_AXIS_VERTICAL);  
// startNestedScroll(ViewCompat.SCROLL_AXIS_HORIZONTAL & ViewCompat.SCROLL_AXIS_VERTICAL);  
方法来告诉父控件自己要开始滑动了，这时父控件的onStartNestedScroll方法将会被回调，  

在子控件开始滑动之前，应该先问父控件，父控件是否需要先滑动，也就是调用 dispatchNestedPreScroll 方法，  
这个方法接收四个参数：  
dxConsumed 表示子控件此次滑动期间将要消耗的水平方法的距离  
dyConsumed 表示子控件此次滑动期间将要消耗的垂直方法的距离  
consumed 这个数组传递给父控件，如果父控件要先行滑动，  
   将会把消耗的距离通过此数据返回给子控件  
offsetInWindow 父控件先完成一个滑动后， 子控件在窗口中的偏移值。  
上面参数可以理解为：dxConsumed和dyConsumed是总的滑动值，传给父控件，如果父控件需要滑动有消耗掉一些距离，  
   然后把消耗的距离放在consumed中，返回给子控件，是根据父控件消耗的距离重新计算自己需要滑动的距离，进行滑动。  
   这个过程发送在父控件的onNestedPreScroll方法中。  

子控件在根据dispatchNestedPreScroll的返回值，然后计算被父view消耗的距离，根据需要位置  

子view重新计算自己的滑动距离进行滑动之后，需要调用dispatchNestedScroll方法，此方法接收五个参数  
int dxConsumed 子view在滑动中水平方向消耗的距离  
int dyConsumed 子view在滑动中垂直方向消耗的距离  
int dxUnconsumed 子view在滑动中水平方向没有消耗的距离  
int dyUnconsumed 子view在滑动中垂直方向没有消耗的距离  
int[] offsetInWindow 返回值。父view完成一个滑动后子view在窗口中的偏移值。  

在完成一系列滑动后，如果需要停止滑动，则子view调用stopNestedScroll然后父view的onStopNestedScroll方法被回调  


如果父view返回true表示配合此次嵌套滑动，并且父view的onNestedScrollAccepted被调用  
父view会收到onStartNestedScroll这个方法的回调从而决定是不是要配合子view做出响应。  
在滑动事件产生但是子控件还没处理前可以调用dispatchNestedPreScroll(0,dy,consumed,offsetInWindow)  
这个方法把事件传给父view这样父view就能在onNestedPreScroll方法里面收到子view的滑动信息，  
然后做出相应的处理把处理完后的结果通过consumed传给子view。  

同样的道理，如果父view需要在子view滑动后处理相关事件的话  
可以在子view的事件处理完成之后调用dispatchNestedScroll然后父view会在onNestedScroll收到回调。  
最后，滑动结束，调用onStopNestedScroll()表示本次处理结束。  

NestedScrollingParent 简称 NP  
NestedScrollingChild 简称 NC  

NC 产生一个 touch 事件，调用 startNestedScroll，表示开始分享出去 touch 事件， NP 的 onStartNestedScroll 判断是否需要跟 NC 配合，返回 true 表示接受  
NC 调用 dispatchNestedPreScroll ，通知 NP 将要进行一个 Touch 事件，如移动 5个像素，NP 的 onNestedPreScroll 中收到通知，知道 NC 要准备移动5个像素，这时候 NP 正好也需要滑动 2 个像素，然后在 onNestedPreScroll 的参数 consumed[] 数组对应的方向（下标为0的x轴，下标为1的y轴）赋值 2，表示消费 2 个像素  
NC 拿到 consumed[] 数组，知道 NP 消费了 2 个像素，剩下了 3 个像素，然后自己根据需要再移动 3 个像素的距离，如果这时候 NC 由于某个原因只移动了 2 个像素，那么剩下的 1 个像素距离会调用 dispatchNestedScroll 给 NP，NP 在 onNestedScroll 处理剩下的未消费的 1 像素。最后，NC 调用 stopNestedScroll ，最后 NP 调用 onStopNestedScroll结束。  

同 NestedScroll 类似的还有一套 NestedFling 操作，整体流程类似，NC 快速滑动产生一个 fling 事件，处理流程 NC.dispatchNestedPreFling -> NP.onNestedPreFling -> NC.dispatchNestedFling -> NP.onNestedFling  

在 NestedScroll 这一套流程中，NestedScrollingChild 不用考虑怎么去通知父布局如何滚动，只需要负责自己的事情：产生滚动事件，并将事件共享出去，  
产生 fling 事件，共享 fling 。  

NC 调用 startNestedScroll() 通知 NP 的 onStartNestedScroll （具体是怎么通知到 NP 的参考 源码）  

NC 产生一个 Touch 事件，如滚动 5 个像素，然后调用 dispatchNestedPreScroll 通知 NP 自己要滚动 5个像素， NP 收到通知后根据自身需要进行消费，  
如消费了 2 个像素，然后将结果通知到 NC  

NC 将剩下的 3 个像素进行自身消费，如此时滚动了 2 个像素到达了边界，还剩下 1 个像素没有消费掉，然后就调用 dispatchNestedScroll   
将剩下的未消费的 1 个像素共享出去。  



Fling 事件类似 Scroll 事件  
```
NestedScrollingChild 
public interface NestedScrollingChild {
dispatchNestedFling
    /**
     * 在嵌套滑动的子View fling之后再调用该函数向父View汇报fling情况。
     *
     * @param velocityX 水平方向的速度
     * @param velocityY 垂直方向的速度
     * @param consumed  true 如果子View fling了, false 如果子View没有fling
     * @return true 如果父View fling了
     */
    @Override
boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed);
dispatchNestedPreFling
    /**
     * 在嵌套滑动的子View fling之前告诉父View fling的情况。
     *
     * @param velocityX 水平方向的速度
     * @param velocityY 垂直方向的速度
     * @return 如果父View fling了
     */
    @Override
boolean dispatchNestedPreFling(float velocityX, float velocityY);
dispatchNestedPreScroll
    /**
     * 在嵌套滑动的子View滑动之前，告诉父View滑动的距离，让父View做相应的处理。
     *
     * @param dx             告诉父View水平方向需要滑动的距离
     *                       表示view本次x方向的滚动的总距离长度
     * @param dy             告诉父View垂直方向需要滑动的距离
     *                       表示view本次y方向的滚动的总距离长度
     * @param consumed
     * 表示父布局消费的距离,consumed[0]表示x方向,consumed[1]表示y方向
     * 
     * 如果不是null, 则告诉子View父View滑动的情况， consumed[0]父View告诉子View水平方向滑动的距离(dx)
     *                       consumed[1]父View告诉子View垂直方向滑动的距离(dy).
     * @param offsetInWindow
     * 表示剩下的距离dxUnconsumed和dyUnconsumed使得view在父布局中的位置偏移了多少
     *
     * 可选 length=2的数组，如果父View滑动导致子View的窗口发生了变化（子View的位置发生了变化）
     *                       该参数返回x(offsetInWindow[0]) y(offsetInWindow[1])方向的变化
     *                       如果你记录了手指最后的位置，需要根据参数offsetInWindow计算偏移量，才能保证下一次的touch事件的计算是正确的。
     * @return true 父View滑动了，false 父View没有滑动。
     */
    @Override
boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow);
dispatchNestedScroll
    /**
     * 在嵌套滑动的子控件滑动之后再调用该函数向父View汇报滑动情况。
     *
     * @param dxConsumed     子View水平方向滑动的距离
     * @param dyConsumed     子View垂直方向滑动的距离
     * @param dxUnconsumed   子View水平方向没有滑动的距离
     *                       表示滚动产生的x滚动距离还剩下多少没有消费
     * @param dyUnconsumed   子View垂直方向没有滑动的距离
     *                       表示滚动产生的y滚动距离还剩下多少没有消费
     * @param offsetInWindow
     * 表示剩下的距离dxUnconsumed和dyUnconsumed使得view在父布局中的位置偏移了多少
     *
     * 出参 如果父View滑动导致子View的窗口发生了变化（子View的位置发生了变化）
     *                       该参数返回x(offsetInWindow[0]) y(offsetInWindow[1])方向的变化
     *                       如果你记录了手指最后的位置，需要根据参数offsetInWindow计算偏移量，
     *                       才能保证下一次的touch事件的计算是正确的。
     * @return true 如果父View有滑动做了相应的处理, false 父View没有滑动.
     */
    @Override
boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow);
hasNestedScrollingParent
    /**
     * 是否有父View 支持 嵌套滑动,  会一层层的往上寻找父View
     *
     * @return
     */
    @Override
boolean hasNestedScrollingParent();
isNestedScrollingEnabled
    /**
     * 是否允许嵌套滑动
     *
     * @return
     */
    @Override
boolean isNestedScrollingEnabled();
setNestedScrollingEnabled
    /**
     * 设置是否允许嵌套滑动
     *
     * @param enabled true表示view使用嵌套滚动,false表示禁用.
     */
    @Override
void setNestedScrollingEnabled(boolean enabled);
startNestedScroll
    /**
     * 告诉开始嵌套滑动流程，调用这个函数的时候会去找嵌套滑动的父控件。
     * 如果找到了父控件并且父控件说可以滑动就返回true，否则返回false
     * 一般ACTION_DOWN里面调用
     *
     * @param axes:支持嵌套滚动轴。水平方向，垂直方向，或者不指定
     *            表示滚动的方向如:ViewCompat.SCROLL_AXIS_VERTICAL(垂直方向滚动)和
     *            ViewCompat.SCROLL_AXIS_HORIZONTAL(水平方向滚动)
     * @return true 父控件说可以滑动，false 父控件说不可以滑动
     * true表示本次滚动支持嵌套滚动,false不支持
     *
     */
    @Override
boolean startNestedScroll(int axes);
stopNestedScroll
    /**
     * 停止嵌套滑动流程(一般ACTION_UP里面调用)
     */
    @Override
void stopNestedScroll();
}
NestedScrollingParent 
/**
 * 当你希望自己的自定义布局支持嵌套子视图并且处理滚动操作，就可以实现该接口。
 * 实现这个接口后可以创建一个 NestedScrollingParentHelper 字段，使用它来帮助你处理大部分的方法。
 * 处理嵌套的滚动时应该使用  `ViewCompat`，`ViewGroupCompat`或`ViewParentCompat` 中的方法来处理，这是一些兼容库，
 * 他们保证 Android 5.0之前的兼容性垫片的静态方法，这样可以兼容 Android 5.0 之前的版本。
 */
public interface NestedScrollingParent {
onStartNestedScroll
    /**
     * 有嵌套滑动到来了，问下 该 父控件 是否接受嵌套滑动
     *
     * @param child            嵌套滑动对应的父类的子类(因为嵌套滑动对于的父View不一定是一级就能找到的，可能挑了两级父View的父View，child的辈分>=target)
     * @param target           具体嵌套滑动的那个子类
     * @param nestedScrollAxes 支持嵌套滚动轴。
     *                         水平方向（ViewCompat.SCROLL_AXIS_HORIZONTAL），
     *                         垂直方向（ViewCompat.SCROLL_AXIS_VERTICAL），
     *                         或者不指定（ViewCompat.SCROLL_AXIS_NONE）
     * @return 是否接受该嵌套滑动
     */
public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes);
onNestedScrollAccepted
    /**
     * 该 父View接受了嵌套滑动的请求该函数调用。onStartNestedScroll返回true该函数会被调用。
     * 参数和onStartNestedScroll一样
     */
public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes);
onStopNestedScroll
    /**
     * 停止嵌套滑动
     * @param target 具体嵌套滑动的那个子类
     */
public void onStopNestedScroll(View target);
onNestedScroll
    /**
     * 嵌套滑动的子View在滑动之后报告过来的滑动情况
     *
     * @param target 具体嵌套滑动的那个子类
     * @param dxConsumed 水平方向嵌套滑动的子View滑动的距离(消耗的距离)
     * @param dyConsumed 垂直方向嵌套滑动的子View滑动的距离(消耗的距离)
     * @param dxUnconsumed 水平方向嵌套滑动的子View未滑动的距离(未消耗的距离)
     * @param dyUnconsumed 垂直方向嵌套滑动的子View未滑动的距离(未消耗的距离)
     */
public void onNestedScroll(View target, int dxConsumed, int dyConsumed,  int dxUnconsumed, int dyUnconsumed);
onNestedPreScroll
    /**
     * 在嵌套滑动的子View未滑动之前告诉过来的准备滑动的情况
     * 子类滑动事件分发回调dispatchNestedPreScroll
     *
     * @param target   具体嵌套滑动的那个子类
     * @param dx       水平方向嵌套滑动的子View想要变化的距离（向右 > 0）
     * @param dy       垂直方向嵌套滑动的子View想要变化的距离（向下 > 0）
     * @param consumed 这个参数要我们在实现这个函数的时候指定，回头告诉子View当前父View消耗的距离
     *                 consumed[0] 水平消耗的距离，consumed[1] 垂直消耗的距离 好让子view做出相应的调整
     */
public void onNestedPreScroll(View target, int dx, int dy, int[] consumed);
onNestedFling
    /**
     * 嵌套滑动的子View在fling之后报告过来的fling情况
     * @param target 具体嵌套滑动的那个子类
     * @param velocityX 水平方向速度
     * @param velocityY 垂直方向速度
     * @param consumed 子view是否fling了
     * @return true 父View是否消耗了fling
     */
public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed);
onNestedPreFling
    /**
     * 在嵌套滑动的子View未fling之前告诉过来的准备fling的情况
     * @param target 具体嵌套滑动的那个子类
     * @param velocityX 水平方向速度
     * @param velocityY 垂直方向速度
     * @return true 父View是否消耗了fling
     */
public boolean onNestedPreFling(View target, float velocityX, float velocityY);
getNestedScrollAxes
    /**
     * 子类滑动事件分发回调
     *
     * @see ViewCompat#SCROLL_AXIS_HORIZONTAL 垂直
     * @see ViewCompat#SCROLL_AXIS_VERTICAL 水平
     * @see ViewCompat#SCROLL_AXIS_NONE 都支持
     */
public int getNestedScrollAxes();
}

```

### 参考  
http://blog.csdn.net/x87648510/article/details/51882040  
http://sanwen8.cn/p/330he2T.html  
http://renyuan-1991.iteye.com/blog/2262643  
http://blog.csdn.net/wuyuxing24/article/details/51112645  
http://www.objsp.com/code/source_nested.html  

demo http://blog.csdn.net/dingding_android/article/details/52948379?locationNum=2&fps=1  
demo http://blog.csdn.net/lmj121212/article/details/52974427  
demo（第一看） http://blog.csdn.net/zty19901005/article/details/50974388  
zhangke3016  http://www.jianshu.com/p/5ffb37226e72  

