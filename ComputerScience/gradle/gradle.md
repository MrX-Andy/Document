### Gradle  

### 使用本地gradle  
```
//  gradle/wrapper/gradle-wrapper.properties  
distributionUrl=file\:/Users/alex/WorkSpace/Gradle/gradle-3.3-all.zip  
```
### 声明依赖项     
compile fileTree(dir: 'libs', include: ['*.jar'])  
compile 'com.android.support:appcompat-v7:25.0.0'    
compile project(':YibaAnalytics')    
compile project(':library:YibaAnalytics')    
compile files('libs/YibaAnalytics5.jar')    

debugCompile project(path: ':library', configuration: 'debug')  
releaseCompile project(path: ':library', configuration: 'release')    

compile是默认的那个，其含义是包含所有的依赖包，即在APK里，compile的依赖会存在。  
provided的意思是提供编译支持，但是不会写入apk。  
compile： main application
androidTestCompile： test application
debugCompile： debug Build Type  
releaseCompile： release Build Type  
runtime：运行时所需要的依赖。默认情况下，包含了编译时期的依赖  
testCompile：编译测试代码时所需要的依赖。默认情况下，包含了编译时产生的类文件，以及编译时期所需要的依赖  
testRuntime：测试运行时期的依赖。默认情况下，包含了上面三个时期的依赖  
branchOneCompile 'com.android.support:appcompat-v7:22.2.0'//只为branchOne添加这个依赖  

api  自己用，也暴露给 别人用  
implementation  仅仅自己用  

```
implementation ('com.alex.tools:log-dev:1.0.11') {
        exclude group: 'com.android.support', module: 'palette-v7'
        exclude group: 'com.android.support', module: 'appcompat-v7'
        exclude group: 'com.android.support', module: 'recyclerview'
        force=true
        //  加上force = true表明的意思就是即使在有依赖库版本冲突的情况下坚持使用被标注的这个依赖库版本
    }
implementation ('com.alex.tools:log-dev:1.0.11'){
    exclude module: 'base_model'    
    exclude group:'com.name.group' module:'base_model'
}    
```

    

### 常用 DSL  
```
include ':app'  
include ':libraries:someProject'  
assemble  组装项目的输出的任务  
check  运行所有检查的任务  
build  这个任务将执行assemble和check  
clean  这个任务将清理项目的输出  
connectedCheck  运行需要一个已连接的设备或模拟器的检查。它们将在所有已连接的设备上并行运行  
deviceCheck  使用 API 连接到远程设备运行检查。这一个是在 CI 服务器上使用的  
```
[属性参考](library/BuildType_Properties.md)   
[方法参考](library/BuildType_method.md)   
### 延伸方法  
```
aaptOptions { }  
adbOptions { }  
buildTypes { }  
compileOptions { }  
dataBinding { }  
defaultConfig { }  
dexOptions { }   
externalNativeBuild { }  
jacoco { }  
lintOptions { }  
packagingOptions { }  
productFlavors { }  
signingConfigs { }  
sourceSets { }  
splits { }  
testOptions { }  

```
[AppExtension](library/AppExtension.md)  
### applicationId 添加后缀  
```
android {
    ...
    defaultConfig {...}
    buildTypes {
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }

        debug {
            applicationIdSuffix ".debug"
        }

        /**
         * The 'initWith' property allows you to copy configurations from other build types,
         * so you don't have to configure one from the beginning. You can then configure
         * just the settings you want to change. The following line initializes
         * 'jnidebug' using the debug build type, and changes only the
         * applicationIdSuffix and versionNameSuffix settings.
         */

        jnidebug {

            // This copies the debuggable attribute and debug signing configurations.
            initWith debug

            applicationIdSuffix ".jnidebug"
            jniDebuggable true
        }
    }
}
```
buildTypes    
```
android {
    buildTypes {
        debug {
            applicationIdSuffix ".debug"
        }

        jnidebug.initWith(buildTypes.debug)
        jnidebug {
            packageNameSuffix ".jnidebug"
            jniDebuggable true
        }
    }
}
```
[productFlavors 构建不同产品](android/productFlavors.md)  
### dexOptions    
```
android {
    dexOptions {
        incremental false
        preDexLibraries = false
        jumboMode = false
    }
}
```
### lintOptions  
```
android {
    lintOptions {
        // 设置为 true时lint将不报告分析的进度
        quiet true
        // 如果为 true，则当lint发现错误时停止 gradle构建
        abortOnError false
        // 如果为 true，则只报告错误
        ignoreWarnings true
        // 如果为 true，则当有错误时会显示文件的全路径或绝对路径 (默认情况下为true)
        //absolutePaths true
        // 如果为 true，则检查所有的问题，包括默认不检查问题
        checkAllWarnings true
        // 如果为 true，则将所有警告视为错误
        warningsAsErrors true
        // 不检查给定的问题id
        disable 'TypographyFractions','TypographyQuotes'
        // 检查给定的问题 id
        enable 'RtlHardcoded','RtlCompat', 'RtlEnabled'
        // * 仅 * 检查给定的问题 id
        check 'NewApi', 'InlinedApi'
        // 如果为true，则在错误报告的输出中不包括源代码行
        noLines true
        // 如果为 true，则对一个错误的问题显示它所在的所有地方，而不会截短列表，等等。
        showAll true
        // 重置 lint 配置（使用默认的严重性等设置）。
        lintConfig file("default-lint.xml")
        // 如果为 true，生成一个问题的纯文本报告（默认为false）
        textReport true
        // 配置写入输出结果的位置；它可以是一个文件或 “stdout”（标准输出）
        textOutput 'stdout'
        // 如果为真，会生成一个XML报告，以给Jenkins之类的使用
        xmlReport false
        // 用于写入报告的文件（如果不指定，默认为lint-results.xml）
        xmlOutput file("lint-report.xml")
        // 如果为真，会生成一个HTML报告（包括问题的解释，存在此问题的源码，等等）
        htmlReport true
        // 写入报告的路径，它是可选的（默认为构建目录下的 lint-results.html ）
        htmlOutput file("lint-report.html")

         // 设置为 true， 将使所有release 构建都以issus的严重性级别为fatal（severity=false）的设置来运行lint
         // 并且，如果发现了致命（fatal）的问题，将会中止构建（由上面提到的 abortOnError 控制）
        checkReleaseBuilds true
        // 设置给定问题的严重级别（severity）为fatal （这意味着他们将会
        // 在release构建的期间检查 （即使 lint 要检查的问题没有包含在代码中)
        fatal 'NewApi', 'InlineApi'
        // 设置给定问题的严重级别为error
        error 'Wakelock', 'TextViewEdits'
        // 设置给定问题的严重级别为warning
        warning 'ResourceAsColor'
        // 设置给定问题的严重级别（severity）为ignore （和不检查这个问题一样）
        ignore 'TypographyQuotes'
    }
}
```
[sourceSets 修改源集](android/sourceSets.md)  
### signingConfigs 签名工具     
```
android {
    ...
    defaultConfig {...}
    signingConfigs {
        release {
            storeFile file("myreleasekey.keystore")
            storePassword "password"
            keyAlias "MyReleaseKey"
            keyPassword "password"
        }
    }
    buildTypes {
        release {
            ...
            signingConfig signingConfigs.release
        }
    }
}
```
### applicationVariants 修改apk名字    
在app的module里的build.gradle文件中，在android { ...}里面加上这样一段代码，即可修改生成的apk的文件名。  

```
applicationVariants.all { variant ->
    variant.outputs.all { output ->
        def outputFile = output.outputFile
        if (outputFile != null && outputFile.name.endsWith('.apk')) {
            //  删除  之前打包的 apk
            def file = new File(outputFile.getParentFile().getParentFile().getParentFile().getAbsolutePath())
            file.deleteDir()
            //生成新的apk  AndFun_basic_debug_1.0_2018_0311_1203.apk
            def fileName = "AndFun_${variant.flavorName}_${variant.buildType.name}_${defaultConfig.versionName}_${buildTime()}.apk"
            outputFileName = fileName
        }
    }
}
//  依赖函数
static def buildTime() {
    def date = new Date()
    def formattedDate = date.format('yyyy_MMdd_HHmm')
    return formattedDate
}  
```

### compileOptions     
```
compileOptions {  
    sourceCompatibility JavaVersion.VERSION_1_7  
    targetCompatibility JavaVersion.VERSION_1_7  
}
```
### externalNativeBuild  
```
android {
    defaultConfig {
    
    //配置ndk的一些规则
    externalNativeBuild {
        cmake {
            cppFlags "-frtti -fexceptions"
        }
        //配置Gradle 构建时需要的.so动态库
        // Gradle会构建那些 ABI 配置,但是只会将 defaultConfig.ndk {} 代码块中指定的配置打包到 apk 中
        ndkBuild{
            abiFilters 'armeabi','armeabi-v7a'
        }
    }
    //指打包到apk里面的.so包种类
    ndk{
        abiFilters 'armeabi','armeabi-v7a'
    }
}
```
### repositories  
```
buildscript {
    repositories {
        mavenLocal()
                maven { url 'http://maven.aliyun.com/nexus/content/groups/public/' }
                maven { url 'http://maven.aliyun.com/nexus/content/repositories/central/' }
                maven { url 'http://maven.aliyun.com/nexus/content/repositories/jcenter' }
                jcenter() { url 'http://jcenter.bintray.com/' }
                maven { url "https://jitpack.io" }
                maven { url "https://maven.google.com" }
                google()
                mavenCentral()
                //  maven { url minePluginUri }
    }
}

allprojects {
    repositories {
        mavenLocal()
                maven { url 'http://maven.aliyun.com/nexus/content/groups/public/' }
                maven { url 'http://maven.aliyun.com/nexus/content/repositories/central/' }
                maven { url 'http://maven.aliyun.com/nexus/content/repositories/jcenter' }
                jcenter() { url 'http://jcenter.bintray.com/' }
                maven { url "https://jitpack.io" }
                maven { url "https://maven.google.com" }
                google()
                mavenCentral()
                 /*设置 aar 路径 */
                flatDir {
                    dirs '../build_jar_aar'
                }
    }
}
```  
### Gradle 常见命令  
windows  gradlew;  mac  gradle  
//构建  
gradlew app:clean    //移除所有的编译输出文件，比如apk    

gradlew app:build   //构建 app module ，构建任务，相当于同时执行了check任务和assemble任务  

//检测  
gradlew app:check   //执行lint检测编译。  

//打包  
gradlew app:assemble //可以编译出release包和debug包，可以使用gradlew assembleRelease或者gradlew assembleDebug来单独编译一种包   

gradlew app:assembleRelease  //app module 打 release 包   

gradlew app:assembleDebug  //app module 打 debug 包  

//安装，卸载  
gradlew app:installDebug  //安装 app 的 debug 包到手机上  

gradlew app:uninstallDebug  //卸载手机上 app 的 debug 包   

gradlew app:uninstallRelease  //卸载手机上 app 的 release 包   

gradlew app:uninstallAll  //卸载手机上所有 app 的包   

gradlew assembleWandoujiaRelease  //豌豆荚 release 包  

gradlew assembleWandoujiaDebug //豌豆荚 debug 包  
### 添加 aar     
在全局的 gradle  
```
allprojects {
    repositories {
        jcenter()

        flatDir {
            dirs '../build_jar_aar'
        }
    }
}
```
在模块的gradle  
```
dependencies {
    compile(name:'YibaAnalytics-release', ext:'aar')
}
```  

### 自定义Plugin  
[开始自定义plugin](plugin/start_001.md)  

https://docs.gradle.org/current/userguide/custom_plugins.html  
[自定义 localMaven](plugin/localMaven.md)   
[自定义 localMaven 添加Java 文档](plugin/localMavenJavaDoc.md)   
[task 示例1](library/task_sample_01.md)  
[task make jar](library/task_makejar.md)  
[基础 方法声明与使用](library/basic_method.md)  
### 基础 类声明与使用  
```
task helloJavaBean << {
    Person p = new Person()

    println "名字是：${p.name}"
    p.name = "张三"
    println "名字是：${p.name}"
    println "年龄是：${p.age}"
}

class Person {
    private String name

    public int getAge(){
        12
    }
}
```
[外部函数的声明与引用](library/def_fun_outter.md)  
[常用功能](library/def_fun_01.md)   
### sdkVersion  
三者关系:  
minSdkVersion <= targetSdkVersion <= compileSdkVersion;  
理想状态:  
minSdkVersion <= targetSdkVersion == compileSdkVersion;  

compileSdkVersion 告诉 Gradle 用哪个版本的 SDK 来编译;  
修改 compileSdkVersion 不会改变运行时的行为, 可能会出现新的编译警告, 编译错误等;  
但新的 compileSdkVersion 不会被包含到 APK 中, 它只是在编译的时候使用, 因此我们强烈推荐总是使用最新的 SDK 进行编译;  

minSdkVersion 是应用可以运行的最低要求;  
是商店用来判断, 用户设备是否可以, 安装某个应用的标志之一;  
minSdkVersion 是一个商业决策问题, 一般支持 97%以上, 额外的支持会带来更多的开发和测试成本;  

targetSdkVersion 是 Android 提供向前兼容的主要依据;  
如果 targetSdkVersion 为19 对应 Android4.4, 应用运行时, 最高只能使用API 19的新特性;  
即使代码中使用了API 23的新特性, 实际运行时, 也不会使用该新特性;  

### 参考  
http://google.github.io/android-gradle-dsl/current/  
https://github.com/udacity/ud867  
配置构建  https://developer.android.google.cn/studio/build/index.html  

http://www.cnblogs.com/zhaoyanjun/p/7603640.html  
http://www.flysnow.org/categories/Android/  
https://juejin.im/post/582d606767f3560063320b21  
https://www.jianshu.com/p/7b31cc80421d  
http://blog.csdn.net/linkuiyao/article/details/78079871  
http://www.bijishequ.com/subject/102  
http://blog.csdn.net/maosidiaoxian/article/details/42023609  
http://www.flysnow.org/2015/03/30/manage-your-android-project-with-gradle.html  
https://github.com/rujews/android-tech-docs/blob/master/new-build-system/user-guide/README.md  
http://tools.android.com/tech-docs/new-build-system/user-guide  
http://blog.csdn.net/maosidiaoxian/article/details/41113353  
http://gradledoc.qiniudn.com/1.12/userguide/userguide.html  
http://blog.csdn.net/maosidiaoxian/article/details/42417779  
http://blog.csdn.net/maosidiaoxian/article/details/42671999  
http://blog.csdn.net/maosidiaoxian/article/details/43148643  
https://github.com/rujews/android-gradle-book-code  

插件  
https://github.com/scana/ok-gradle  

