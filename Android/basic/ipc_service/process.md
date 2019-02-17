### 进程优先级

前台进程, 可见进程, 服务进程, 后台进程, 空进程;  

❀ 前台进程#Foreground process  
该进程拥有, 与用户正在交互的 Activity, 已调用 onResume, 但未执行 onPause 方法;  
该进程拥有, 某个 Service, 且绑定到当前 Activity;  
该进程拥有, 某个 ContentProvider, 与当前 Activity 进行通信;  
该进程拥有, 某个 Service 调用了 startForeground();  

该进程拥有, 正执行 onCreate, onStartCommand,  onDestroy 等方法的 Service;  
该进程拥有, 正执行 onReceive 方法的 BroadcastReceiver;  
(这样做确保了这些组件的操作是有效的原子操作，每个组件都能执行完成而不被杀掉);  


❀ 可见进程#Visible process  
前台的 activity, 启动了一个 对话框样式的 activity, 或者透明的 activity, 例如运行时权限的 activity;  
当前 activity 执行了 onPause 方法, 这样的 activity 是可见 activity;  
该进程拥有, 某个可见 activity, 或者拥有与可见 activity 通信的 service 或者 contentProvider, 不在前台, 用户依然可见, 比如输入法;  

❀ 服务进程#Service process  
通过 startService 启动的服务, 看作后台服务;  
该进程拥有, 后台服务, 例如 Email 服务, 下载服务;  

❀ 后台进程#Background process  
当用户在前台 activity, 按了 home 键, 导致 onStop 方法被调用, 该 activity 退到了后台;  
该进程拥有, 后台 activity, 系统不会立刻杀死他们, 而会保留一段时间;  

❀ 空进程#Empty process  
在进程层次中, 如果不属于以上类别, 那它就是空进程;  
保留这种进程的的唯一目的是用作缓存, 以缩短下次启动时间;  
如果有需要, 系统随时会杀死这些进程, 例如:  
按 back 返回的进程, 仅仅为了下次启动快速响应;  


### 参考  
https://juejin.im/post/5c2449bae51d456c0c572a50  
https://chinagdg.org/2016/01/%E7%94%9F%E8%BF%98%E6%98%AF%E6%AD%BB%EF%BC%9Fandroid-%E8%BF%9B%E7%A8%8B%E4%BC%98%E5%85%88%E7%BA%A7%E8%AF%A6%E8%A7%A3  

