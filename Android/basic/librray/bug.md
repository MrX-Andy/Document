### 静态变量回收问题  
https://blog.csdn.net/qq_29918347/article/details/51727581  
### ScrollView嵌套ListView  

NestedScrollView嵌套RecyclerView  
都会出现 ScrollView展示的第一条数据，是ListView的第一条数据， 在ListView之上的布局， 是看不见的，  
解决办法：android:descendantFocusability="blocksDescendants"    


```
<android.support.v4.widget.NestedScrollView     
    android:layout_width="match_parent"     
    android:layout_height="match_parent">     
    
    <LinearLayout         
        android:descendantFocusability="blocksDescendants"         
        android:layout_width="match_parent"         
        android:layout_height="match_parent"         
        android:orientation="vertical">
        
```
beforeDescendants：viewgroup会优先其子类控件而获取到焦点  
afterDescendants：viewgroup只有当其子类控件不需要获取焦点时才获取焦点  
blocksDescendants：viewgroup会覆盖子类控件而直接获得焦点   

### CoordinateLayout嵌套水平的RecyclerView  

Java
```
rvTotal.setNestedScrollingEnabled(false); rvWeekly.setNestedScrollingEnabled(false);
....

LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());   
layoutManager.setSmoothScrollbarEnabled(true);   
layoutManager.setAutoMeasureEnabled(true);    
mNearbyCtivists.setLayoutManager(layoutManager);   
mNearbyCtivists.setHasFixedSize(true);   
mNearbyCtivists.setNestedScrollingEnabled(false);
```
xml  
```
<CoordinateLayout>  
    <NestedScrollView>
        <RecyclerView/>
```
### 65535问题  
app build.gradle  
```
主模块的gradle
defaultConfig {     
    multiDexEnabled true 
}
compile 'com.android.support:multidex:1.0.0'
```
application  
```
@Override
protected void attachBaseContext(Context base) {
   super.attachBaseContext(base);
   MultiDex.install(this);
}
```  
### Uri_7.0    
安装apk   
```
public void installApk(File file) {     
    try {         
        Intent intent = new Intent();        
        intent.setAction(Intent.ACTION_VIEW);         
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);         
        LogTrack.e(file.getAbsolutePath());         
        Uri uriForFile;         
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {             
            uriForFile = FileProvider.getUriForFile(this.activity, this.activity.getApplicationContext().getPackageName() + ".provider", file);             
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);         
        } else {             
            uriForFile = Uri.fromFile(file);         
        }         
        intent.setDataAndType(uriForFile, "application/vnd.android.package-archive");         
        LogTrack.e("开始安装--");         
        activity.startActivity(intent);     
    } catch (Exception ex) {         
        LogTrack.e(ex);     
    }
 }
```
res/xml/file_paths.xml   
```
<?xml version="1.0" encoding="utf-8"?> 
<paths xmlns:android="http://schemas.android.com/apk/res/android">     
    <external-path name="external_files" path="."/> 
</paths>
```
清单文件  
```
<manifest xmlns:android="http://schemas.android.com/apk/res/android"           package="com.alex.install">
	<application>
	    <!-- android:authorities="包名.provider" --> 	    
	    <provider 	        
	        android:name="android.support.v4.content.FileProvider" 	        
	        android:authorities="com.alex.install.provider" 	        
	        android:grantUriPermissions="true" 	        
	        android:exported="false"> 	        
	        <meta-data 	            
	            android:name="android.support.FILE_PROVIDER_PATHS" 	            
	            android:resource="@xml/file_paths" /> 	    
	    </provider> 	
	</application>
</manifest>
```

### support 迁移到 androidx   
https://www.jianshu.com/p/f7a7a8765294  
https://developer.android.com/jetpack/androidx/migrate  
1.. gradle-wrapper.properties   
```
distributionUrl=https\://services.gradle.org/distributions/gradle-4.10.1-all.zip  
```  
1.. gradle_version=3.4.0-alpha02  
2.. gradle.properties  
```
android.useAndroidX=true
android.enableJetifier=true
```  

### 参考  
https://blog.csdn.net/wxz1179503422/article/details/83031724  
