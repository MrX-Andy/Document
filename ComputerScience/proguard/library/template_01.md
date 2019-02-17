```
# 抑制警告
-ignorewarnings
# 代码混淆压缩比，在0~7之间，默认为5，一般不做修改
-optimizationpasses 5
# 混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames
# 混淆时是否记录日志
# 这句话能够使我们的项目混淆后产生映射文件
# 包含有类名 -> 混淆后类名的映射关系
-verbose
# 优化  不优化输入的类文件
-dontoptimize
-dontobfuscate
# 不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
-dontpreverify
# 指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses
# 指定不去忽略非公共库的类成员
-dontskipnonpubliclibraryclassmembers

-printconfiguration

# 避免混淆泛型
-keepattributes Signature

# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes EnclosingMethod
-printmapping proguardMapping.txt
# 指定混淆是采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不做更改
-optimizations  !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*,!method/*
# -optimizations !code/simplification/cast,!field/*,!class/merging/*
#---------------------------------    Activity     开始     ↓    ------------------------------------

#---------------------------------    Activity     结束    ↑    ------------------------------------




#---------------------------------    R     开始     ↓    ------------------------------------
-keep class **.R$* {*;}
#---------------------------------    R     结束    ↑    ------------------------------------


#---------------------------------    Application     开始     ↓    ------------------------------------
-keep public class * extends android.app.Application
-keep public class * extends android.support.multidex.MultiDexApplication
#---------------------------------    Application     结束    ↑    ------------------------------------


#---------------------------------    四大组件     开始     ↓    ------------------------------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Appliction
-keep public class * extends android.app.Fragment
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class com.google.vending.licensing.ILicensingService
#---------------------------------    四大组件     结束    ↑    ------------------------------------

#---------------------------------    support     开始     ↓    ------------------------------------
# 保留support下的所有类及其内部类

# 保留继承的
-dontwarn android.support.**
-keep class android.support.** {*;}
-keep public class * extends android.support.annotation.**


#  support.v4    ====================
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment
-keepclasseswithmembernames class android.support.v4.widget.** { *; }
-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

#  support.v7    ====================
-keep public class * extends android.support.v7.**
-keep class android.support.v7.internal.** { *; }
-keep interface android.support.v7.internal.** { *; }
-keep class android.support.v7.widget.** { *; }
-keep public class * extends android.support.v7.app.ActionBarActivity { *; }
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }
-keep class android.support.v7.widget.RoundRectDrawable { *; }

#  design    ====================
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

#---------------------------------    support v7     结束    ↑    ------------------------------------


#---------------------------------    点击事件     开始     ↓    ------------------------------------
-keepclassmembers class *  {
    void *(android.view.View);
}
-keepclassmembers class * extends android.app.Activity {
   void *(android.view.View);
}
#---------------------------------    点击事件     结束    ↑    ------------------------------------

#---------------------------------    webview     开始     ↓    ------------------------------------
# 自定义的 WebView
-dontwarn android.webkit.WebView

-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}

#---------------------------------    webview     结束    ↑    ------------------------------------


#---------------------------------    日志输出     开始     ↓    ------------------------------------
#  keep  是保持 log，没有 keep，就是 混淆 log
-keep class org.alex.util.LogTrack {*;}

#  去掉 日志 函数
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
}
#    public static *** e(...);

#  System.out.print
-assumenosideeffects class java.io.PrintStream {
    public *** println(...);
    public *** print(...);
}
#---------------------------------    日志输出     结束    ↑    ------------------------------------


#---------------------------------    自定义 view     开始     ↓    ------------------------------------
-keep public class * extends android.view.View
-keepclassmembers public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    void set*(***);
    *** get*();
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#---------------------------------    自定义 view     结束    ↑    ------------------------------------


#---------------------------------    Annotation     开始     ↓    ------------------------------------
-keep class * extends java.lang.annotation.Annotation { *; }

#  keep相关注解
-keep class android.support.annotation.Keep

-keep @android.support.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}
#---------------------------------    Annotation     结束    ↑    ------------------------------------


#---------------------------------    序列化     开始     ↓    ------------------------------------
# 保持 Serializable 不被混淆
-keep class * implements java.io.Serializable { *; }
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
# 保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable { *; }

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
#---------------------------------    序列化     结束    ↑    ------------------------------------

#---------------------------------    native     开始     ↓    ------------------------------------
-keepclasseswithmembernames class * { native <methods>;}
-keepclasseswithmembers class * { native <methods>; }
#---------------------------------    native     结束    ↑    ------------------------------------

#---------------------------------    enum     开始     ↓    ------------------------------------
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#---------------------------------    enum     结束    ↑    ------------------------------------


#---------------------------------    squareup  全家桶     开始     ↓    ------------------------------------
-keepattributes Signature
-keepattributes Exceptions
-dontwarn javax.annotation.**

# retrofit2
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
-dontnote retrofit2.Platform
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
-dontwarn retrofit2.Platform$Java8
#-------------- okhttp3 -------------
#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}
#okio
-dontwarn okio.**
-keep class okio.**{*;}

# ====== reactive ====================
-dontwarn io.reactivex.**
-keep class io.reactivex.**{ *;}
-dontwarn org.reactivestreams.**
-keep class org.reactivestreams.**{ *;}
-dontwarn com.trello.rxlifecycle2.**
-keep class com.trello.rxlifecycle2.**{ *;}

#---------------------------------    squareup  全家桶     结束    ↑    ------------------------------------


#---------------------------------    butterknife     开始     ↓    ------------------------------------
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
#---------------------------------    butterknife     结束    ↑    ------------------------------------


#---------------------------------    json     开始     ↓    ------------------------------------
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
#  gson  ===============
-keep class sun.misc.Unsafe { *; }
-keep public class com.google.gson.** {*;}
-keep class com.google.gson.stream.** { *; }

#  fastjson  ===============
-dontwarn com.alibaba.fastjson.**

# 使用Gson时需要配置Gson的解析对象及变量都不混淆。不然Gson会找不到变量。
# 将下面替换成自己的实体类
-keep class com.alex.*.entity.** { *; }

# Jackson 2.x    ===============
-keep class com.fasterxml.jackson.databind.ObjectMapper {
    public <methods>;
    protected <methods>;
}
-keep class com.fasterxml.jackson.databind.ObjectWriter {
    public ** writeValueAsString(**);
}
#---------------------------------    json     结束    ↑    ------------------------------------


#---------------------------------    event bus     开始     ↓    ------------------------------------
#  event bus 2   ===============
-keepclassmembers class ** {
    public void onEvent*(***);
}

# Only required if you use AsyncExecutor
-keepclassmembers class * extends de.greenrobot.event.util.ThrowableFailureEvent {
    public <init>(java.lang.Throwable);
}

# Don't warn for missing support classes
-dontwarn de.greenrobot.event.util.*$Support
-dontwarn de.greenrobot.event.util.*$SupportManagerFragment

#  event bus 3   ===============
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
#---------------------------------    event bus     结束    ↑    ------------------------------------

#---------------------------------    data  base     开始     ↓    ------------------------------------
# greendao   ===============
-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
    public static java.lang.String TABLENAME;
}
-keep class **$Properties

#---------------------------------    data  base     结束    ↑    ------------------------------------


#---------------------------------    glide     开始     ↓    ------------------------------------
#========== glide v3.8  ===============
-dontwarn com.bumptech.glide.**
-keep class com.bumptech.glide.**{ *;}

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

#========== glide v4.0 ===============
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule
#---------------------------------    glide     结束    ↑    ------------------------------------


#---------------------------------    joda     开始     ↓    ------------------------------------
# joda  ===============
-keep class org.joda.convert.** { *; }
-keep interface org.joda.convert.** { *; }
-dontwarn org.joda.convert.FromString
-dontwarn org.joda.convert.ToString

-dontwarn org.joda.convert.**
-dontwarn org.joda.time.**
-keep class org.joda.time.** { *; }
-keep interface org.joda.time.** { *; }

#---------------------------------    joda     结束    ↑    ------------------------------------


#---------------------------------    kotlin     开始     ↓    ------------------------------------
-dontwarn kotlin.**
-keep class kotlin.**{ *;}
-dontwarn kotlinx.**
-keep class kotlinx.**{ *;}

-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
    public static void checkNotNullParameter(java.lang.Object, java.lang.String);
    public static void checkExpressionValueIsNotNull(java.lang.Object, java.lang.String);
    public static void checkNotNullExpressionValue(java.lang.Object, java.lang.String);
    public static void checkReturnedValueIsNotNull(java.lang.Object, java.lang.String);
    public static void checkReturnedValueIsNotNull(java.lang.Object, java.lang.String, java.lang.String);
    public static void checkFieldIsNotNull(java.lang.Object, java.lang.String);
    public static void checkFieldIsNotNull(java.lang.Object, java.lang.String, java.lang.String);
    public static void throwUninitializedProperty(java.lang.String);
    public static void throwUninitializedPropertyAccessException(java.lang.String);
    public static void throwNpe(...);
}

#---------------------------------    kotlin     结束    ↑    ------------------------------------

```