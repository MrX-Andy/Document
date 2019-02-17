### 主要用法

> [注意事项](Attention_SameClass_DifferentFile.md)  



```
Demo
host
project build
// Top-level build file where you can add configuration options common to all sub-projects/modules.  
buildscript {     
    
    dependencies {         
        classpath 'com.android.tools.build:gradle:2.3.3'         
        classpath 'com.qihoo360.replugin:replugin-host-gradle:2.1.7'      } 
    }
    
app build
android {     
    apply plugin: 'replugin-host-gradle' repluginHostConfig {     
        useAppCompat = true 
    } 
     
    dependencies {     compile 'com.qihoo360.replugin:replugin-host-lib:2.1.7' }

Application
public class App extends Application {      
    @Override     
    protected void attachBaseContext(Context context) {         
        super.attachBaseContext(context);         
        RePlugin.App.attachBaseContext(this);         
        BaseUtil.getInstance().init(this);     
    }      
    @Override     
    public void onCreate() {         
        super.onCreate();         
        RePlugin.App.onCreate();     
    }      
}



plugin

project build
buildscript {     
    dependencies {         
    classpath 'com.android.tools.build:gradle:2.3.3'         
    classpath 'com.qihoo360.replugin:replugin-plugin-gradle:2.1.7'     
}
} 
   

app build
apply plugin: 'com.android.application'  android {     
defaultConfig {         
applicationId "com.alex.replugin.plugin"     }     
buildTypes {     }      lintOptions {         
abortOnError false     }  }   
// 这个plugin需要放在android配置之后，因为需要读取android中的配置项 
apply plugin: 'replugin-plugin-gradle' repluginPluginConfig {     
pluginName = "plugin"     
hostApplicationId = "com.alex.repluginsample"     
hostAppLauncherActivity = "com.alex.repluginsample.MainActivity" }  
dependencies {     
compile 'com.qihoo360.replugin:replugin-plugin-lib:2.1.7' }


示例1
示例描述：
host 从 SD卡安装 plugin，并打开 plugin 的 MainActivity

plugin 的 app 的 build
apply plugin: 'com.android.application' android {     
defaultConfig {         applicationId "com.alex.replugin.plugin"     }     buildTypes {     }      lintOptions {         abortOnError false     }  }  /* 这个plugin需要放在android{} 配置之后，因为需要读取android中的配置项*/ apply plugin: 'replugin-plugin-gradle' repluginPluginConfig {     pluginName = "plugin"     hostApplicationId = "com.alex.repluginsample"     hostAppLauncherActivity = "com.alex.repluginsample.MainActivity" } 

plugin 的 清单文件
<?xml version="1.0" encoding="utf-8"?> <manifest xmlns:android="http://schemas.android.com/apk/res/android"           xmlns:tools="http://schemas.android.com/tools"           package="com.alex.replugin.plugin">      <!-- 在SDCard中创建与删除文件权限 -->     <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>     <!-- 往SDCard写入数据权限 -->     <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>     <!-- 从SDCard读取数据权限 -->     <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>     <!-- 申请联网权限-->     <uses-permission android:name="android.permission.INTERNET"/>     <!-- 检测网络状态 -->     <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>       <application         android:allowBackup="true"         android:icon="@mipmap/ic_launcher"         android:label="@string/app_name"         android:roundIcon="@mipmap/ic_launcher_round"         android:supportsRtl="true"         android:theme="@style/AppTheme"         tools:ignore="AllowBackup,GoogleAppIndexingWarning">          <!--RePlugin  -->         <meta-data             android:name="com.qihoo360.plugin.name"             android:value="plugin"/>         <meta-data             android:name="com.qihoo360.plugin.version.ver"             android:value="114"/>          <activity android:name=".MainActivity">             <intent-filter>                 <action android:name="android.intent.action.MAIN"/>                  <category android:name="android.intent.category.LAUNCHER"/>             </intent-filter>         </activity>     </application>  </manifest>

host 的 MainActivity
override fun onClickEvent(v: View) {     if (R.id.btInstall == v.id) {         val pluginInfo = RePlugin.install(PluginEnum.mainActivityPath)         val preload = RePlugin.preload(pluginInfo)         ToastUtil.shortCenter("插件安装成功 " + preload)         return     }     if (R.id.btStart == v.id) {         RePlugin.startActivity(this@MainActivity, RePlugin.createIntent(PluginEnum.mainActivityPair.first, PluginEnum.mainActivityPair.second));         return     } }

host 的 静态字段
object CacheEnum {     val rootPath = Environment.getExternalStorageDirectory().absolutePath!!     val cacheName = "PluginSample";     val cachePath = "$rootPath/$cacheName/"  }  object PluginEnum {     val mainActivityPath = CacheEnum.cachePath + "plugin.apk"     val mainActivityPair = Pair("plugin", "com.alex.replugin.plugin.MainActivity") }


参考
```

> 参考  

https://github.com/Qihoo360/RePlugin/tree/master/replugin-sample  
https://github.com/Qihoo360/RePlugin/wiki/%E5%BF%AB%E9%80%9F%E4%B8%8A%E6%89%8B  
https://github.com/Qihoo360/RePlugin/blob/dev/README_CN.md  
https://github.com/Qihoo360/RePlugin/wiki  

插件接入指南  
https://github.com/Qihoo360/RePlugin/wiki/%E6%8F%92%E4%BB%B6%E6%8E%A5%E5%85%A5%E6%8C%87%E5%8D%97  

详细教程  
https://github.com/Qihoo360/RePlugin/wiki/%E8%AF%A6%E7%BB%86%E6%95%99%E7%A8%8B  

插件的管理  
https://github.com/Qihoo360/RePlugin/wiki/%E6%8F%92%E4%BB%B6%E7%9A%84%E7%AE%A1%E7%90%86  

插件的组件  
https://github.com/Qihoo360/RePlugin/wiki/%E6%8F%92%E4%BB%B6%E7%9A%84%E7%BB%84%E4%BB%B6#%E6%8F%92%E4%BB%B6%E8%B0%83%E7%94%A8%E4%B8%BB%E7%A8%8B%E5%BA%8F%E7%BB%84%E4%BB%B6  

RePlugin 流程与源码解析  
https://juejin.im/post/59752eb1f265da6c3f70eed9  

