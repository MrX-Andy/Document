### class loader  
[java class loader](../../../Java/jvm/class_loader/class_loader.md)  
DexClassLoader ：可以加载文件系统上的jar、dex、apk  
PathClassLoader ：可以加载/data/app目录下的apk，这也意味着，它只能加载已经安装的apk  
URLClassLoader ：可以加载java中的jar，但是由于dalvik不能直接识别jar，所以此方法在android中无法使用，尽管还有这个类  

### 参考  
https://juejin.im/post/5bf22bb5e51d454cdc56cbd5   
https://juejin.im/post/5c5100c1e51d4550f31755b6  
https://github.com/ManbangGroup/Phantom  
https://juejin.im/post/5c5bee986fb9a049bc4d1b58  
