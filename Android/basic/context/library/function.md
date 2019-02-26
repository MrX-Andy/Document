Activity 常用方法  
设置全屏  
```
getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
```

去掉 Activity 标题  
```
requestWindowFeature(Window.FEATURE_NO_TITLE);  
getActionBar().hide();  
```

重启当前Activity    
```
Intent intent = getIntent();  
intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);  
overridePendingTransition(0, 0);  
finish();  
startActivity(intent);  
overridePendingTransition(0, 0);  
```
### 物理按键相关
点击 手机返回键, 切换后台, 不是finish  
```
@Override
public void onBackPressed()
{
	//实现Home键效果 
	//super.onBackPressed();这句话一定要注掉,不然又去调用默认的back处理方式了 
	Intent intent= new Intent(Intent.ACTION_MAIN);
	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	intent.addCategory(Intent.CATEGORY_HOME);
	startActivity(intent);
}
```
捕获菜单键  
```
@Override
public boolean onKeyDown(int keyCode, KeyEvent event)
{
	if(keyCode==KeyEvent.KEYCODE_MENU){
		dl.openDrawer(right);	
	}
	return super.onKeyDown(keyCode, event);
}
```
屏蔽返回键, 自定义返回键  
```
/**最好用这个 方式, 防止上一个activity也finish掉, 并注释掉 super 防止系统先响应*/
@Override
public void onBackPressed()
{
	//super.onBackPressed();
	startHomeOrBuyActivity();
}
```

屏蔽返回键  
```
@Override
public boolean onKeyDown(int keyCode, KeyEvent event)
{
	if(keyCode==KeyEvent.KEYCODE_BACK ){

		return true;
	}
	return super.onKeyDown(keyCode, event);
}
```
### SingleTask 模式 Intent传值
```
@Override
protected void onCreate(Bundle savedInstanceState)
{
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_login);
	getIntentData();
}
@Override  
protected void onNewIntent(Intent intent) {        
	super.onNewIntent(intent);  
	setIntent(intent);  
	getIntentData();
}
private void getIntentData()
{
	String moduleName = getIntent().getStringExtra("moduleName");
	String phoneNum = getIntent().getStringExtra("phoneNum");
	String pwd = getIntent().getStringExtra("pwd");	 
}
```
### startActivityForResult   
启动者  
```
private static final int requestCodeLoginOK = 100;

Intent intent = new Intent(context, LoginActivity.class);
startActivityForResult(intent, requestCodeLoginOK);

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data)
{
	super.onActivityResult(requestCode, resultCode, data);
	if(data == null){
		return;
	}
	if(requestCode == requestCodeVersion){
			
	}
}
```

启动项   
Intent intent = new Intent(context, IndexActivity.class);  
setResult(Activity.RESULT_OK, intent);   
finish();  

注意  
启动一个新的Activity, 可以在它finish的时候, 得到想要的 result;    
在当前的Activity的onActivityResult方法回调中得到这些数据,     
如果想要使用startActivityForResult, 对应的requestCode必须 >= 0 ;    

### 点击其他区域键盘隐藏
```
// 以下代码 写在 Activity 
@Override
public boolean onTouchEvent(MotionEvent event) {
	if (event.getAction() == MotionEvent.ACTION_DOWN) {
		return hiddenKeyPad();
	}
	return false;
}
/**
 * 可以  关闭软键盘
 * 键盘 展示出来, 则可以关闭,  返回 true;
 * 键盘 没有展示出来, 则不可以关闭,  返回 false;
 */
public boolean hiddenKeyPad() {
	return KeyPadUtil.getInstance().hiddenKeyPad(activity);
}

@Override
protected void onStop() {
	super.onStop();
	hiddenKeyPad();
	ToastUtil.cancel();
}
```
### 不同App 的 Activity 之间的传值  
A App:   
```
<activity
		android:name=".activity.FloatIndicatorActivity"
		android:exported="true"
		android:launchMode="singleTop"
		android:screenOrientation="portrait">
		<intent-filter>
			<action android:name="com.alex.floatindicator.activity.FloatIndicatorActivity" />

			<data android:scheme="params" />

			<category android:name="android.intent.category.DEFAULT" />
		</intent-filter>
</activity>

```
	
B App:   
```
Intent intent = new Intent("com.alex.floatindicator.activity.FloatIndicatorActivity", 
Uri.parse("params://111"));
startActivity(intent);
```  

A App:   
```
<activity android:name=".order.OrderActivity">
    <intent-filter>
        <data
            android:host="@string/android_host"
            android:path="/order"
            android:port="@string/android_port"
            android:scheme="@string/android_scheme" />
        <!-- 一下几行必须设置 -->
        <action android:name="android.intent.action.VIEW" /> <!-- 隐式调用必须声明 -->
        <category android:name="android.intent.category.DEFAULT" /> <!-- 隐式调用必须声明 -->
        <category android:name="android.intent.category.BROWSABLE" /> <!-- BROWSABLE的意思就是浏览器在特定条件下可以打开你的Activity -->
    </intent-filter>
</activity>  
```  
```
<string name="android_scheme">andfun</string>
<string name="android_host">router</string>
<string name="android_port">9527</string>
```
uri =  schema://host:port/path?key0=value0&key1=value1   
例如 andfun://router:9527/order   


### App假装进程包活
```
/*我能做到的, 仅仅是让App存活的稍微久一点, 仅此而已*/
/*android:alwaysRetainTaskState="true" 只在 入口在 Activity 有效*/
<activity
	android:name=".ui.MainActivity"
	android:alwaysRetainTaskState="true"
    android:clearTaskOnLaunch="false"
	android:theme="@style/alex_theme_cold_start"
	>
	<intent-filter>
		<action android:name="android.intent.action.MAIN"/>

		<category android:name="android.intent.category.LAUNCHER"/>
	</intent-filter>
</activity>
		
public class MainActivity extends BaseActivity{
    @Override
    public void onBackPressed()
    {
		/*写在主页 ,  按返回键返回桌面, 不结束Activity*/
        moveTaskToBack(true);
    }
}
```
    
### 关于墙壁纸
获取当前壁纸  
```
WallpaperManager wm=WallpaperManager.getInstance(this);
Drawable wallpaper=wpm.getDrawable();
```
设置当前壁纸  
```
imapaper.setDrawingCacheEnabled(true);
Drawable drawale=this.getResources().getDrawable(R.drawable.bg);
imapaper.setImageDrawable(drawale);
wpm.setBitmap(imapaper.getDrawingCache());
``` 

```
<uses-permission android:name="android.permission.SET_WALLPAPER"/>
```
### 关于输入法
强制, Activity第一次不打开输入法  
```
<activity
		android:name="com.subzero.userman.LoginActivity"
		android:configChanges="orientation|keyboardHidden"
		android:windowSoftInputMode="adjustUnspecified|stateHidden" 
		
		android:launchMode="singleTop"
		android:screenOrientation="portrait"
/>
```
强制, Activity第一次打开输入法: 只能调用一次, 只有一次有效  
```
getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
```
强制, android 输入法弹出 把布局顶上去: （不让它上去）  
```
<activityandroid:name=".activity.HomeActivity"
	android:windowSoftInputMode="adjustPan|stateHidden"
/>
```
强制, android 输入法弹出 把布局顶上去: （让它上去）  
```
<activity
	android:name="com.subzero.userman.LoginActivity"
	android:launchMode="singleTop"
	android:windowSoftInputMode="adjustResize"
	android:screenOrientation="portrait" />
```

禁止Activity截图  
```
getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);  
setContentView(getLayoutResId());
```
保持屏幕常亮  
```
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
}

/**
 * 这个方法的好处是不像唤醒锁（wake locks）, 需要一些特定的权限（permission）。  
  * 并且能正确管理不同app之间的切换, 不用担心无用资源的释放问题。 
  */
```
### 横屏透明的Activity

```
 <activity
            android:name="com.ubisys.ubisyssafety.activity.DialogInfoActivity"
            android:screenOrientation="landscape"
            android:theme="@style/activity_landscape" />
			
//style_activity_landscape.xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="activity_landscape" parent="@android:Theme.Dialog">
        <item name="android:windowFrame">@null</item>
        <item name="android:windowBackground">@drawable/shape_activity_landscape</item>
        <item name="android:windowNoTitle">true</item>
        <item name="android:windowIsFloating">true</item>
        <item name="android:windowContentOverlay">@null</item>
    </style>
</resources>
//shape_activity_landscape.xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle" >
    <solid android:color="#009ACD" />
    <corners android:radius="5dip" />
</shape>
```

### 横竖屏  
保持竖屏  
```
setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
```
自动  
```
setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
setRequestedOrientation(isPortrait() ? 
ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
```

### 参考  
activity-alias  
https://yrom.net/blog/2012/10/20/activity-enabled-at-platform-specific-version/  
