### apt  
[Target](library/Target.md)  
[Element](library/Element.md)  
[Retention](library/Retention.md)  
[process](function/process.md)   
[ParameterSpec](library/ParameterSpec.md)  
[format](function/format.md)  
@Inherited  
该注解的字面意识是继承，但你要知道注解是不可以继承的。  
当你的注解定义到类A上，此时，有个B类继承A，且没使用该注解。但是扫描的时候，会把A类设置的注解，扫描到B类上。  
@SupportedAnnotationTypes  
在只有一到两个注解需要处理时，可以这样编写：  
```
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({"org.alex.apt.router.model.TargetClass",
        "org.alex.apt.router.model.ClassPath",
        "org.alex.apt.router.model.RemotePath"})
```


◆ 参考  
https://github.com/square/javapoet  
https://www.jianshu.com/p/4701538edd21  
http://blog.csdn.net/crazy1235/article/details/51876192  
https://joyrun.github.io/2016/07/19/AptHelloWorld/  
http://blog.csdn.net/u010405231/article/details/52210401  
https://www.race604.com/annotation-processing/  
https://www.jianshu.com/p/1910762593be  
https://juejin.im/post/5a311cf4f265da431c704dfe  
https://github.com/aitorvs/auto-parcel/blob/master/compiler/src/main/java/com/aitorvs/autoparcel/internal/codegen/AutoParcelProcessor.java  
https://juejin.im/post/5aa0e7eff265da2395308f48  
