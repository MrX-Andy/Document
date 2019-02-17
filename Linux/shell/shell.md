### shell  
[hello world](sample/sample01.md)   
[cd 命令无效的问题](sample/sample02.md)   
[杀死java进程](sample/sample04.md)  
#### 变量赋值，运行结果赋值  
```
result=$(pgrep java)
echo 杀死 java进程，pid = $result
kill -9 $result
```
#### 条件判断  
```
#!/bin/bash
  result=$(pgrep java)
  if [ -z "$result" ]; then
      echo "result 为空"
  fi
  if [ -n "$result" ]; then
      echo "result 为非空"
  fi
```
### 参考  
https://www.cnblogs.com/yinheyi/p/6648242.html  

