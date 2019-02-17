### 基础用法  

单行字母个数  
Editor -> Code Style 右边-Default Option-Right margin columns 200  

空方法体-单行方法体, 代码折叠  
Editor -> General -> Code Folding 右边 One-line methods 选中时默认折叠; 勾掉时默认展开;  

不让出现过多代码提示   
Preferences | Editor | General | Code Completion  

仅仅保留:    
Basic Completion; Smart Type Completion; 
Sort suggestions alphabetically;  
Show suggestions as you type;  



 
定制 Toolbar  
如果 Toolbar 没有显示出来， View -> Toolbar  选中即可  
修改， 在 run 那一行，鼠标右键，点击 Customize Menus and Toolbar ,  点击 Main Toolbar  


Code面板在下面怎么办  
在Code面板, 任意一个tab标签上, 鼠标右键, 选择 Tabs Placement, 选择 top   

Code面板多行展示  
在Code面板, 任意一个tab标签上, 鼠标右键, 选择 Tabs Placement, 勾掉 Show Tabs in Single Row   

背景色  
设置 -> Editor -> Color Scheme -> General ， 右边 选择 Text -> Default text -> back ground  =  C7EDCC  

LogCat色值
```
VERBOSE	BBBBBB
DEBUG	  0070BB
INFO	48BB31
WARN	 BBBB23
ERROR	 FF0006
ASSERT	8F0005  
```

修改文件编码  
设置 -> Editor -> File Encodings  


编辑不区分大小写  
设置 -> Editor -> General -> Code Completion , 右边 case sensitive completion 选择 none  


不让每次都直接进入工程  
设置 -> Appearance & Behavior -> System Settings , 右边 Start up/Shutdown  Open last project on startup  勾掉  


自动导包  
设置 -> Editor -> General -> Auto Import 右边  
勾掉  Optimize imports on the fly  自动优化 import     
勾掉  Add unambiguous import s on the fly  自动添加 import   


不让提示文档实时出现  
设置 -> Editor -> General -> Code Completion 右边  
勾掉  code completion Auto popup documentation in ms   


鼠标悬浮显示  
设置 -> Editor -> General 右边  
勾掉  other  Show quick doc on mouse move Delay ms    


代码不换行 单行显示  
设置 -> Editor -> Code Style -> Java 右边  
wrapping and  braces  
Keep when reformatting  
勾掉  Line breaks  


显示行号  
设置 -> Editor -> General -> Appearances  右边  
勾上  Show line numbers    

换字体  
设置 -> Editor -> Code & Fonts 右边  


类名注释模板  
设置 -> Editor -> File and code Templates  右边  Include  File  Header  
```
/**  * 作者：Alex  
 * 时间：${DATE}${HOUR}:${MINUTE}  
 * 简述：  
 */    
```

换SDK  
File，选择Other Settings，选择Default Project Structure...，就可以看到JDK和SDK的设置地方了。    


隐藏 文件夹导航  
View  ->  Navigation Bar  

打开连接  
```
// @http://bbs.csdn.net/topics/390797771
/**
 * @启动者: {@link MainActivity}
 * 单聊:   {@link com.alex.alexchat.activity.chatroom.ChatRoomActivity}
 */
```

Gradle 依赖冲突  
```
androidTestCompile('com.android.support.test.espresso:espresso-core:2.2.2', {     
    exclude group: 'com.android.support', module: 'support-annotations' }) 
    compile ('com.facebook.react:react-native:+') {     
        exclude group: 'com.squareup.okhttp3', module: 'okhttp'    
        exclude group: 'com.android.support', module: 'support-v4'     
        exclude group: 'com.android.support', module: 'support-v7'
    }
}
```  
