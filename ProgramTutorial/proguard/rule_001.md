> 混淆的开关
```
android {
    defaultConfig {
    }
    /** 配置 签名工具 */
    signingConfigs {
    }
    buildTypes {
        debug {
            /*Zip代码压缩优化  默认就是true app对齐*/
            zipAlignEnabled isMinifyEnabled.toBoolean()
            /*优化是否开启 true的时候会进行代码混淆 并优化代码 去掉多余无用的类*/
            minifyEnabled isMinifyEnabled.toBoolean()
            /*优化压缩资源文件 和minifyEnabled配合使用 必须开启混淆 这个可以减少apk的大小*/
            shrinkResources isMinifyEnabled.toBoolean()
            /*生成的apk是否可以调试 默认是true*/
            debuggable true
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            signingConfig signingConfigs.release
        }
        release {
            /*Zip代码压缩优化  默认就是true app对齐*/
            zipAlignEnabled isMinifyEnabled.toBoolean()
            /*启动代码混淆*/
            minifyEnabled isMinifyEnabled.toBoolean()
            /*优化压缩资源文件 和minifyEnabled配合使用 必须开启混淆 这个可以减少apk的大小*/
            shrinkResources isMinifyEnabled.toBoolean()
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            signingConfig signingConfigs.release
        }
    }
}
```