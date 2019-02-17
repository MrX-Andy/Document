◆ cloning  
```
fatal: The remote end hung up unexpectedly 
fatal: early EOF 
fatal: index-pack failed
```
[clone_error](library/clone_error_eof.md)  
[update latest](library/update_latest.md)  
[环境变量](library/run_path.md)   
git log  
如何撤销已经push上去的commit    
git提供了好几种方法做到这点，其中最简单的方法就是用git revert命令，后面跟上你要撤销的commit id即可。  

git commit  
```
//  提交当前本地变动, 添加变动描述;  
git commit -am 更新  
```

git push  
```
//  把本地的, 当前分支, 推送到远端的, 当前分支上  
git push origin  
```

git fetch  
```
//  抓取远端的 当前分支
git fetch origin  

//  抓取远端的 feature/v5.0 分支
git fetch origin feature/v5.0  
```  
fetch 和 pull 区别  
```
git pull = git fetch + git merge  
git fetch 的时候只是将remote的origin进行update  但是并没有在local的branch进行merge  
```
git merge  
```
//  远端的 feature/v5.0 分支, merge 到本地, 当前分支;  
git merge origin/feature/v5.0 
```
git checkout  
```
//  检出对应的分支名称  
git checkout -b feature/v5.0  
```
### 分支管理  
查看本地分支  
git branch  
前面带*，高亮的是当前分支  
```
test  
dev  
release  
* master  
```
查看远端分支  
```
git branch -a  
```

### 参考  
git 下载  
https://gitforwindows.org/
  
http://blog.csdn.net/wangyanchang21/article/details/51437934  
http://iissnan.com/progit/  
https://git-scm.com/book/zh/v1  
https://git-scm.com/book/zh/v2  
http://www.runoob.com/git/git-basic-operations.html  
https://www.yiibai.com/git/git_clone_operation.html  
https://github.com/geeeeeeeeek/git-recipes/wiki  
https://www.ibm.com/developerworks/cn/java/j-lo-git-mange/index.html  

