### 静态注册  

```
//  网络状态变化 监听网络状态变化的广播需要添加权限  
<uses-permission android:name="android:permission.ACCESS_NETWORK_STATE" />

//  开机启动广播 使用开机启动广播需要添加权限：
<uses-permission android:name="android:permission.RECEIVE_BOOT_COMPLETED" />


```
清单文件  
```
<receiver
    android:name=".back.DownloadAssistantBroadcastReceiver"
    android:enabled="true"
    android:exported="true"

    android:permission="android.permission.RECEIVE_BOOT_COMPLETED">
    <intent-filter>
        <category android:name="android.intent.category.LAUNCHER"/>
        <!--手机开机 广播-->
        <action android:name="android.intent.action.BOOT_COMPLETED"/>
        <!-- 手机屏幕解锁 广播 -->
        <action android:name="android.intent.action.USER_PRESENT"/>
        <!--手机屏幕 关闭  广播-->
        <action android:name="android.intent.action.SCREEN_OFF"/>
        <!--手机屏幕 点亮  广播-->
        <action android:name="android.intent.action.SCREEN_ON"/>
        <!--安装 app 广播-->
        <action android:name="android.intent.action.PACKAGE_ADDED"/>
        <!--卸载 app 广播-->
        <action android:name="android.intent.action.PACKAGE_REMOVED"/>
        <!--位置变化 广播-->
        <action android:name="android.intent.action.LOCALE_CHANGED"/>
        <!--接通充电器 广播-->
        <action android:name="android.intent.action.ACTION_POWER_CONNECTED"/>
        <!--断开充电器 广播-->
        <action android:name="android.intent.action.ACTION_POWER_DISCONNECTED"/>
        <action android:name="android.intent.action.TIME_SET"/>
        <!--计时器变化-->
        <action android:name="android.intent.action.TIME_TICK"/>
        <!--时区发生变化-->
        <action android:name="android.intent.action.TIMEZONE_CHANGED"/>
        <!--电量发生变化-->
        <action android:name="android.intent.action.BATTERY_CHANGED"/>
        <!--按下照相时的拍照按键-->
        <action android:name="android.intent.action.CAMERA_BUTTON"/>
        <!--屏幕超时，进行锁屏时-->
        <action android:name="android.intent.action.CLOSE_SYSTEM_DIALOGS"/>
        <!--设备当前设置被改变时-->
        <action android:name="android.intent.action.CONFIGURATION_CHANGED"/>
        <!--插上耳机-->
        <action android:name="android.intent.action.HEADSET_PLUG"/>
        <!--墙壁纸发生改变-->
        <action android:name="android.intent.action.WALLPAPER_CHANGED"/>

    </intent-filter>
</receiver>
```

java 代码  
```
@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
@SuppressLint("ObsoleteSdkInt")
class DownloadUtil {
    private final static Map<String, String> ACTION_MAP = new HashMap<>();

    static {
        ACTION_MAP.put(Intent.ACTION_BOOT_COMPLETED, "开机完成");
        ACTION_MAP.put(Intent.ACTION_USER_PRESENT, "屏幕解锁完成");
        ACTION_MAP.put(Intent.ACTION_SCREEN_ON, "屏幕点亮");
        ACTION_MAP.put(Intent.ACTION_SCREEN_OFF, "屏幕关闭");
        ACTION_MAP.put(Intent.ACTION_PACKAGE_ADDED, "安装App");
        ACTION_MAP.put(Intent.ACTION_PACKAGE_REMOVED, "卸载App");
        ACTION_MAP.put(Intent.ACTION_LOCALE_CHANGED, "位置发生变化");
        ACTION_MAP.put(Intent.ACTION_POWER_CONNECTED, "接通充电器");
        ACTION_MAP.put(Intent.ACTION_POWER_DISCONNECTED, "断开充电器");
        ACTION_MAP.put(Intent.ACTION_BATTERY_CHANGED, "电量发生变化");
        ACTION_MAP.put(Intent.ACTION_TIME_CHANGED, "时间发生变化");
        ACTION_MAP.put(Intent.ACTION_TIME_TICK, "计时器变化");
        ACTION_MAP.put(Intent.ACTION_CAMERA_BUTTON, "按下照相时的拍照按键");
        ACTION_MAP.put(Intent.ACTION_TIMEZONE_CHANGED, "时区发生变化");
        ACTION_MAP.put(Intent.ACTION_CLOSE_SYSTEM_DIALOGS, "屏幕超时，进行锁屏时");
        ACTION_MAP.put(Intent.ACTION_CONFIGURATION_CHANGED, "设备当前设置被改变时");
        ACTION_MAP.put(Intent.ACTION_HEADSET_PLUG, "插上耳机");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            ACTION_MAP.put(Intent.ACTION_WALLPAPER_CHANGED, "墙壁纸发生改变");
        }

    }

    static String getActionCnName(String action) {
        LogTrack.w("action = " + action);
        return ACTION_MAP.get(action);
    }
}
```

广播接收者  
```
public class DownloadAssistantBroadcastReceiver extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        LogTrack.i("onReceive  " + DownloadUtil.getActionCnName(intent.getAction()));
    }


    @Override
    public IBinder peekService(Context myContext, Intent service) {
        LogTrack.i("peekService");
        return super.peekService(myContext, service);
    }
}
```
### 动态注册  
```
IntentFilter _Filter = new IntentFilter();
_Filter.setPriority(0x7fffffff);
//系统广播: 开屏
_Filter.addAction(Intent.ACTION_USER_PRESENT);
//系统广播: 开机
_Filter.addAction(Intent.ACTION_BOOT_COMPLETED);
//系统广播: USB插拔
_Filter.addAction(Intent.ACTION_POWER_CONNECTED);
_Filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
//系统广播: 电池信息变化
_Filter.addAction(Intent.ACTION_BATTERY_CHANGED);
//系统广播: 网络切换
_Filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
pContext.getApplicationContext().registerReceiver(mTestBroadcastReceiver, _Filter);
```

### 电量变化  
```
int currentBatteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);//当前电量
int totalLevel = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 1);//总电量
double percent = currentBatteryLevel * 100.0 / totalLevel;
```
立刻获取  
```
Intent intent = getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
int currentBatteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);//当前电量
int totalLevel = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 1);//总电量
double percent = currentBatteryLevel * 100.0 / totalLevel;
```