### 使用参数设置  

```
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

INTERNET:访问网络权限，有了这个权限才能从网络上在线加载图片;  
ACCESS_NETWORK_STATE:访问网络状态权限，不是必须的权限，但是加上可以处理片状网络(flaky network) 和飞行模式;  
READ_EXTERNAL_STORAGE:阅读本地SD卡权限，从本地文档夹或 DCIM 或图库中加载图片;  
WRITE_EXTERNAL_STORAGE：将Glide的缓存存储到SD卡上需要这个权限;
  
```
禁用 内存缓存    
```
Glide.with(this)
     .load(url)
     .skipMemoryCache(true) // 禁用 内存缓存
     .into(imageView);
```

磁盘缓存  
```
Glide.with(this)
     .load(url)
     .diskCacheStrategy(DiskCacheStrategy.NONE)
     .into(imageView);

// 缓存参数说明
// DiskCacheStrategy.NONE：不缓存任何图片，即禁用磁盘缓存
// DiskCacheStrategy.ALL ：缓存原始图片 & 转换后的图片
// DiskCacheStrategy.SOURCE：只缓存原始图片（原来的全分辨率的图像，即不缓存转换后的图片）
// DiskCacheStrategy.RESULT：（默认）只缓存转换后的图片（即最终的图像：降低分辨率后 / 或者转换后 ，不缓存原始图片
```

### 加载图片  
```
// 加载本地图片
File file = new File(getExternalCacheDir() + "/image.jpg");
Glide.with(this).load(file).into(imageView);

// 加载应用资源
int resource = R.drawable.image;
Glide.with(this).load(resource).into(imageView);

// 加载二进制流
byte[] image = getImageBytes();
Glide.with(this).load(image).into(imageView);

// 加载Uri对象
Uri imageUri = getImageUri();
Glide.with(this).load(imageUri).into(imageView);

//  本地视频  
String filePath = "/storage/emulated/0/Pictures/example_video.mp4";  
Glide    
.with( context )  
.load( Uri.fromFile( new File( filePath ) ) )  
.into( imageViewGifAsBitmap );  
```
### 占位图  
 加载前占位图  
```
RequestOptions options = new RequestOptions()
             .placeholder(R.mipmap.ic_launcher);
Glide.with(this)
     .load(url)
     .apply(options)
     .into(imageView);
```  
异常占位图  
```
RequestOptions options = new RequestOptions()
        .error(R.drawable.error)
        .diskCacheStrategy(DiskCacheStrategy.NONE);
Glide.with(this)
        .load(url)
        .apply(options)
        .into(imageView);
```

后备回调符 fallback  
如果请求的url/model为null导致请求失败，fallback会优先于error调用，也就是说url为null时设置了error而没有设置fallback将继续显示error;  
反之，设置了fallback就会显示fallback;    


指定图片加载格式  
```
Glide.with(this)
        .asBitmap()  //  asGif()  asFile()  asDrawable()  
        .load(url)
        .apply(options)
        .into(imageView);
```
### 指定图片大小  
```
RequestOptions options = new RequestOptions()
        .override(100, 100);
Glide.with(this)
        .load(url)
        .apply(options)
        .into(imageView);
```
如果你想加载一张图片的原始尺寸的话，可以使用Target.SIZE_ORIGINAL关键字  
```
RequestOptions options = new RequestOptions()
        override(Target.SIZE_ORIGINAL);
Glide.with(this)
        .load(url)
        .apply(options)
        .into(imageView);
```
### 图片变换功能  
```
RequestOptions options = new RequestOptions()
        .circleCrop();
Glide.with(this)
     .load(url)
     .apply(options)
     .into(imageView);
```

```
<application>
   <meta-data
            android:name="com.bumptech.glide.integration.okhttp3.OkHttpLibraryGlideModule"
            android:value="GlideModule" />
</application>
```

### Glide缓存问题

```
Glide.with(this@BasicPhotoUploadActivity)
        .load(imgUrl)
        .signature(StringSignature(System.currentTimeMillis().toString()))
        .into(this)
```

### GlideModule  
```
annotationProcessor 'com.github.bumptech.glide:compiler:4.8.0'
kapt 'com.github.bumptech.glide:compiler:4.8.0'  

@GlideModule
public final class ExampleAppGlideModule extends AppGlideModule {}  
```
### recyclerView#滑动很快  
```
recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            LoggerUtils.e("initRecyclerView"+ "恢复Glide加载图片");
            Glide.with(ImageBrowseActivity.this).resumeRequests();
        }else {
            LoggerUtils.e("initRecyclerView"+"禁止Glide加载图片");
            Glide.with(ImageBrowseActivity.this).pauseRequests();
        }
    }
});
```
### Application  
```
onLowMemory(){
    Glide.cleanMemory();  
}  
onTrimMemory(){
    Glide.trimMemory();
}
```


