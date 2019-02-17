假设shell 命令  
```
cd /Users/alex/WorkSpace/IntelliJ
pwd
```
正常输出，结果是终端最后一次的位置， 而不是 目标 [/Users/alex/WorkSpace/IntelliJ]  
解决办法：  
```
. /Users/alex/WorkSpace/IntelliJ/sample01.sh 
或者  
source /Users/alex/WorkSpace/IntelliJ/sample01.sh  
```

