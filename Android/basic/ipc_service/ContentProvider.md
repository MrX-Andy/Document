### ContentProvider  

内容提供者，是 Android 四大组件之一；  
内容提供者，只是适合于多进程模式下的，中间层、数据的搬运工，并不是数据源本身，数据源本身可能是db、file等；  
内容提供者，基于Binder实现；  

[ContentProvider初始化](ContentProvider/InitFun.md)  
[ContentProvider  Insert](ContentProvider/Insert.md)  


相关知识  
[统一资源标识符 URI](/ComputerScience/network/URI.md)   
[MIME数据类型](/ComputerScience/network/MIME.md)    
辅助类 ContentResolver  
辅助类 ContentUris  
辅助类 UriMatcher  
辅助类 ContentObserver  


### 原理  
ContentProvider 读取数据, 使用了匿名共享内存, CursorWindow 就是匿名共享内存;  




### 参考  
http://www.jianshu.com/p/ea8bc4aaf057  
http://blog.csdn.net/hehe26/article/details/51784355  
http://blog.csdn.net/liuhe688/article/details/7050868  
http://blog.csdn.net/dmk877/article/details/50387741  

uri  
http://blog.csdn.net/harvic880925/article/details/44679239  
http://www.cnblogs.com/fsjohnhuang/p/4280369.html  
http://blog.csdn.net/whyrjj3/article/details/7852800  

ContentObserver  
http://blog.csdn.net/qinjuning/article/details/7047607  


其他  
http://blog.csdn.net/coder_pig/article/details/47858489  
https://github.com/Triple-T/simpleprovider  

原理  
https://www.jianshu.com/p/37f366064b98  
https://www.jianshu.com/p/37f366064b98  
https://www.jianshu.com/p/9fdc894fb97c    
https://www.jianshu.com/p/3c81df444034  


