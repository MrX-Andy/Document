android jetpack;  
ConcurrentHashMap;  
公平锁-非公平锁-偏向锁-互斥锁  
HTTP-三次握手-四次挥手;  
HTTPS;  超级难  CA证书有什么用;  
thread 之间通信, 怎么通信;  
插件化-热更新;  
性能检测, 页面卡顿检测, 内存泄漏检测, 过度绘制检测;  
四大组件, 原理;  
view 测量-绘制原理;  
okHttp 怎么做的3次握手，什么时候做，socket有哪些api;  
除了 thread.start, 你还用过哪些 api, 对 join 有了解吗?  
message.obtain 源码, 怎么区分 队列中 消息池中的状态;  
ThreadHandler, IdlHandler;  
Glide 是不是后使用的任务, 优先执行? 

bundle 传值, 大小限制;  
HTTP, keep-Alive 怎么实现的 长连接 和 复用;  
onMeasure 测量模式的实现细节, parent 是 50 dp, child 是 100dp, 测量模式的实现细节, 最终呈现效果;  
UI 线程进行耗时操作, 会引起卡顿, 为什么会卡顿;  
FragmentPagerAdapter 缓存机制,  怎么做 Fragment 的缓存;  
清单文件注册的广播, 什么时候回生效;  
清单文件, 什么时候被解析, 什么时候生效;  
bindService 和 startService 的区别, 在 生命周期上的表现;  
IPC, 有哪些, binder 机制的了解有多少;  
有哪些 OOM, 除了 bitmap string+string, 还有别的么? 
thread 申请过多, 会不会造成OOM, 为什么?  
你有过哪些性能优化的案例, 能不能再说一些有深入性的案例;  
简单说一下 startActivity 的流程;  


LinkedHashMap, HashMap, ArrayMap 实现原理的区别;  
HashMap 什么时候会扩容, 怎么扩容的, 细节;  
HashMap hash 值重复的地方, 之前是链表存储, 为什么要做树化?  
红黑树, 你了解多少, 时间复杂度 O(lg n), 是怎么推导出来的;  

对象的比较, equals 和 hashCode, 如果, 只重写了 equals, 没有重写 hashCode, a1==a2, 这样的运算, 系统会怎么处理, 为什么?   
hashCode 的重写规则, 哪些时候, 会用到 hashCode, 简单说一下;  

ClassLoader 加载原理;  
### Java  
ConcurrentHashMap;  
什么年龄代, 用什么回收策略, 什么回收器;  
TreeMap;  
synchronized 实现的原理;  
ReentrantLock 原理;  
synchronized与ReentrantLock;  

synchronized 与 Lock的区别;  
为什么要有 工作内存 和 主存;  
String 为什么要设计成不可变的;  
泛型, 如果不想 类型擦除, 怎么办;  

说说你对Java反射的理解; 

内部类的作用;  
Java深拷贝和浅拷贝;  
Java finally与return执行顺序;  
谈谈classloader  
类加载机制;  
双亲委派模型;  
类加载器;  

责任链设计模式, 手写;  
https://github.com/crossoverJie/Java-Interview  

###  Android  
LinearLayout, FrameLayout, RelativeLayout 哪个效率高, 为什么;  


横竖屏切换, 会触发 Activity 什么生命周期;  

图片编解码, BitmapFactory.createBitmap 的时候, 就会涉及编解码;  
LinearLayout 包裹 A, B 两个 Button, 在 AButton 上按下, 一直移动, 手指扫过 BButton, 整个事件传递的过程, MoveEvent 的数值;  

如果是在 Thread 里面用 handler, 怎么初始化;  

intent 传值, 需要注意哪些;  

Parcelable 为什么要保持读写顺序一致;  

Intent 传值注意什么, 有什么限制, 为什么要用 bundle;  
数据量小, 适用移动端, 还有就是安全性;  


Jvm、Dalvik、Art和对比;  

Application 的 Context 和 Activity 的 Context;  

Service 的生命周期;  
BroadcastReceiver;  
Binder机制及底层实现;  
什么是AIDL 以及如何使用;  
BroadcastReceiver, LocalBroadcastReceiver 区别  

既然 RecyclerView 在很多方面能取代 ListView, Google 为什么没把 ListView 标记过时;  
```
ListView 采用的是 RecyclerBin 的回收机制在一些轻量级的 item 显示时效率更高;  
```
你用过MD, 你知道怎么定义一个Behavior吗?  
https://www.jianshu.com/p/82d18b0d18f4  
AlertDialog, popupWindow, 在 Activity 中的区别;  


ArrayMap<Int, Int>  
SparseArray<Int>  
SparseBooleanArray  
SparseIntArray  
SparseLongArray  
LongSparseArray<Int>  

多线程断点续传原理;  

EventBus;  
Https请求慢的解决办法, DNS, 携带数据, 直接访问IP;  

热修复;  
插件化;  
组件化;  

1.. IPC机制, Binder和匿名共享内存等;  
2.. 四大组件启动, 工作原理;  
3.. View系统, 绘制原理, 事件分发;  
4.. 动画框架, 原理;  
5.. 多线程机制, 消息机制 AsyncTask, Thread / Handler;  
6.. 系统启动过程, system_server启动过程;  
7.. Window系统, Window创建过程;  
8.. 资源管理系统, 资源加载机制等;  

### 项目相关(Android)  
RecyclerView 的 item 包含一个 横向的列表:  
1.. 如果 item 是 recyclerView , 上下滑动的时候, 会不会卡顿, 为什么;  
2.. 你打算, 怎么处理;  用 LinearLayout 通过 add 和 remove;  
2.. 你打算, 怎么处理;  用 LinearLayout 通过 visible 和 gone;  

图片加解码;  
如果 用列表页面, 播放视频, 怎么做, 可以保证流畅度  占用内存小;  
WebView 怎么解决加载缓慢, 和 缓存问题, 导致数据不能及时刷新;  


### 数据结构与算法  
时间复杂度, 推导;  
两个排序数组归并为一个;  
写出你所知道的排序算法及时空复杂度, 稳定性;  
求1000以内的水仙花数以及40亿以内的水仙花数;  
猫扑素数;  
单词反转; 
字符串压缩abbcddde,压缩后ab2cd3e;  
合并多个单有序链表（假设都是递增的）;   
深度优先, 广度优先;  
两个不重复的数组集合中, 求共同的元素;  

二叉树 遍历;  
二叉树 求 最小 公共节点;  
数组 合并;  
随机数组, 包含正负, 找最大子数组;  
一个有序数组, 把奇数放在左边, 偶数放在右边, 怎么实现;  
Stack max()  O(1),  怎么实现? 用一个辅助的 stack;  
实现 0-1000 的数组, 随机乱序, 怎么实现;  
4.找到链表倒数第k个节点
5.二分查找数组中的k, 输出第一个
1、用栈实现队列的功能;  
2、分别用递归和遍历实现二叉树的深度计算;  

1、单链表，输入一个value，比如3，那么把链表中小于等于3的值放在链表左边，其它放在右边
要求，元素相对位置不变，时间复杂度O（1）
2、检测一个二叉树是平衡二叉树  
[4, 5], [5, 8] [6, 10] [11, 15], [14, 15], [17, 18] 区间合并  [4,10] [11, 15] [17, 18]  
### 计算机相关  
进程 与 线程;  
输入一个字符串判断是不是合法的iP地址
常见编码方式;  
utf-8编码中的中文占几个字节; int型几个字节;  
RSA, AES加密;  
TCP 的3次握手和四次挥手;  
TCP 与 UDP的区别;  
HTTP 协议;
HTTP 1.0与1.1 的区别;  
HTTP 1.1与2.0 的区别;  
HTTP 与 HTTPS 的区别以及如何实现安全性;  
http://www.jianshu.com/p/ef892323e68f  
http 相关, 比较多 最好都看下
SSL九次握手;  

### 参考(简历模板)  

https://mdluo.github.io/blog/about-resume/  
https://github.com/geekcompany/ResumeSample/blob/master/android.md  
https://mp.weixin.qq.com/s?__biz=MzA4NTQwNDcyMA==&mid=402970472&idx=1&sn=b9738c66fb5750c2515d57357c01a83f&scene=21#wechat_redirect  
http://www.jianshu.com/p/99f890ef36d4?hmsr=toutiao.io&utm_medium=toutiao.io&utm_source=toutiao.io  
http://j.codekk.com/blogs/detail/5705bcdf4a38205862ef4770  
http://j.codekk.com/blogs/detail/5705bcdf4a38205862ef476f  

### 参考  
https://github.com/jwasham/coding-interview-university/blob/master/translations/README-cn.md    
https://github.com/francistao/LearningNotes   
https://github.com/zmywly8866/Worth-Reading-the-Android-technical-articles  
http://whuhan2013.github.io/blog/2016/05/31/job-find-summary/  
http://blog.csdn.net/chivalrousman/article/details/51553114  
https://github.com/suzeyu1992/repo  
https://github.com/karmalove/AndroidInterview  
https://github.com/JackyAndroid/AndroidInterview-Q-A  
http://www.jianshu.com/nb/3450453  
http://www.jianshu.com/p/1e415787efc1  
https://github.com/kdn251/interviews/blob/master/README-zh-cn.md  
http://blog.csdn.net/xiaole0313/article/details/62056612  
https://github.com/CyC2018/Interview-Notebook  
https://github.com/andreis/interview  
https://github.com/Mr-YangCheng/ForAndroidInterview  
https://github.com/Snailclimb/Java-Guide  
https://github.com/CyC2018/CS-Notes  
https://github.com/kdn251/interviews/blob/master/README-zh-cn.md   
https://github.com/Snailclimb/JavaGuide  

### 参考(Android)  
https://github.com/Mr-YangCheng/ForAndroidInterview  
https://github.com/Blankj/AndroidOfferKiller  
https://github.com/JackyAndroid/AndroidInterview-Q-A  
https://github.com/francistao/LearningNotes  
https://github.com/leerduo/InterviewQuestion  
https://github.com/Mr-YangCheng/ForAndroidInterview  
http://www.jianshu.com/p/89f19d67b348  
https://github.com/suzeyu1992/repo  
https://www.jianshu.com/p/735be5ece9e8  
https://github.com/Freelander/Android_Data  
http://www.jianshu.com/nb/3450453  
https://github.com/LRH1993/android_interview  
https://juejin.im/post/59e54b9051882578cb511f00  
http://blog.csdn.net/axi295309066/article/details/51275470  
https://www.jianshu.com/p/b5ba11275a6d  
https://www.jianshu.com/p/dfa6d4caedad  
https://www.jianshu.com/p/3df3d2974234    
https://www.jianshu.com/p/a22450882af2  
https://www.jianshu.com/p/fb815eaf628f  
https://juejin.im/post/5aa721936fb9a028d4443d8b  
http://blog.csdn.net/qian520ao/article/details/79601179  
https://github.com/MindorksOpenSource/android-interview-questions  
https://www.jianshu.com/p/c70989bd5f29  
https://github.com/AweiLoveAndroid/CommonDevKnowledge  
https://github.com/guoxiaoxing/android-interview  
https://juejin.im/post/5b97ab465188255c865e030a  
https://juejin.im/post/5b8f15e26fb9a01a031b12d9  
https://github.com/hadyang/interview  
https://www.jianshu.com/p/cf5092fa2694  
https://github.com/huannan/AndroidReview  
