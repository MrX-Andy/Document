### Bitmap   
BitmapFactory.Options.inPreferredConfig;  
Bitmap.Config.RGB_ARGB_8888;  
图片上的每个像素块，A, R, G, B 四个通道，每个通道用8位表示，共32位, 4B；  
Bitmap.Config.ARGB_4444;  
图片上的每个像素块，A, R, G, B 四个通道，每个通道用4位表示，共16位, 2B；
Bitmap.Config.RGB_565;  
图片上的每个像素块，R, G, B 三个通道，两个字节中高5位表示红色通道，中间6位表示绿色通道，低5位表示蓝色通道，共16位, 2B；  
Bitmap.Config.ALPHA_8;  
图片上的每个像素块，Alpha 一个通道，存储的是图片8位的透明度值，共8位, 1B；  

Bitmap所占用字节大小  
图片高度 * 图片宽度 * 一个像素占用的内存大小;  
 
BitmapFactory.Options.inPurgeable  
设置为true时，表示系统内存不足时可以被回 收；  
设置为false时，表示不能被回收。  

BitmapFactory.Options.inJustDecodeBounds  
inJustDecodeBounds = true  只加载图片，获取宽高等信息，并不展示图片；  
inJustDecodeBounds = false 展示图片；    

BitmapFactory.Options.inSampleSize   
inSampleSize = 2; 就是 宽度只用1/2，高度只用 1/2，就是压缩展示， 占据内存会变成原来的 1/4;  


CompressFormat  
format:压缩格式,它有JPEG、PNG、WEBP三种选择，JPEG是有损压缩，PNG是无损压缩，WEBP是Google推出的图像格式.  
int quality：0~100可选，数值越大，质量越高，图像越大。  

### BitmapOption  
```
inBitmap——在解析Bitmap时重用该Bitmap，不过必须等大的Bitmap而且inMutable须为true
inMutable——配置Bitmap是否可以更改，比如：在Bitmap上隔几个像素加一条线段
inJustDecodeBounds——为true仅返回Bitmap的宽高等属性
inSampleSize——须>=1,表示Bitmap的压缩比例，如：inSampleSize=4，将返回一个是原始图的1/16大小的 Bitmap
inPreferredConfig——Bitmap.Config.ARGB_8888等
inDither——是否抖动，默认为false
inPremultiplied——默认为true，一般不改变它的值
inDensity——Bitmap的像素密度
inTargetDensity——Bitmap最终的像素密度
inScreenDensity——当前屏幕的像素密度
inScaled——是否支持缩放，默认为true，当设置了这个，Bitmap将会以inTargetDensity的值进行缩放
inPurgeable——当存储Pixel的内存空间在系统内存不足时是否可以被回收
inInputShareable——inPurgeable为true情况下才生效，是否可以共享一个InputStream
inPreferQualityOverSpeed——为true则优先保证Bitmap质量其次是解码速度
outWidth——返回的Bitmap的宽
outHeight——返回的Bitmap的高
inTempStorage——解码时的临时空间，建议16*1024
```



如果想做图片自适应, 又担心比较占据内存, 可以加上 maxWidth 和 maxHeight;   
否则可能会因为 wrap_content 导致图片过大, 引起内存飙升;   
### setImageURI不刷新   
解决：   
1、使用不同的文件名（不同的URI）   
2、使用setImageBitmap的方式代替   
```
Bitmap bmp;
try {
bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.fromFile(file));
} catch (Exception e) { 

}
```
iv.setImageBitmap(bmp);   
3、先制空，再刷新   
```
fun ImageView.setImageUrl(url: String?) {
    try {
        (url == null).yes { return }
        this.setImageURI(null)
        this.setImageURI(Uri.parse(url))
    } catch (ex: Exception) {
        ex.logW()
    }
}
```

### drawable  
我们将同一张图片放在不同 dpi 的 res/drawable 目录下，占用的内存也不一样;  
这是因为在 android 中 Bitmap.decodeResource() 会根据图片存放的目录做一次宽高的转换，具体公式如下:  
转换后高度 = 原图高度 * (设备的 dpi /目录对应的 dpi )  
转换后宽度 = 原图宽度 * (设备的 dpi / 目录对应的 dpi)  

假设你的手机 dpi 是 320（对应 xhdpi），你将上述的图片放在 xhdpi 目录下:  
图片占用内存 = 1080 * (320 / 320) * 480 * (320 / 320) * 4B = 1.98 M  


同样的手机，将上述图片放到 hdpi (240 dpi) 目录下:  
图片占用内存 = 1080 * (320 / 240) * 480 * (320 / 240) * 4B = 3.52 M  


如果需要查看手机 density 相关配置，可以使用如下命令:  
```
adb shell cat system/build.prop|grep density
```

### 参考  
https://www.jianshu.com/p/765e54b44cf6  
https://juejin.im/post/5b0d73116fb9a00a1c14c84e  
https://juejin.im/post/5a1bd6595188254cc067981f  
http://www.52im.net/thread-1208-1-1.html  
http://zhengxiaoyong.me/2017/04/23/%E4%B9%9F%E8%B0%88%E5%9B%BE%E7%89%87%E5%8E%8B%E7%BC%A9/  
https://www.zhihu.com/question/29355920  
https://www.jianshu.com/p/0f56f35068e2  
https://wangfuda.github.io/2017/07/09/nebula_gpu_monitor_optimize  
https://www.jianshu.com/p/d5714e8987f3  
https://meta.tn/a/eba0634889de40959b4e42b8deeaa0f46ed14ec86c21ddd680bf3a399978b39c  
https://juejin.im/post/5c56bd57f265da2dc5388f33  
