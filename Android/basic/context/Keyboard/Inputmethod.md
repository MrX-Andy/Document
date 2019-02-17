### 获取当前输入法 app

```
String currentInputmethod = Settings.Secure.getString(getContentResolver(),Settings.Secure.DEFAULT_INPUT_METHOD);
LogUtil.e("currentInputmethod = "+currentInputmethod);
E/LogUtil: [ (MainActivity.java:35)#getInputMethod ] currentInputmethod = com.tencent.qqpinyin/.QQPYInputMethodService
```
> 
```
获取手机里面的所有输入法
InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
// 取得当前所有的输入法
List<InputMethodInfo> infos = imm.getInputMethodList();
for (InputMethodInfo info : infos) {
    LogUtil.e("输入法包名：" + info.getPackageName());
}
? E/LogUtil: [ (MainActivity.java:32)#getInputMethod ] 输入法包名：com.tencent.qqpinyin
? E/LogUtil: [ (MainActivity.java:32)#getInputMethod ] 输入法包名：com.meizu.flyme.input
? E/LogUtil: [ (MainActivity.java:32)#getInputMethod ] 输入法包名：com.hathy.simplekeyboard
? E/LogUtil: [ (MainActivity.java:32)#getInputMethod ] 输入法包名：com.alex.keyboardview
```