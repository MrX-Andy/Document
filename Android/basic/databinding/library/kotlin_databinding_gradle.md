###### Kotlin与DataBinding 配置

> project的gradle配置

```

buildscript {

    repositories {
        mavenLocal()
        jcenter()
        mavenCentral()
        maven { url "https://jitpack.io" }
        maven { url "https://dl.google.com/dl/android/maven2/" }
        maven { url "https://jcenter.bintray.com" }
        maven { url "http://repo1.maven.org/maven2" }
        maven { url 'https://maven.google.com' }
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.0.0'
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlin_version}"
        /*当初就是 少了这句，搞了好长时间*/
        classpath "org.jetbrains.kotlin:kotlin-android-extensions:${kotlin_version}"
    }
}

allprojects {
    repositories {
        mavenLocal()
        jcenter()
        mavenCentral()
        maven { url "https://jitpack.io" }
        maven { url "https://dl.google.com/dl/android/maven2/" }
        maven { url "https://jcenter.bintray.com" }
        maven { url "http://repo1.maven.org/maven2" }
        maven { url 'https://maven.google.com' }
    }
}



task clean(type: Delete) {
    delete rootProject.buildDir
}
```
> module的gradle配置
```
if (isDebug.toBoolean()) {
    apply plugin: 'com.android.application'
} else {
    apply plugin: 'com.android.library'
}
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-kapt'
apply plugin: 'kotlin-android-extensions'

android {

    buildTypes {
    
    }
    /**DataBinding*/
    dataBinding {
        enabled = true
    }
   
}
kapt {
    /**DataBinding 配置*/
    generateStubs = true
    /**ARouter 配置*/
    arguments {
        arg("moduleName", project.getName())
    }
}
dependencies {
    kapt "com.android.databinding:compiler:3.0.0"
}
apply from: "../build_lib/build_module_account.gradle"
```

> gradle.properties

```
#定义全局的 kotlin 版本号
kotlin_version=1.1.51
#是模块，就是application，可以独立运行，修改之后需要同步gradle
isDebug=true
# 开启代码混淆
isMinifyEnabled = false
# databinding与kotlin的冲突问题
kotlin.incremental=false
```