### aspectj  

[intellij gradle配置](/Android/framework/aop_aspectj/library/and_build.md)  
[aspect for android](/Android/framework/aop_aspectj/and_aspectj.md)  
[基础知识](aspectj/basic.md)  
execution：用于匹配方法执行的连接点；  
within：用于匹配指定类型内的方法执行；

### 依赖库的问题  
如果 app  依赖 tools  
那么需要在 app.gradle 和 tools.gradle 都配置上   
```
apply plugin: 'org.alex.plugin.aspectj'
```
   
  

◆  参考  
https://github.com/HujiangTechnology/gradle_plugin_android_aspectjx  
http://www.eclipse.org/aspectj/doc/released/runtime-api/index.html  
https://eclipse.org/aspectj/doc/released/progguide/index.html  

https://fernandocejas.com/2014/08/03/aspect-oriented-programming-in-android/  
http://jinnianshilongnian.iteye.com/blog/1415606  
http://blog.csdn.net/zhengchao1991/article/details/53391244  
http://blog.csdn.net/qwe6112071/article/details/50951720    
http://blog.51cto.com/lavasoft/172292  
http://www.cnblogs.com/yudy/archive/2012/03/22/2411175.html  
https://www.ibm.com/developerworks/cn/java/j-lo-springaopcglib/?spm=5176.100239.blogcont7104.5.xUDMTR  
