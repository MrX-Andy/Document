### 65535问题  

app gradle  
```
android {

    defaultConfig {
        multiDexEnabled = true
    }
    
}
```


Application  
```
override fun attachBaseContext(base: Context?) {
    super.attachBaseContext(base)
    MultiDex.install(base)
}
```

MultiDex  
因为在Dalvik指令集里，调用方法的invoke-kind指令中，method reference index只给了16bits，最多能调用65535个方法，  
所以在生成dex文件的过程中，  当方法数超过65535就会报错。细看指令集，除了method，field和class的index也是16bits，所以也存在65535的问题。  
一般来说，method的数目会比field和class多，所以method数会首先遇到65535问题，你可能都没机会见到field过65535的情况。  
为 Dalvik 可执行文件分包构建每个 DEX 文件时，构建工具会执行复杂的决策制定来确定主要 DEX 文件中需要的类，以便应用能够成功启动。  
如果启动期间需要的任何类未在主 DEX 文件中提供，那么您的应用将崩溃并出现错误 java.lang.NoClassDefFoundError。  
该情况不应出现在直接从应用代码访问的代码上，因为构建工具能识别这些代码路径，但可能在代码路径可见性较低（如使用的库具有复杂的依赖项）时出现。  
例如，如果代码使用自检机制或从原生代码调用 Java 方法，那么这些类可能不会被识别为主 DEX 文件中的必需项。    
因此，如果您收到 java.lang.NoClassDefFoundError，则必须使用构建类型中的 multiDexKeepFile 或 multiDexKeepProguard 属性声明它们，  
以手动将这些其他类指定为主 DEX 文件中的必需项。如果类在 multiDexKeepFile 或 multiDexKeepProguard 文件中匹配，则该类会添加至主 DEX 文件。  

multiDexKeepFile 属性  
您在 multiDexKeepFile 中指定的文件应该每行包含一个类，并且采用 com/example/MyClass.class 的格式。  
例如，您可以创建一个名为 multidex-config.txt 的文件，如下所示：  
```
com/example/MyClass.class
com/example/MyOtherClass.class
```
然后，您可以按以下方式针对构建类型声明该文件：  
```
android {
    buildTypes {
        release {
            multiDexKeepFile file 'multidex-config.txt'
            ...
        }
    }
}
```
请记住，Gradle 会读取相对于 build.gradle 文件的路径，因此如果 multidex-config.txt 与 build.gradle 文件在同一目录中，以上示例将有效。  


multiDexKeepProguard 属性  
multiDexKeepProguard 文件使用与 Proguard 相同的格式，并且支持整个 Proguard 语法。  
您在 multiDexKeepProguard 中指定的文件应该在任何有效的 ProGuard 语法中包含 -keep 选项。  
例如，-keep com.example.MyClass.class。您可以创建一个名为 multidex-config.pro 的文件，如下所示：  
```
-keep class com.example.MyClass
-keep class com.example.MyClassToo
```
如果您想要指定包中的所有类，文件将如下所示：  
```
-keep class com.example.** { *; } // All classes in the com.example package
```
然后，您可以按以下方式针对构建类型声明该文件：  
```
android {
    buildTypes {
        release {
            multiDexKeepProguard 'multidex-config.pro'
            ...
        }
    }
}
```

### 参考  
http://jiajixin.cn/2015/10/21/field-65535/  
http://www.jianshu.com/p/33f22b21ef1e  
https://developer.android.com/studio/build/multidex  
https://www.jianshu.com/p/a85bc59d6549   