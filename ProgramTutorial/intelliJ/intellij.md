### IntelliJ 注册机

◆ 本地服务器   
● 打开 http://idea.lanyus.com/  
● 找到 请搭建自己的IntelliJ IDEA授权服务器，教程在http://blog.lanyus.com/archives/174.html  
● 进入网站，找到 1、下载下面的文件  
● 解压并运行 IntelliJIDEALicenseServer_windows_386.exe  
● 打开IntelliJ，授权服务器地址 填写 http://127.0.0.1:1017  
● 以后，尽情实用吧；  

◆ 使用远端 ip  
window + r   
输入 c:\windows\system32\drivers\etc  
编辑 hosts文件 追加  0.0.0.0 account.jetbrains.com  
打开网站 http://idea.lanyus.com/  
点击获取注册码  
[打包上传到仓库](library/upload_aar.md)  
[配置Maven库](library/with_maven.md)  
[基础设置](library/basic_using.md)  
[快捷键 mac](library/key_map_mac.md)  
[快捷键 window](library/key_map_window.md)  

### 使用Tomcat  
new project -> Java Enterprise  
web Application  

Tomcat出错看不到日志  
```
C:\ProgramFiles\Tomcat8_5\conf\logging.properties
追加添加  
org.apache.jasper.servlet.TldScanner.level = FINE
```

### java  doc 索引  
```
/**
 * @param clickStatus {@link com.a.b.c.StringUtil#USER_NAME_LENGTH}
 * @param clickStatus {@link com.a.b.c.StringUtil}
 */
```

### 解决依赖包，版本冲突  
在全局的 gradle  
```
allprojects {
    repositories {
        mavenLocal()
        jcenter()
        google()
        mavenCentral()
        maven { url "https://jitpack.io" }

        /*设置 aar 路径 */
        flatDir {
            dirs '../build_jar_aar'
        }
    }

    configurations.all {
        /*解决  Android dependency 'android.arch.core:runtime' has different version for the*/
        resolutionStrategy.force libs.arch_core_common
        resolutionStrategy.force libs.arch_core_runtime
        resolutionStrategy.force libs.support_annotation

        //  过滤掉 所有的 support
        exclude group: 'com.android.support'
    }
}
```

### 插件  
https://github.com/JesusFreke/smali  

ignore  
https://github.com/hsz/idea-gitignore  
https://plugins.jetbrains.com/plugin/7495--ignore  

GsonFormat  
https://plugins.jetbrains.com/plugin/7654-gsonformat  
https://github.com/zzz40500/GsonFormat  

