#### dataBinding 结合 kotlin 报错问题  
3.2 Canary 18   、  gradle 4.6 、 kotlin 2.1.50  
##### 全局的 gradle
```
buildscript {
    ext.kotlin_version = '1.2.50'
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.2.0-alpha18'
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}

```
##### module 的gradle
```
apply plugin: 'com.android.application'
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-android-extensions'
apply plugin: 'kotlin-kapt'

android {
    compileSdkVersion 28
    defaultConfig {
        applicationId "com.alex.acho"
        minSdkVersion 17
        targetSdkVersion 28
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }

    /**DataBinding*/
    dataBinding {
        enabled = true
    }

}

kapt {
    /**DataBinding 配置*/
    generateStubs = true
//    mapDiagnosticLocations = true
}
androidExtensions {
    experimental = true
}

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'
    implementation 'com.android.support:appcompat-v7:28.0.0-alpha3'
    implementation 'com.android.support.constraint:constraint-layout:1.1.2'
    /*dataBinding*/
    kapt 'com.android.databinding:compiler:3.1.3'

    /*kotlin*/
    implementation libs.jetbrains_kotlin_stdlib_jdk7
    implementation libs.jetbrains_kotlinx_coroutines_core
    implementation libs.jetbrains_kotlinx_coroutines_android
}

```
##### 运行报错
```
Directory 'D:\WorkSpace\Android\Acho\app\build\intermediates\feature_data_binding_base_feature_info\debug\dataBindingExportFeaturePackageIdsDebug\out' specified for property 'annotationProcessorOptionProviders$kotlin_gradle_plugin.$0.$0.baseFeatureInfoDir' does not exist.

```  

##### 解决办法  
猜测，版本不一致，导致   dataBinding 的版本 要和 gradle的版本一致  
```
classpath 'com.android.tools.build:gradle:3.1.3'  
kapt 'com.android.databinding:compiler:3.1.3'  
```
