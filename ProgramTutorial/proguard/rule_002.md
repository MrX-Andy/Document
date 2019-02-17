###### 常用不变设置（option）
```
# 代码混淆压缩比，在0和7之间，默认为5，一般不需要改
-optimizationpasses 5
# 混淆时不使用大小写混合，混淆后的类名为小写
# 这个是给Microsoft Windows用户的，因为ProGuard假定使用的操作系统是能区分两个只是大小写不同的文件名，
# 但是Microsoft Windows不是这样的操作系统，所以必须为ProGuard指定-dontusemixedcaseclassnames选项
# windows下的同学还是加入这个选项吧(windows大小写不敏感)
-dontusemixedcaseclassnames
# 指定不去忽略非公共的库的类，用于告诉ProGuard，不要跳过对非公开类的处理。
# 默认情况下是跳过的，因为程序中不会引用它们，
# 有些情况下人们编写的代码与类库中的类在同一个包下，并且对包中内容加以引用，此时需要加入此条声明。
-dontskipnonpubliclibraryclasses
# 指定不去忽略非公共的库的类的成员
-dontskipnonpubliclibraryclassmembers
# 不做预校验，preverify是proguard的4个步骤之一
# Android不需要preverify，去掉这一步可加快混淆速度
-dontpreverify
#不优化输入的类文件
-dontoptimize
#混淆时记录日志
# 有了verbose这句话，混淆后就会生成映射文件
# 包含有类名->混淆后类名的映射关系
# 然后使用printmapping指定映射文件的名称
-verbose
-printmapping priguardMapping.txt
# 指定混淆时采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不改变
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
# 保护代码中的Annotation不被混淆
# 保护代码中的Annotation不被混淆，这在JSON实体映射时非常重要，比如fastJson
-keepattributes *Annotation*,InnerClasses
# 保持泛型，避免混淆泛型，这在JSON实体映射时非常重要，比如fastJson
-keepattributes Signature
#抛出异常时保留代码行号，在异常分析中可以方便定位
-keepattributes SourceFile,LineNumberTable
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions
#把混淆类中的方法名也混淆了
-useuniqueclassmembernames
#优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification
#将文件来源重命名为“SourceFile”字符串
# 混淆过后，crash所在的文件名、行号都会被混淆。
# 这行的作用是将混淆后的文件来源名定为 “SourceFile” 这个常量。
-renamesourcefileattribute SourceFile
```

-ignorewarning



