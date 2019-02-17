### ADB环境变量  

● 没有环境变量  
C:\Users\alex_>adb  
'adb' 不是内部或外部命令，也不是可运行的程序  
或批处理文件。  

配置环境变量  

● 新建ADB变量：
变量名：  
ADB  
变量值：  
D:\Android\sdk\platform-tools  

● 编辑 path 变量：
添加 ;%ADB%  

### mac 配置 adb环境  
第一步 打开 finder  
shift + command + G 
输入  ~/Library/Android/sdk/platform-tools  

第二部 终端输入  
touch .bash_profile  
open -e .bash_profile  


追加  
export PATH=${PATH}:~/Library/Android/sdk/platform-tools  


### 常见命令  

● 查看栈顶的Activity  
adb shell dumpsys activity top  

● 计算MainActivity启动时间  
adb shell am start -W com.alex.andfun.basic/com.alex.andfun.basic.MainActivity  
如果只关心某个应用自身启动耗时，参考TotalTime；  
如果关心系统启动应用耗时，参考WaitTime；  
如果关心应用有界面Activity启动耗时，参考ThisTime；  

● 命令行指定设备 -s deviceName  
deviceName 用 adb devices 查看  
adb -s 79BMADR8WZA3F reverse tcp:8888 tcp:8888  


● 查看 dpi  
adb shell cat system/build.prop|grep density  

● 安装app  
adb devices    
adb -s emulator-5554 install    
adb -s emulator-5556 install ebook.apk   
// 安装测试 包  
adb install -t   

● 查看连接设备  
adb devices  

● 查看adb 版本信息  
adb version  

● 退出当前环境  
exit  

● 进入shell环境  
adb shell   

● 查看进程  
adb shell  
ps  
// 结果
USER    PID    PPID    VSIZE    RSS    WCHAN    PC    NAME    

● 查看指定包名的进程  
adb shell   
ps|grep com.alex.andfun.baisc  

● 按照包名 杀死进程  
adb shell am force-stop com.alex.andfun.basic  

● 列出app包名列表  
-3	  只显示第三方应用  
-s	  只显示系统应用  
adb shell pm list packages  -3

● 获取android_id  
adb shell settings get secure android_id  

● 获取MAC地址   
adb shell cat /sys/class/net/wlan0/address  

● 获取序列号   
adb get-serialno  

● 获取CPU信息  
adb shell cat /proc/cpuinfo  

● 更多硬件与系统属性  
adb shell cat /system/build.prop  

● 输入文本  
adb shell input text XXXX  

● 查看顶层 Activity 名字  
```
linux:
adb shell dumpsys activity | grep "mFocusedActivity"

windows:
adb shell dumpsys activity | findstr "mFocusedActivity"
```

★ log  
```
//  tag:level  
adb logcat -s "LogTrack:v" "LogTrack:v"
```

获取系统版本 // 7.0  
adb shell getprop ro.build.version.release  


获取系统api版本 // 28  
adb shell getprop ro.build.version.sdk  

重启手机  
adb shell setprop sys.powerctl reboot  

### 模拟器debug掉线  
```

adb kill-server adb start-server adb devices   


adb kill-server  
adb devices  
adb start-server  
```

真机debug error   
Warning: debug info can be unavailable.Please close other application using ADB:Monitor, DDMS, Eclipse  
1.. 杀掉 adb   
2.. 重启studio   
3.. 重启电脑  
4.. 重启手机（这个很搞笑，我居然重启手机成功了）  



### 参考  
Android Debug Bridge  
https://mp.weixin.qq.com/s/lIHFKU9uvKSxcPfi8NgOzQ  
https://github.com/mzlogin/awesome-adb 
https://developer.android.google.cn/studio/command-line/adb.html  
http://blog.csdn.net/u010818425/article/details/52266593  



