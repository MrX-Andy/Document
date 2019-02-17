###### Gson

```
-libraryjars libs/gson-2.3.1-sources.jar  
-libraryjars libs/gson-2.3.1.jar  
-dontwarn com.google.gson.**      
-keep class sun.misc.Unsafe { *; }  
-keep class com.google.gson.** { *; }  
```