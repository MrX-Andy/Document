### Linux  

[命令](cmd/cmd.md)   
[shell](shell/shell.md)  
 
### linux目录介绍  
```
 /bin  存放linux常用命令
 /boot  存放系统启动时用到的程序
 /dev  包含了linux系统中使用的所有外设
 /etc  系统管理时用到的配置文件和配置子目录
 /home  存放用户
 /lib  所有程序的文件库
 /lost+found  非正常关机时，存放数据
 /mnt  外来文件临时挂载处
 /proc  在该目录下能获取到系统信息
 /root  超级用户的主目录
 /sbin  存放管理员的系统管理程序
 /tmp  存放程序执行时产生的临时文件
 /usr  所有用户程序和文件都存放在此
```
### 常用命令  
```
# 表示命令行注释  

删除文件
rm -rf filePath
-f  force 
-i  interactive  #进行交互式 删除
-r -R  recursive  #递归删除所有子目录，子文件
-v  verbose  #详细地进行的步骤
```  
### 目录和文件  
```
ls  #显示文件或目录
pwd  #当前工作目录 路径
cd  #切换目录
cd  /   #切换到根目录
cd  ~  #去主目录，home目录
cd  ..  #返回上级目录	
cd  ../..  #返回两级

打开文件夹
nautilus  home  #打开home 目录
nautilus  Downloads  #打开DownLoads 目录

获得文件写权限
chmod o+rwx hello.java  #获得文件写权限


mkdir ProgramFiles #在当前目录下，创建ProgramFiles文件夹
如果没有权限， 首先授权权限  
sudo su
# linux下，输入密码是没有显示的  


touch Sample.txt  #在当前目录下，创建Sample.txt 文件  


echo Hello >> Sample.txt  #往Sample.txt 文件上 追加并换行写入 Hello
echo Hello > Sample.txt  #往Sample.txt 文件上 清空并写入 Hello


cat Sample.txt #查看文件内容  

文件复制  
文件复制 重命名
cp hello.text  /usr/share/hello.java



```  
安装rpm  
```
rpm -i rmpFileName  #安装rpm文件
-i  #install 安装
-v  #小v，显示版本信息
-h  #显示安装进度
-l  #显示安装包中的所有文件被安装到哪些目录下；
-s  #显示安装版中的所有文件状态及被安装到哪些目录下；以下两个附加命令用于指定需要查询的是安装包还是已安装后的文件；
-p  #查询的是安装包的信息；
-f  #查询的是已安装的某文件信息；
-e  #需要卸载的安装包
-U  #需要升级的包
-V  #大V需要验证的包

```

### Terminal相关  
```
Tab  #命令自动补全
Ctrl + C  #停止当前指令，下一行重新开始
Ctrl + D  #退出终端
clear  #对应快捷键  Ctrl + L
Shift+Ctrl+T  #新建标签页
Shift+Ctrl+W  #关闭标签页
Shift+Ctrl+W  #关闭当前 terminal窗口


Ctrl+PageUp:前一标签页
Ctrl+PageDown:后一标签页
Shift+Ctrl+PageUp:标签页左移
Shift+Ctrl+PageDown:标签页右移
Alt+1:切换到标签页1
Alt+2:切换到标签页2
Alt+3:切换到标签页3
Shift+Ctrl+N:新建窗口
Shift+Ctrl+Q:关闭终端
终端中的复制／粘贴:
Shift+Ctrl+C:复制
Shift+Ctrl+V:粘贴
终端改变大小：
F11：全屏
Ctrl+plus:放大
Ctrl+minus:减小
Ctrl+0:原始大小

```

### make  
```
make是一个非常重要的编译命令，
make all：编译程序、库、文档等（等同于make）
make clean：删除由make命令产生的文件
make check：测试刚刚编译的软件（某些程序可能不支持）
make dist：重新打包成packname-version.tar.gz
make distclean：删除由./configure产生的文件
make install：安装已经编译好的程序。复制文件树中到文件到指定的位置
make installcheck：检查安装的库和程序（某些程序可能不支持）
make unistall：卸载已经安装的程序。

参数
-f
指定作为makefile的文件的名称。 如果不用该选项，那么make程序首先在当前目录查找名为makefile的文件，如果没有找到，它就会转而查找名为Makefile的文件。
如果您在Linux下使用GNU Make的话，它会首先查找GNUmakefile，之后再搜索makefile和Makefile。按照惯例，许多Linux程序员使用Makefile，
因为这样能使Makefile出现在目录中所有以小写字母命名的文件的前面。所以，最好不要使用GNUmakefile这一名称，因为它只适用于make程序的GNU版本。
–k
如果使用该选项，即使make程序遇到错误也会继续向下运行；如果没有该选项，在遇到第一个错误时make程序马上就会停止，那么后面的错误情况就不得而知了。
我们可以利用这个选项来查出所有有编译问题的源文件。
–n
该选项使make程序进入非执行模式，也就是说将原来应该执行的命令输出，而不是执行。

```

### 参考  
http://www.cnblogs.com/nosadness/p/5136652.html  
深入理解LINUX内核(第3版)  博韦 (作者)    
深入Linux内核架构  莫尔勒 (Wolfgang Mauerer) (作者),‎ 郭旭 (译者)    
Linux系统编程(第2版)  拉姆 (Robert Love) (作者),‎ 祝洪凯 (译者),‎ 李妹芳 (译者),‎ 付途 (译者)    

### 附录    

```
man: Manual 意思是手册，可以用这个命令查询其他命令的用法。
rmdir：Remove directory 移动目录
mkfs: Make file system 建立文件系统
fsck：File system check 文件系统检查
uname: Unix name 系统名称
df: Disk free 空余硬盘
du: Disk usage 硬盘使用率
lsmod: List modules 列表模块
ln: Link files  链接文件
fg: Foreground 前景
bg: Background 背景
chown: Change owner 改变所有者
chgrp: Change group 改变用户组
chmod: Change mode 改变模式
umount: Unmount 卸载
dd: 本来应根据其功能描述“Convert an copy”命名为“cc”，但“cc”已经被用以代表“C Complier”，所以命名为“dd”
tar：Tape archive 解压文件
ldd：List dynamic dependencies 列出动态相依
insmod：Install module 安装模块
rmmod：Remove module 删除模块
lsmod：List module 列表模块

```
