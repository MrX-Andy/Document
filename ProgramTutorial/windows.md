### 命令行  操作

[操作 进程](windows/shell_proccessor.md)  
[解决夜神 连接 android studio 掉线问题](windows/shell_restart_nox.md)  

★ cmd.exe 携带参数  
-c   
执行 String 指定的命令，执行完后停止，会退出  
C:\Windows\System32\cmd.exe /c taskkill /im java.exe -f  


★ 修改 cmd 字体字号  
cmd_dos_config.reg
```
Windows Registry Editor Version 5.00
[HKEY_CURRENT_USER\Console\%SystemRoot%_system32_cmd.exe]
"WindowSize"=dword:00170058
"ScreenBufferSize"=dword:01170058
"WindowPosition"=dword:0079004b
"ColorTable01"=dword:00235600
"FontSize"=dword:00160000
"FontWeight"=dword:00000190
"FaceName"="Consolas"
"FontFamily"=dword:00000036
```

★ 任务栏添加文件夹
新建一个快捷方式，修改属性  
C:\Windows\explorer.exe D:\WorkSpace  

★ 常见快捷键  
当前窗口最小化  Alt + 空格 + N  
所有窗口最小化  Win + M  
展开当前窗口至最大化    Win + ↑  
收回当前窗口至最小化    Win + ↓  
移动当前窗口到左边的显示器   Shift + Win + ←  
移动当前窗口到右边的显示器   Shift + Win + 右  
在一个已经打开的文件夹中，新建一个文件夹   Ctrl + Shift + N  
增强型鼠标右键    Shift + 鼠标右键  

★ 系统图标都在哪里  
C:\Windows\System32\shell32.dll  
  