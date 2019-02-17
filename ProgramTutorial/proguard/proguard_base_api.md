###### 基本语法
```
# 防止类和成员被移除或者被重命名
-keep	
# 运行不显示；防止类和成员被重命名
-keepnames	
# 运行不显示；防止成员被移除或者被重命名
-keepclassmembers 
# 防止成员被重命名
-keepnames	
# 运行不显示
-keepclassmembernames 
# 防止拥有该成员的类和成员被移除或者被重命名
-keepclasseswithmembers	 
# 运行不显示 防止拥有该成员的类和成员被重命名
-keepclasseswithmembernames	
# 编译报错
-keepattributes 
# 编译报错
-keepdirectories 
#编译报错
-keepparameternames 
# 编译报错
-keeppackagenames 
```

> 保持元素不参与混淆的规则

```
具体的类
访问修饰符（public、protected、private）
通配符*，匹配任意长度字符，但不含包名分隔符(.)
通配符**，匹配任意长度字符，并且包含包名分隔符(.)
extends，即可以指定类的基类
implement，匹配实现了某接口的类
$，内部类
“成员”代表类成员相关的限定条件，它将最终定位到某些符合该限定条件的类成员。它的内容可以使用：

<init> 匹配所有构造器
<fields> 匹配所有域
<methods> 匹配所有方法
通配符*，匹配任意长度字符，但不含包名分隔符(.)
通配符**，匹配任意长度字符，并且包含包名分隔符(.)
通配符***，匹配任意参数类型
…，匹配任意长度的任意类型参数。比如void test(…)就能匹配任意 void test(String a) 或者是 void test(int a, String b) 这些方法。
访问修饰符（public、protected、private）
```

>  常用的自定义混淆规则
```
# 不混淆某个类
-keep public class name.huihui.example.Test { *; }
# 不混淆某个包所有的类
-keep class name.huihui.test.** { *; }
# 不混淆某个类的子类
-keep public class * extends name.huihui.example.Test { *; }
# 不混淆所有类名中包含了“model”的类及其成员
-keep public class **.*model*.** {*;}
# 不混淆某个接口的实现
-keep class * implements name.huihui.example.TestInterface { *; }
不混淆某个类的构造方法
-keepclassmembers class name.huihui.example.Test { 
  public <init>(); 
}
# 不混淆某个类的特定的方法
-keepclassmembers class name.huihui.example.Test { 
  public void test(java.lang.String); 
}
# 不混淆某个类的内部类
-keep class name.huihui.example.Test$* {
      *;
}

```