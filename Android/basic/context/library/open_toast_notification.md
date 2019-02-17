### 关于展示吐司和通知

通知是否打开（通知关闭 无法展示吐司）

```
/**
 * 通知是否打开（通知关闭 无法展示吐司）
 * */
public static boolean isNotificationEnabled() {
    String CHECK_OP_NO_THROW = "checkOpNoThrow";
    String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    AppOpsManager mAppOps = (AppOpsManager) context().getSystemService(Context.APP_OPS_SERVICE);
    ApplicationInfo appInfo = context().getApplicationInfo();
    String pkg = context().getPackageName();
    int uid = appInfo.uid;

    Class appOpsClass = null;
    /* Context.APP_OPS_MANAGER */
    try {
        appOpsClass = Class.forName(AppOpsManager.class.getName());
        Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);
        Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

        int value = (Integer) opPostNotificationValue.get(Integer.class);
        return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    } catch (NoSuchMethodException e) {
        e.printStackTrace();
    } catch (NoSuchFieldException e) {
        e.printStackTrace();
    } catch (InvocationTargetException e) {
        e.printStackTrace();
    } catch (IllegalAccessException e) {
        e.printStackTrace();
    }
    return false;
}


public static void start4ApplicationDetailSettings(String packageName) {
    Uri packageURI = Uri.parse("package:" + AppUtil.getAppPackageName());
    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
    startActivity(intent);
    return;
}
```