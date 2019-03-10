### 传值大小限制
Intent 中的 Bundle 是使用 Binder 机制进行数据传送的, 数据会写到内核空间, Binder 缓冲区域;  
Binder 的缓冲区是有大小限制的, 有些 ROM 是 1M, 有些 ROM 是 2M;  
这个限制定义在 frameworks/native/libs/binder/processState.cpp 类中, 如果超过这个限制, 系统就会报错;  
```  
#define BINDER_VM_SIZE ((1*1024*1024) - (4096 *2)) ; 
```
因为 Binder 本身就是为了进程间频繁-灵活的通信所设计的, 并不是为了拷贝大量数据;  

而一个进程默认有 16 个 Binder 线程, 所以一个线程能占用的缓冲区就更小了, 有人以前做过测试, 大约一个线程可以占用 128 KB;    
所以当你看到 The Binder transaction failed because it was too large 这类 TransactionTooLargeException 异常;  

### 隐式意图 - 显式意图   
❀ 显式意图  
按名称(完全限定类名)指定要启动的组件, 通常会应用中使用显式 Intent 来启动组件, 这是因为你知道要启动的 Activity 或服务的类名;  
例如, 启动新 Activity 以响应用户操作, 或者启动服务以在后台下载文件;  

❀ 隐式意图  
不会指定特定的组件, 而是声明要执行的常规操作, 从而允许其他应用中的组件来处理它;   
例如, 如需在地图上向用户显示位置, 则可以使用隐式 Intent, 请求另一具有此功能的应用在地图上显示指定的位置;  
创建隐式 Intent 时, Android 系统通过将 Intent 的内容与在设备上其他应用的清单文件中声明的 Intent 过滤器进行比较, 从而找到要启动的相应组件;  
如果 Intent 与 Intent 过滤器匹配, 则系统将启动该组件, 并向其传递 Intent 对象; 如果多个 Intent 过滤器兼容, 则系统会显示一个对话框, 支持用户选取要使用的应用;  
```
// Create the text message with a string
Intent sendIntent = new Intent();
sendIntent.setAction(Intent.ACTION_SEND);
sendIntent.putExtra(Intent.EXTRA_TEXT, textMessage);
sendIntent.setType("text/plain");

// Verify that the intent will resolve to an activity
if (sendIntent.resolveActivity(getPackageManager()) != null) {
    startActivity(sendIntent);
}
```  
❀ 接收隐式 Intent  
公布应用可以接收哪些隐式 Intent, 请在清单文件中使用 <intent-filter> 元素为每个应用组件声明一个或多个 Intent 过滤器;  
每个 Intent 过滤器均根据 Intent 的操作, 数据和类别指定自身接受的 Intent 类型, 仅当隐式 Intent 可以通过 Intent 过滤器之一传递时, 系统才会将该 Intent 传递给应用组件;  
每个 Intent 过滤器均由应用清单文件中的 <intent-filter> 元素定义, 在 <intent-filter> 内部, 您可以使用以下三个元素中的一个或多个指定要接受的 Intent 类型:   
<action>  在 name 属性中, 声明接受的 Intent 操作;该值必须是操作的文本字符串值, 而不是类常量;  
<data>  使用一个或多个指定数据 URI 各个方面(scheme, host, port, path 等)和 MIME 类型的属性, 声明接受的数据类型;  
<category>  在 name 属性中, 声明接受的 Intent 类别;该值必须是操作的文本字符串值, 而不是类常量;  
```
<activity android:name="ShareActivity">
    <intent-filter>
        <action android:name="android.intent.action.SEND"/>
        <category android:name="android.intent.category.DEFAULT"/>
        <data android:mimeType="text/plain"/>
    </intent-filter>
</activity>
```
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

### 系统Intent介绍
```
回到Home窗口
Intent intent = new Intent("android.intent.action.MAIN");
intent.addCategory("android.intent.category.HOME");
显示系统设置主界面
Intent intent = new Intent("android.settings.SETTINGS");
设置无线网: 
Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
浏览网页
Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.baidu.com"));
直接拨打拨打电话:
Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:12345678"));
将电话号码传入拨号App   
Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:12345678"));
调用拨号App
Intent intent = new Intent("com.android.phone.action.TOUCH_DIALER");
查看联系人
Intent intent = new Intent("com.android.contacts.action.LIST_CONTACTS");
编辑短信＋发送SMS/MMS
Intent intent = new Intent(Intent.ACTION_VIEW);
　intent.putExtra("sms_body", "The SMS text");
intent.setType("vnd.android-dir/mms-sms");
startActivintenty(intent);

发送短信 
Uri uri = Uri.parse("smsto:0800000123");
Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
intent.putExtra("sms_body", "The SMS text");
startActivintenty(intent);
发送彩信 Uri uri = Uri.parse("content://media/external/images/media/23");
Intent intent = new Intent(Intent.ACTION_SEND);
intent.putExtra("sms_body", "some text");
intent.putExtra(Intent.EXTRA_STREAM, uri);
intent.setType("image/png");
startActivintenty(intent);
发送Email
Uri uri = Uri.parse("mailto:xxx@abc.com");
Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
　startActivintenty(intent);
Intent intent = new Intent(Intent.ACTION_SEND);
　intent.putExtra(Intent.EXTRA_EMAIL, "me@abc.com");
intent.putExtra(Intent.EXTRA_TEXT, "The email body text");
intent.setType("text/plain");
startActivintenty(Intent.createChooser(intent, "Choose Email Client"));
Intent intent=new Intent(Intent.ACTION_SEND);
String[] tos={"me@abc.com"};
String[] ccs={"you@abc.com"};
intent.putExtra(Intent.EXTRA_EMAIL, tos);
intent.putExtra(Intent.EXTRA_CC, ccs);
intent.putExtra(Intent.EXTRA_TEXT, "The email body text");
intent.putExtra(Intent.EXTRA_SUBJECT, "The email subject text");
intent.setType("message/rfc822");
startActivintenty(Intent.createChooser(intent, "Choose Email Client"));
添加附件 
Intent intent = new Intent(Intent.ACTION_SEND);
intent.putExtra(Intent.EXTRA_SUBJECT, "The email subject text");
intent.putExtra(Intent.EXTRA_STREAM, "file:///sdcard/mysong.mp3");
sendIntent.setType("audio/mp3");
startActivintenty(Intent.createChooser(intent, "Choose Email Client"));
播放多媒体
Intent intent = new Intent(Intent.ACTION_VIEW);
Uri uri = Uri.parse("file:///sdcard/song.mp3");
intent.setDataAndType(uri, "audio/mp3");
startActivintenty(intent);
Uri uri = Uri.wintenthAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "1");
Intent intent = new Intent(Intent.ACTION_VIEW, uri);
startActivintenty(intent);

启动处理音频的程序
Intent audioIntent = new Intent(Intent.ACTION_GET_CONTENT);
audioIntent.setType("audio/*");
startActivity(Intent.createChooser(audioIntent, "选择音频程序"));

卸载程序 
Uri uri = Uri.fromParts("package", strPackageName, null);
Intent intent = new Intent(Intent.ACTION_DELETE, uri);
startActivintenty(intent);


其他
10.    ACTION_DATE_SETTINGS :            //  跳转日期时间设置界面               Intent intent =  new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);               startActivity(intent);   11.    ACTION_DEVICE_INFO_SETTINGS  :     // 跳转手机状态界面                  Intent intent =  new Intent(Settings.ACTION_DEVICE_INFO_SETTINGS);               startActivity(intent);   12.    ACTION_DISPLAY_SETTINGS  :         // 跳转手机显示界面               Intent intent =  new Intent(Settings.ACTION_DISPLAY_SETTINGS);               startActivity(intent);   13.    ACTION_DREAM_SETTINGS     【API 18及以上 没测试】               Intent intent =  new Intent(Settings.ACTION_DREAM_SETTINGS);               startActivity(intent);     14.    ACTION_INPUT_METHOD_SETTINGS :     // 跳转语言和输入设备               Intent intent =  new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);               startActivity(intent);   15.    ACTION_INPUT_METHOD_SUBTYPE_SETTINGS  【API 11及以上】  //  跳转 语言选择界面 【多国语言选择】                Intent intent =  new Intent(Settings.ACTION_INPUT_METHOD_SUBTYPE_SETTINGS);                startActivity(intent);   16.    ACTION_INTERNAL_STORAGE_SETTINGS         // 跳转存储设置界面【内部存储】                Intent intent =  new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);                startActivity(intent);         或者:            ACTION_MEMORY_CARD_SETTINGS    :    // 跳转 存储设置 【记忆卡存储】                Intent intent =  new Intent(Settings.ACTION_MEMORY_CARD_SETTINGS);                startActivity(intent);      17.    ACTION_LOCALE_SETTINGS  :          // 跳转语言选择界面【仅有English 和 中文两种选择】                   Intent intent =  new Intent(Settings.ACTION_LOCALE_SETTINGS);                 startActivity(intent);     18.     ACTION_LOCATION_SOURCE_SETTINGS :    //  跳转位置服务界面【管理已安装的应用程序;】                 Intent intent =  new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);                 startActivity(intent);   19.    ACTION_NETWORK_OPERATOR_SETTINGS :  // 跳转到 显示设置选择网络运营商;                 Intent intent =  new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);                 startActivity(intent);                 20.    ACTION_NFCSHARING_SETTINGS  :        // 显示NFC共享设置; 【API 14及以上】                 Intent intent =  new Intent(Settings.ACTION_NFCSHARING_SETTINGS);                 startActivity(intent);   21.    ACTION_NFC_SETTINGS  :            // 显示NFC设置;这显示了用户界面,允许NFC打开或关闭;  【API 16及以上】                 Intent intent =  new Intent(Settings.ACTION_NFC_SETTINGS);                 startActivity(intent);   22.    ACTION_PRIVACY_SETTINGS :        //  跳转到备份和重置界面                 Intent intent =  new Intent(Settings.ACTION_PRIVACY_SETTINGS);                 startActivity(intent);   23.    ACTION_QUICK_LAUNCH_SETTINGS  :  // 跳转快速启动设置界面                  Intent intent =  new Intent(Settings.ACTION_QUICK_LAUNCH_SETTINGS);                  startActivity(intent);   24.    ACTION_SEARCH_SETTINGS    :     // 跳转到 搜索设置界面                  Intent intent =  new Intent(Settings.ACTION_SEARCH_SETTINGS);                  startActivity(intent);   25.    ACTION_SECURITY_SETTINGS  :      // 跳转到安全设置界面                  Intent intent =  new Intent(Settings.ACTION_SECURITY_SETTINGS);                  startActivity(intent);   26.    ACTION_SETTINGS   :                 // 跳转到设置界面                   Intent intent =  new Intent(Settings.ACTION_SETTINGS);                   startActivity(intent);   27.   ACTION_SOUND_SETTINGS                // 跳转到声音设置界面                     Intent intent =  new Intent(Settings.ACTION_SOUND_SETTINGS);                    startActivity(intent);   28.   ACTION_SYNC_SETTINGS :              // 跳转账户同步界面                   Intent intent =  new Intent(Settings.ACTION_SYNC_SETTINGS);                   startActivity(intent);   29.     ACTION_USER_DICTIONARY_SETTINGS :   //  跳转用户字典界面                   Intent intent =  new Intent(Settings.ACTION_USER_DICTIONARY_SETTINGS);                   startActivity(intent);   30.     ACTION_WIFI_IP_SETTINGS  :          // 跳转到IP设定界面                    Intent intent =  new Intent(Settings.ACTION_WIFI_IP_SETTINGS);                    startActivity(intent);   31.     ACTION_WIFI_SETTINGS  :             //  跳转Wifi列表设置
其他2
  Intent intent = new Intent("/");  
 /*只修改 第二个参数*/
    ComponentName cm = new ComponentName("com.android.settings","com.android.settings.WirelessSettings");  
    intent.setComponent(cm);  
    intent.setAction("android.intent.action.VIEW");  
activity.startActivityForResult( intent , 0);  
com.android.settings.AccessibilitySettings 辅助功能设置 　　com.android.settings.ActivityPicker 选择活动 　　com.android.settings.ApnSettings APN设置 　　com.android.settings.ApplicationSettings 应用程序设置 　　com.android.settings.BandMode 设置GSM/UMTS波段 　　com.android.settings.BatteryInfo 电池信息 　　com.android.settings.DateTimeSettings 日期和坝上旅游网时间设置 　　com.android.settings.DateTimeSettingsSetupWizard 日期和时间设置 　　com.android.settings.DevelopmentSettings 应用程序设置=》开发设置 　　com.android.settings.DeviceAdminSettings 设备管理器 　　com.android.settings.DeviceInfoSettings 关于手机 　　com.android.settings.Display 显示——设置显示字体大小及预览 　　com.android.settings.DisplaySettings 显示设置 　　com.android.settings.DockSettings 底座设置 　　com.android.settings.IccLockSettings SIM卡锁定设置 　　com.android.settings.InstalledAppDetails 语言和键盘设置 　　com.android.settings.LanguageSettings 语言和键盘设置 　　com.android.settings.LocalePicker 选择手机语言 　　com.android.settings.LocalePickerInSetupWizard 选择手机语言 　　com.android.settings.ManageApplications 已下载(安装)软件列表 　　com.android.settings.MasterClear 恢复出厂设置 　　com.android.settings.MediaFormat 格式化手机闪存 　　com.android.settings.PhysicalKeyboardSettings 设置键盘 　　com.android.settings.PrivacySettings 隐私设置 　　com.android.settings.ProxySelector 代理设置 　　com.android.settings.RadioInfo 手机信息 　　com.android.settings.RunningServices 正在运行的程序(服务) 　　com.android.settings.SecuritySettings 位置和安全设置 　　com.android.settings.Settings 系统设置 　　com.android.settings.SettingsSafetyLegalActivity 安全信息 　　com.android.settings.SoundSettings 声音设置 　　com.android.settings.TestingSettings 测试——显示手机信息, 电池信息, 使用情况统计, Wifi information, 服务信息 　　com.android.settings.TetherSettings 绑定与便携式热点 　　com.android.settings.TextToSpeechSettings 文字转语音设置 　　com.android.settings.UsageStats 使用情况统计 　　com.android.settings.UserDictionarySettings 用户词典 　　com.android.settings.VoiceInputOutputSettings 语音输入与输出设置 　　com.android.settings.WirelessSettings 无线和网络设置

```

设置  
```
打开设置-应用管理-应用详情
public static void start4ApplicationDetailSettings(String packageName) {     Uri packageURI = Uri.parse("package:" + AppUtil.getAppPackageName());     Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);     startActivity(intent);     return; }

辅助功能  
Intent intent =  new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS); startActivity(intent);

网络设置  
Intent intent =  new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS); startActivity(intent);

APN设置  
Intent intent =  new Intent(Settings.ACTION_APN_SETTINGS); startActivity(intent);

开发人员选项  
Intent intent =  new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS); startActivity(intent);

应用列表-全部  
Intent intent =  new Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS); startActivity(intent);

蓝牙设置
Intent intent =  new Intent(Settings.ACTION_BLUETOOTH_SETTINGS); startActivity(intent);
```
### 参考    
https://developer.android.com/guide/components/intents-common  
https://www.jianshu.com/p/c0eed3b2a473  

