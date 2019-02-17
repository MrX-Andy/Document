### 命令

### ps  

ps -f|grep java
```
UID       PID       PPID      C     STIME    TTY       TIME         CMD  
501  9208   392   0  5:00下午 ttys000    0:00.00 grep java
```  
UID      ：程序被该 UID 所拥有  
PID      ：就是这个程序的 ID   
PPID    ：则是其上级父程序的ID  
C          ：CPU使用的资源百分比  
STIME ：系统启动时间  
TTY     ：登入者的终端机位置  
TIME   ：使用掉的CPU时间。  
CMD   ：所下达的是什么指令  

获取 pid  
pgrep java  
```
1656  # uid  
10571  # pid  
```
杀死进程 kill -9 pid  
kill -9 10571  

### [zip](librray/zip.md)   
[find](librray/find.md)  
### rm  
```
rm -rf /alex/work/sample  

```

### 时间类  
今天是 一年中的第几天  
```
cal -j  

```

### 网络类  
获取 域名对应的ip  
```
ping www.baidu.com

```  

### 打开文件目录 open   
```
cd /Users/alex/WorkSpace/Linux/leetcode/leet/src/main/java/com/alex/leet  
open .
```

### 找出 最大的文件  
https://blog.csdn.net/tzh_linux/article/details/51192472    
```
du -ah . | sort -nrk 1 | head 
```

