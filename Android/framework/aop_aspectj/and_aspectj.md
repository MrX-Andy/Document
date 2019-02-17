###  aspectj  

使用原生aspectj 配置建麻烦，还有不兼容问题，建议使用hugo插件  
[gradle配置 aspectj](library/and_build.md)  
[gradle配置 hugo](library/and_build_hugo.md)  


[切入点 基本知识](library/join_point.md)  
[切入点 示例介绍](library/jp_001.md)  

假设插入的方法是 fooA;  目标方法是 funA;  
call 和 execution  
execution  插入的 代码，在目标 方法  内部， 形成  
```
funA(){
    fooA();
    // 实际 方法体  
}
funA(){
    // 实际 方法体  
    fooA();
}
```
call 插入的 代码，在目标 方法  外部， 形成  
```
methodA(){
    fooA();
    funA();  
}
methodA(){
    funA();  
    fooA();
}
```

@Before("execution(* android.app.Activity.onCreate(..))")  
@Before("execution(* android.app.Activity.on*(..))")  
第一个『*』表示返回值，『*』表示返回值为任意类型  
第二个『*』来进行通配，几个『*』没区别。  



◆  参考  
https://www.jianshu.com/p/5c9f1e8894ec（不看了）  
https://www.jianshu.com/p/f90e04bcb326  
https://www.jianshu.com/p/27b997677149  
https://www.jianshu.com/p/6ccfa7b50f0e   
https://www.jianshu.com/p/dca3e2c8608a  
https://github.com/uPhyca/gradle-android-aspectj-plugin  
http://blog.csdn.net/woshimalingyi/article/details/51519851  
http://blog.csdn.net/weelyy/article/details/78987087  
http://blog.csdn.net/qq_25943493/article/details/52524573    
http://blog.csdn.net/innost/article/details/49387395  
https://www.jianshu.com/p/430f9ea1e80f  


https://github.com/JakeWharton/hugo  
https://github.com/HujiangTechnology/gradle_plugin_android_aspectjx  






