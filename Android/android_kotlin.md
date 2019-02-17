### kotlin in android 

报错  
```
Class kotlin.reflect.jvm.internal.FunctionCaller$FieldSetter can not access a member of class com.android.build.gradle.tasks.ManifestProcessorTask with modifiers "private"
```
解决办法  
https://stackoverflow.com/questions/52311374/error-in-kotlin-but-using-only-java-class-kotlin-reflect-jvm-internal-function/52311813  
I solved this way. 1. Delete all the .gradle folders 2. Invalidate caches / restart everything is OK for me.  

### 参考  
https://kotlinlang.org/docs/tutorials/android-plugin.html  
https://github.com/fengzhizi715/Lifecycle-Coroutines-Extension  
https://github.com/ktorio/ktor  

SQL  
https://github.com/Kotlin/anko/wiki/Anko-SQLite  
https://github.com/arrow-kt/arrow  
