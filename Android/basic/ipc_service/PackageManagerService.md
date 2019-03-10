PackageManagerService(PMS) 是用来获取 apk 信息的, AMS 总是会使用 PMS 加载安装包的信息, 将其封装在 LoadedApk 这个类对象中;  
在下载并安装 apk 的过程中, 会把 apk 存放在 data/app 目录下;  
apk 是一个压缩包, 文件头记录着压缩包的大小;  
每次从 apk 中国读取资源, 并不是先解压在查找资源文件, 而是解析 apk 中的 resource.arsc 文件,  
这个文件中存储着资源的所有信息, 包括在 apk 中的地址-路径, 大小等信息;  
不解压, 还可以节省空间;  

android 系统使用 PMS 解析 apk 文件中的 Manifest 文件, 包括:  
四大组件的信息;  
分配 用户id, 用户组id, 用户id 是唯一的, 因为 android 是一个 linux 系统;  
用户组id, 指的是各种权限, 每一个权限都在一个用户组中, 比如读写 SD 卡或者访问网络, 分配给了哪个用户组 id, 就拥有了哪些权限;  
在 launcher 中生成一个 icon, icon 保存着默认启动的 activity 的信息;  
在 app 安装过程的最后, 会把上面的信息记录在一个 xml 文件中, 以便于下次安装时, 再次使用;  
android 每次启动时, 都会把所有的 apk 文件解析一遍;  


PackageManagerService 在启动后会扫描系统和第三方的app信息, 在 scanPackageLI 方法中实例化 PackageParser 对象 pp, 使用 pp 对包进行解析;  
PackageParser 的 parseBaseApk 在调用之后解析 AndroidManifest.xml, 返回一个 Package 对象, 将手机中所有的 app 的 AndroidManifest.xml 解析完毕, 构建出一个手机中所有 app 的信息树;  
从这颗棵树上