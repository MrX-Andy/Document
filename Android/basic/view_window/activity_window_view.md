### 对Activity、Window、View的认识  

[Activity、Window、View层级嵌套结构](../context/ImageFiles/awv_001.jpg)    

每一个Activity都包含了唯一一个PhoneWindow，这个就是Activity根Window；  
在它上面可以增加更多其他的Window，如dialog等；  

● 为什么要设计Activity、View、Window？  
View 是视图， window是面板，视图需要挂在到面板上；Activity负责管理面板的生命周期；  
[三者，是怎样关联的](awv_001.md)  
### Activity有存在的必要吗  

Window已经是系统管理的窗口界面。那么为什么还需要Activity呢？  
其实，本质上讲，我们要显示一个窗口出来，的确可以不需要Activity，悬浮窗口可以不依赖Activity；  
Android中的应用中，对各个窗口的管理相当复杂，包括窗口的入栈出栈，焦点问题，状态管理，内存回收等等问题，为了简化开发难度，引入Activity的概念；  


DecorView  
### Window  

一个Window对象代表一块显示区域，系统不关心Window里面具体的绘制内容，也不管Window怎么去绘制，  
只给Window提供可以在这块区域上绘制图形的Surface对象；  
换句话说，站在系统的角度上看，系统是“不知道”有View对象的；  
 
 

PhoneWindow  
WindowManagerService  
WindowManager  


● Window是什么？它的职能是什么？  
Activity 内部持有 PhoneWindow 对象， 
Activity 通过Window来管理View，Window通过addView()、removeView()、updateViewLayout()这三个方法来管理View的。  

● View跟Window有什么联系？  
View需要通过Window来展示在Activity上。  

● Activity、View、Window三者如何关联？  
[Activity包含了一个PhoneWindow](awv_003.md)  ，
View通过WindowManager的addView()、removeView()、updateViewLayout()对View进行管理。    
Window的添加过程以及Activity的启动流程都是一次IPC的过程。    
Activity的启动需要通过AMS完成；Window的添加过程需要通过WindowSession完成。  

WindowManager  
WindowManager为每个Window创建Surface对象，然后应用就可以通过这个Surface来绘制任何它想要绘制的东西。而对于WindowManager来说，这只不过是一块矩形区域而已。  

### 参考  
http://blog.csdn.net/huachao1001/article/details/51866287  
http://liuwangshu.cn/framework/wm/1-windowmanager.html  
http://liuwangshu.cn/framework/wm/2-window-property.html  
http://liuwangshu.cn/framework/wm/3-add-window.html  
http://liuwangshu.cn/framework/wms/1-wms-produce.html  
http://liuwangshu.cn/framework/wms/2-wms-member.html  
http://liuwangshu.cn/framework/wms/3-wms-remove.html  
https://blog.csdn.net/AndrExpert/article/details/81349343  

