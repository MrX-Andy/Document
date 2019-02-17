### 压缩与解压缩  

#### tar   
tar -cvf leet.tar leetcode  

tar -cvf leet.zip /Users/alex/leetcode  
这样创建的， 解压之后，会有 目录 [Users/alex/leetcode]  

cd /Users/alex 
tar -jcvf leet.tar.bz2 leetcode
tar -jcvf AndFun.tar.bz2 AndFun
```
-c, --create  create a new archive 创建一个归档文件
- C: 要在指定目录下进行解压缩，
-v, --verbose verbosely list files processed 显示创建归档文件的进程
-f, --file=ARCHIVE use archive file or device ARCHIVE  后面要立刻接被处理的档案名； 如果有多个选项 , f 必须在最后； -zcf   demo.tar.gz  
-x：解压  
-t：查看内容  
-u：更新原压缩包中的文件
-r：向压缩归档文件末尾追加文件  
-j: 通过bzip2进行压缩与解压缩， 此时文档名最好（必须）为 *.tar.bz2  
-J: 通过xz进行压缩与解压缩， 此时文档名最好（必须）为 *.tar.xz
-z: 通过gzp进行压缩与解压缩，此时文档名最好（必须）为 *.tar.gz
```

### zip 语法  
```
//  tmp 文件目录下，压缩 所有的 .txt 文件
zip test.zip *.txt tmp  

cd /Users/alex 
zip leet.zip *  leetcode  
```
### gzip 语法  
```
gzip [-cdtv#] 文件名  
zcat *.gz  
参数：  
-c:将压缩的数据输出到屏幕上，可通过数据流重定向来处理；  
-d:解压的参数；  
-t:可以用来验证一个压缩文件有没有错误；  
-v:可以显示出"原文件/压缩文件"的压缩比等信息；  
-#：压缩等级，1最快，但是压缩比最差，-9最慢，但是压缩比最高，默认为6；  
```
###  常见压缩文件格式  
```
*.gz   gzip程序压缩的文件  
*.bz2  bzip程序压缩的文件  
*.tar  tar程序打包的数据，没有经过压缩  
*.tar.gz  tar程序打包后，并经过gzip程序压缩  
*.tar.bz2 tar程序打包后，并经过bzip程序压缩  
*.zip     zip程序压缩的文件  
*.rar     rar.程序压缩的文
```
### 参考  
https://blog.csdn.net/zmzwll1314/article/details/81746487  
https://www.cnblogs.com/jxldjsn/p/8029431.html  
http://eksliang.iteye.com/blog/2109693  
