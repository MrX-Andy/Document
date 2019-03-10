### URI 与 URL  
URL（Uniform Resource Locator, 统一资源定位符）; URL 正是使用 Web 浏览器等访问Web 页面时需要输入的网页地址;  
比如,  http://hackr.jp 就是 URL;  

URI 是 Uniform Resource Identifier 的缩写  
包括: http://  https://  ftp:// file://    telnet://  urn://  rtsp://  rtspu://  
让我们先来了解一下绝对 URI 的格式;  
[URI 的格式](/ComputerScience/network/ImageFiles/uri_001.png)   
<scheme>://<user>:<password>@<host>:<port>/<path>;<params>?<query>#<frag>  
方案  访问服务器以获取资源时要使用哪种协议   
用户  某些方案访问资源时需要的用户名  
密码  用户名后面可能要包含的密码,  中间由冒号（ :） 分隔  
主机  资源宿主服务器的主机名或点分 IP 地址  
端口  
```
资源宿主服务器正在监听的端口号; 很多方案都有默认端口号（ HTTP 的默认端口号为 80）
```
路径  
```
服务器上资源的本地名,  由一个斜杠（ /） 将其与前面的URL 组件分隔开来;   
路径组件的语法是与服务器和方案有关的（ 本章稍后会讲到 URL 路径可以分为若干个段,  每段都可以有其特有的组件;）  
```
参数  
```
某些方案会用这个组件来指定输入参数; 参数为名 / 值对;  
URL 中可以包含多个参数字段,  它们相互之间以及与路径的其余部分之间用分号（ ;） 分隔  
```
查询  
```
某些方案会用这个组件传递参数以激活应用程序（ 比如数据库、 公告板、 搜索引擎以及其他因特网网关）;   
查询组件的内容没有通用格式; 用字符“ ?” 将其与 URL 的其余部分分隔开来  
```
片段  
```
一小片或一部分资源的名字; 引用对象时,  不会将 frag 字段传送给服务器; 这个字段是在客户端内部使用的;   
通过字符“ #” 将其与 URL 的其余部分分隔开来  
```
Uniform Resource Identifier，即统一资源标识符  
自定义URI = content://com.alex.andfun.provider.DownloadContentProvider/UserEntity/1  
自定义URI = 主题名（Schema）://授权信息（Authority）/表名（Path）/记录（ID）  
主题（Schema）：Android所规定的，Content Provider的URI前缀；  
授权信息（Authority）：Content Provider的唯一标识符；  
表名（Path）：Content Provider指向的数据库中的某个table；  
记录（ID）：表中的某条记录，如果没有指定，表示所有记录；  

Uri结构  
基本形式：scheme:scheme-specific-part#fragment  
进一步划分：scheme://authority/path?query#fragment  
终极划分：scheme://host:port/path?query#fragment   
表名（Path）  
path可以有多个，每个用/连接，比如  
scheme://authority/path1/path2/path3?query#fragment  
query参数  
query参数可以带有对应的值，也可以不带，如果带对应的值用=表示，如:    
scheme://authority/path1/path2/path3?id = 1#fragment，这里有一个参数id，它的值是1  
query参数可以有多个，每个用&连接  
scheme://authority/path1/path2/path3?id = 1&name = mingming&old#fragment  
这里有三个参数：  
参数1：id，其值是:1  
参数2：name，其值是:mingming  
参数3：old，没有对它赋值，所以它的值是null  
特别的  
在android中，除了scheme、authority是必须要有的，其它的几个path、query、fragment，它们每一个可以选择性的要或不要，但顺序不能变，比如：  
其中"path"可不要：scheme://authority?query#fragment  
其中"path"和"query"可都不要：scheme://authority#fragment  
其中"query"和"fragment"可都不要：scheme://authority/path  
"path","query","fragment"都不要：scheme://authority  
等等……  
通配符：  
* 匹配任意长度的任何有效字符的字符串   
content://com.example.app.provider/*   表示 匹配provider的任何内容  

＃ 匹配任意长度的数字字符的字符串  
content://com.example.app.provider/table/#   表示 匹配provider中的table表的所有行    

1...通话记录  
1.1...Uri uri = Uri.parse("content://call_log/calls");  
1.2...Cursor cursor = resolver.query(uri, new String[]{"_id","number","date"}, null, null, null);  

2...手机短信  
2.1...Uri uri = Uri.parse("content://sms");  //查询所有字段   
2.2...Cursor cursor = resolver.query(uri, new String[]{"_id","address","date","body"}, null, null, null);  

3...通讯记录  
3.1.1...Uri uri = Uri.parse("content://com.android.contacts/contacts"); //读取所有字段  
3.1.2...Cursor cursor = resolver.query(uri, new String[]{"_id","display_name"}, null, null, null); //  
3.2.1...Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");    
3.2.1.1...Uri uri = RawContacts.CONTENT_URI;			//利用系统常量，获得Uri  
3.2.2...Cursor cursor = resolver.query(uri, new String[]{"_id","display_name"}, null, null, null);  
3.3.1...Uri uriForData = Uri.parse("content://com.android.contacts/data");  

### MIME  

MIME类型组成  
每种MIME类型 由2部分组成 = 类型 + 子类型  
text/css  
text/xml  
application/pdf  

MIME类型形式  
// 形式1：单条记录    
vnd.android.cursor.item/自定义  
// 形式2：多条记录（集合）
vnd.android.cursor.dir/自定义   
// 1. vnd：表示父类型和子类型具有非标准的、特定的形式。  
// 2. 父类型已固定好（即不能更改），只能区别是单条还是多条记录    
// 3. 子类型可自定义  


MIME实例说明  
<-- 单条记录 -->  
// 单个记录的MIME类型  
vnd.android.cursor.item/vnd.yourcompanyname.contenttype   

// 若一个Uri如下  
content://com.example.transportationprovider/trains/122     
// 则ContentProvider会通过ContentProvider.geType(url)返回以下MIME类型  
vnd.android.cursor.item/vnd.example.rail  

<-- 多条记录 -->  
// 多个记录的MIME类型  
vnd.android.cursor.dir/vnd.yourcompanyname.contenttype   
// 若一个Uri如下  
content://com.example.transportationprovider/trains   
// 则ContentProvider会通过ContentProvider.geType(url)返回以下MIME类型  
vnd.android.cursor.dir/vnd.example.rail  
"vnd.android.cursor.item/email_v2"							邮箱  
"vnd.android.cursor.item/phone_v2"						手机号  
"vnd.android.cursor.item/name"									姓名  
"vnd.android.cursor.item/postal-address_v2"		通信地址  

  