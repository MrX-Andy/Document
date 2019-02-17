###  Launcher  
LauncherActivity中是以ListView来显示我们的应用图标列表的，并且为每个Item保存了应用的包名和启动Activity类名，  
这样点击某一项应用图标的时候就可以根据应用包名和启动Activity名称启动我们的App了。  
◆ 简述  
Zygote进程 →   
SystemServer 进程 # startOtherService 方法 →     
ActivityManagerService # systemReady 方法 →   startHomeActivityLocked 方法 →   
ActivityStackSupervisor # startHomeActivity 方法 →   
执行Activity的启动逻辑，执行 scheduleResumeTopActivities 方法  

因为是隐式的启动Activity，所以启动的Activity就是在AndroidManifest.xml中配置catogery的值为：   
```
<activity
    android:name="LauncherActivity"
    android:configChanges="orientation|keyboardHidden"
    android:screenOrientation="portrait"
    android:windowSoftInputMode="adjustResize|stateHidden">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />

        <category android:name="android.intent.category.LAUNCHER" />
        <category android:name="android.intent.category.HOME" />
        <category android:name="android.intent.category.DEFAULT" />
    </intent-filter>
</activity>
```  
◆ SystemServer#run  
```
// ... 186 Start services.  
try {
    traceBeginAndSlog("StartServices");
    startBootstrapServices();
    startCoreServices();
    startOtherServices();
    SystemServerInitThreadPool.shutdown();
} catch (Throwable ex) {
    throw ex;
} finally {
    traceEnd();
}
```
◆ SystemServer#startOtherServices  
```
private void startOtherServices() {
    // ... 1504  
    traceBeginAndSlog("StartLauncherAppsService");
    mSystemServiceManager.startService(LauncherAppsService.class);
    // ... 1626  
    mPowerManagerService.systemReady(mActivityManagerService.getAppOpsService());
    // ... 1672  
    mActivityManagerService.systemReady(() ->  
}
```
◆ ActivityManagerService#systemReady  
```
public void systemReady(final Runnable goingCallback, TimingsTraceLog traceLog) {
    //  ...  14148  
    traceLog.traceBegin("PhaseActivityManagerReady");
    //  ...  14282  
    startHomeActivityLocked(currentUserId, "systemReady");
}
```

◆ ActivityManagerService#startHomeActivityLocked  
```
boolean startHomeActivityLocked(int userId, String reason) {
    if (mFactoryTest == FactoryTest.FACTORY_TEST_LOW_LEVEL && mTopAction == null) {
        ...
        return false;
    }
    //  4066 
    Intent intent = getHomeIntent();
    ActivityInfo aInfo = resolveActivityInfo(intent, STOCK_PM_FLAGS, userId);
    if (aInfo != null) {
        intent.setComponent(new ComponentName(aInfo.applicationInfo.packageName, aInfo.name));
        aInfo = new ActivityInfo(aInfo);
        aInfo.applicationInfo = getAppInfoForUser(aInfo.applicationInfo, userId);
        ProcessRecord app = getProcessRecordLocked(aInfo.processName, aInfo.applicationInfo.uid, true);
        if (app == null || app.instr == null) {
            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NEW_TASK);
            final int resolvedUserId = UserHandle.getUserId(aInfo.applicationInfo.uid);
            final String myReason = reason + ":" + userId + ":" + resolvedUserId;
            mActivityStarter.startHomeActivityLocked(intent, aInfo, myReason);
        }
    } else {
        Slog.wtf(TAG, "No home screen found for " + intent, new Throwable());
    }
    return true;
}
```
◆ ActivityManagerService#getHomeIntent  
```
Intent getHomeIntent() {
    Intent intent = new Intent(mTopAction, mTopData != null ? Uri.parse(mTopData) : null);
    intent.setComponent(mTopComponent);
    intent.addFlags(Intent.FLAG_DEBUG_TRIAGED_MISSING);
    if (mFactoryTest != FactoryTest.FACTORY_TEST_LOW_LEVEL) {
        intent.addCategory(Intent.CATEGORY_HOME);
    }
    return intent;
}
```
### 参考  
https://blog.csdn.net/itachi85/article/details/56669808  




