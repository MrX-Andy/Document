Activity 是一种交互型组件, 用于展示视图, 接收用户的输入和滑动等操作;  
Service 是一种计算型组件, 用于后台执行一些列的计算任务, 工作在后台, 用户无法感知它的存在;  
BroadcastReceiver 是一种消息型组件, 用于在不同组件, 或者在不同应用之间传递消息;  
ContentProvider 是一种数据共享型组件, 用于组件之间, 或者应用之间共享数据;  

### 对#Context的认识    
Application, Service 中的 Context, 即 getApplicationContext, 被当作应用;  
Activity 中的 Context, 即 activity.this, 被当作视图;  
Context 是一个抽象类, 类继承结构图:  
```
Context;    
    ContextImpl;  
    ContextWrapper;  
        Application;  
        Service;  
        ContextThemeWrapper;  
            Activity;  
```
具体的 context 的功能都是 ContextImpl 去实现的;  
在 Activity, Service 中  
getApplication();  
getApplicationContext();  
getBaseContext();  
其中, getApplication  与 getApplicationContext 是同一个对象, 都是 Application 的引用;   
getBaseContext 得到的是 ContextImpl 的引用;  


在 Fragment 中  
getContext();  
getActivity();  


在ContentProvider中  
getContext();  


在BroadcastReceiver中  
```
public void onReceive(Context context, Intent intent) {
    context.getApplicationContext();
}
```
 
![Context](ImageFiles/context_001.png)  


### 参考  
http://www.jianshu.com/p/46c35c5079b4     
http://www.jianshu.com/p/994cd73bde53    
http://www.jianshu.com/p/94e0f9ab3f1d  
