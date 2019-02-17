每一个不同的productFlavors 都可以覆盖 applicationId  
如果使用 gradle 3.0以上， 那么flavor必须有一个dimension，其他的flavor都使用这个dimension， 如果每一个都有自己的dimension，那么可以：    
```
android {
    ...
    // Specifies the flavor dimensions you want to use. The order in which you
    // list each dimension determines its priority, from highest to lowest,
    // when Gradle merges variant sources and configurations. You must assign
    // each product flavor you configure to one of the flavor dimensions.
    flavorDimensions 'api', 'version'

    productFlavors {
      demo {
        // Assigns this product flavor to the 'version' flavor dimension.
        dimension 'version'
        packageName "com.example.flavor1"
        versionCode 20
        ...
    }

      full {
        dimension 'version'
        packageName "com.example.flavor2"
        minSdkVersion 14
        ...
      }

      minApi24 {
        // Assigns this flavor to the 'api' dimension.
        dimension 'api'
        minSdkVersion '24'
        versionNameSuffix "-minApi24"
        ...
      }

      minApi21 {
        dimension "api"
        minSdkVersion '21'
        versionNameSuffix "-minApi21"
        ...
      }
   }
}
```
```
android {
    ...
    defaultConfig {...}
    buildTypes {...}
    productFlavors {
        demo {
            applicationIdSuffix ".demo"
            versionNameSuffix "-demo"
        }
        full {
            applicationIdSuffix ".full"
            versionNameSuffix "-full"
        }
    }
}
```

```
productFlavors {
    // 定义 字段  me_flavor  
    /**
     * 常规版本
     * 打release包：gradlew assembleCommonRelease
     */
    Common {
        buildConfigField "int", "me_flavor", "0"
    }
    //自动化测试版
    Monkey {
        buildConfigField "int", "me_flavor", "1"
    }
    //其他测试
    Test {
        buildConfigField "int", "me_flavor", "2"
    }
    //Crash 2 Test Environment
    CrashEnvironment {
        buildConfigField "int", "me_flavor", "3"

    }
}

// 自动生成  
public final class BuildConfig {
  // Fields from product flavor: Common
  public static final int me_flavor = 0;
}

//  使用字段  
if (CommonConfig.Test_me_flavor == BuildConfig.me_flavor) {

}
```   
```
flavorDimensions "brand","college","mobile"
    productFlavors {
        common {
            applicationId "com.alex.myapplication"
            dimension "brand"
        }
        baidu {
            applicationId "com.alex.myapplication"
            dimension "brand"
        }
        qihu {
            applicationId "com.alex.myapplication"
            dimension "college"
        }
        wandoujia {
            applicationId "com.alex.myapplication"
            dimension "mobile"
        }
        yingyongbao {
            applicationId "com.alex.myapplication"
            dimension "mobile"
        }

    }
```
