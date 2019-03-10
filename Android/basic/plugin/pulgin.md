### class loader  
DexClassLoader: 可以加载文件系统上的 jar, dex, apk  
PathClassLoader: 可以加载 /data/app 目录下的 apk, 这也意味着, 它只能加载已经安装的 apk  
URLClassLoader: 可以加载 java 中的 jar,但是由于 dalvik 不能直接识别 jar, 所以此方法在 android 中无法使用, 尽管还有这个类  

### 参考  
http://weishu.me/2016/01/28/understand-plugin-framework-overview/  
http://weishu.me/2016/01/28/understand-plugin-framework-proxy-hook/  
http://weishu.me/2016/03/21/understand-plugin-framework-activity-management/  
http://weishu.me/2016/04/05/understand-plugin-framework-classloader/  
http://weishu.me/2016/04/12/understand-plugin-framework-receiver/  
http://weishu.me/2016/05/11/understand-plugin-framework-service/  
http://weishu.me/2016/07/12/understand-plugin-framework-content-provider/  
Android插件化开发指南  

https://github.com/ManbangGroup/Phantom  
https://github.com/tiann/understand-plugin-framework  
http://weishu.me/2016/01/28/understand-plugin-framework-overview/  
https://segmentfault.com/a/1190000004062866  
http://liuwangshu.cn/application/classloader/1-java-classloader-.html  
http://liuwangshu.cn/application/classloader/2-android-classloader.html
http://liuwangshu.cn/application/hotfix/1-code-repair.html  
https://www.jianshu.com/p/c58804962f73  
https://www.jianshu.com/p/a620e368389a  
https://www.jianshu.com/p/96a72d1a7974  
http://www.cnblogs.com/lanrenxinxin/p/4712224.html  
https://www.jianshu.com/p/704cac3eb13d  
http://weishu.me/2016/03/21/understand-plugin-framework-activity-management/  
https://juejin.im/post/5c1b20e26fb9a049c30b33d7  

废物  
https://juejin.im/post/5bf22bb5e51d454cdc56cbd5   
https://juejin.im/post/5c5100c1e51d4550f31755b6  


