###### OkHttp、Retrofit、RxJava
```

 #============  okhttp  ===============
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}
#============  okhttp finish  ===============

#============  okio  ============
-dontwarn okio.**
-keep class okio.**{*;}
#============  okio finish ============

#=============   retrofit2 ====================
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8

#=============   retrofit2 finish ====================
#====== reactive ====================
-dontwarn io.reactivex.**
-keep class io.reactivex.**{ *;}
-dontwarn org.reactivestreams.**
-keep class org.reactivestreams.**{ *;}
-dontwarn com.trello.rxlifecycle2.**
-keep class com.trello.rxlifecycle2.**{ *;}
#====== reactive finish ====================

#====== retrofit2 ====================
-dontwarn retrofit2.**
-keep class retrofit2.**{ *;}
#====== retrofit2 finish ====================
```