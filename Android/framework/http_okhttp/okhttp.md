### okHttp  
Http 协议基于 tcp-ip 协议的, 在 Java 中, tcp-ip 是使用 socket 实现的;  
socket 自动选择最好路线, 并支持自动重连;  
拥有自动维护的 socket 连接池, 减少握手次数;  
透明的 自动的 支持 GZip, 节省流量;  
支持 SPDY , 共享同一个 Socket 来处理同一个服务器的所有请求;  
通过连接池来减少请求延时;  
缓存响应数据来减少重复的网络请求;  

流: HttpCode  
连接: Connection  
连接池: ConnectionPool  
### 内置拦截器  
RetryAndFollowUpInterceptor  
重试与重定向拦截器;  
创建 StreamAllocation 对象, 负责失败重试以及重定向;  

BridgeInterceptor  
配置 Request 的 Headers 头信息: 读取Cookie;  
负责把用户构造的请求, 转换为发送给服务器的请求, 补全缺失的一些 http header, 把服务器返回的响应转换为对用户友好的响应。  


CacheInterceptor  
GET 类型的缓存;  
负责读取缓存以及更新缓存, 处理 http 缓存。  

ConnectInterceptor    
负责与服务器建立连接, 借助于前面分配的 StreamAllocation 对象建立与服务器之间的连接, 并选定协议是 HTTP 1.1 还是 HTTP 2  
  
CallServerInterceptor  
将 requestBody 写入输入流, 通过 socket 与服务器交互;  
将 socket 中的输出流构建成 responseBody, 交给客户端;  

### 关于 gzip  
开发者没有添加 Accept-Encoding 时, 自动添加 Accept-Encoding: gzip  
自动添加 Accept-Encoding, 会对 request, response 进行自动解压  
手动添加 Accept-Encoding, 不负责解压缩  
自动解压时移除 Content-Length, 所以上层 Java 代码想要 contentLength 时为-1  
自动解压时移除 Content-Encoding  
自动解压时, 如果是分块传输编码, Transfer-Encoding: chunked 不受影响。  

### 源码分析  
ConnectPool 是在 OkHttpClient 中被初始化;  
RouteDatabase  在 ConnectPool 中初始化;  

StreamAllocation 是在 RetryAndFollowUpInterceptor#intercept 中被创建的 ;    
RouteSelector 是在 RetryAndFollowUpInterceptor#intercept 中被创建的 ;    

HttpCodec 是在 ConnectInterceptor#intercept 中被创建的 ;  
RealConnection 是在 ConnectInterceptor#intercept 中被创建的 ;  
Response 是在 CallServerInterceptor#intercept 中被创建的 ;    
读写操作 在RealConnection#connectSocket 中进行;  


[OKHttp3排队任务 与 线程池模型](library/source_thread_pool.md)  
[RetryAndFollowUpInterceptor](library/RetryAndFollowUpInterceptor.md)  
[CacheInterceptor](library/CacheInterceptor.md)  
[ConnectInterceptor](library/ConnectInterceptor.md)  
[CallServerInterceptor](library/CallServerInterceptor.md)  
[HttpCodec](library/HttpCodec.md)  
[Response](library/Response.md)  
[StreamAllocation](library/StreamAllocation.md)  
[RealConnection](library/RealConnection.md)  
[ConnectionPool](library/ConnectionPool.md)  
[cancel方法](library/source_cancel.md)  
[连接池复用](library/source_connection_pool.md)  
[超时时间](library/source_timeout.md)  
RouteDatabase  
内部使用 LinkedHashSet ,记录 Route 黑名单;  


### 使用  
http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0106/2275.html  
https://blog.csdn.net/u014614038/article/details/51210685  
https://blog.csdn.net/briblue/article/details/52920531  
https://blog.csdn.net/shengfakun1234/article/details/54615592  
https://blog.csdn.net/u014614038/article/details/51210685  
https://github.com/franmontiel/PersistentCookieJar  

### 源码  
https://blog.csdn.net/chunqiuwei/column/info/16213  
https://juejin.im/post/5a704ed05188255a8817f4c9  
https://juejin.im/post/5bd94a1ae51d45682a028a8f  
https://www.jianshu.com/nb/21173714  
https://www.jianshu.com/p/adb364660e1a  

重试, 重定向    
https://www.jianshu.com/p/03a75976a9a2  
https://blog.csdn.net/dehang0/article/details/80556625  

bridge
https://blog.csdn.net/chunqiuwei/article/details/72980397  

连接池         
已看  
https://blog.csdn.net/dehang0/article/details/80724043
https://yq.aliyun.com/articles/78101  

未看  
https://blog.csdn.net/chunqiuwei/article/details/74203667  
https://www.jianshu.com/p/e6fccf55ca01  
https://yq.aliyun.com/articles/638928  
https://www.wolfcstech.com/archives/page/3/  
https://www.jianshu.com/p/ee220d5a5cc3  
https://www.jianshu.com/p/e6fccf55ca01  

访问连接  
https://www.jianshu.com/p/671a123ec163  

缓存配置  策略    
https://blog.csdn.net/dehang0/article/details/80588908  
https://blog.csdn.net/chunqiuwei/article/details/73224494  
https://www.jianshu.com/p/ac8e9285f9dc  
https://yq.aliyun.com/articles/78102  
https://cloud.tencent.com/developer/article/1197112  
  

任务队列    
https://yq.aliyun.com/articles/78103  
https://yq.aliyun.com/articles/78104  
https://yq.aliyun.com/articles/78105  
https://blog.csdn.net/itachi85/article/details/52335403  
https://www.jianshu.com/p/9bfdcc38d934  
https://blog.piasy.com/2016/07/11/Understand-OkHttp/  
https://www.jianshu.com/p/37e26f4ea57b  
https://www.jianshu.com/p/db699081bc38  
https://blog.csdn.net/qq_33768280/article/details/80722124  
https://itimetraveler.github.io/2018/01/25/%E3%80%90Android%E3%80%91OkHttp%E6%BA%90%E7%A0%81%E5%88%86%E6%9E%90/  
https://www.zybuluo.com/natsumi/note/210074  
https://juejin.im/post/5af93575518825426539917b  
https://yuqirong.me/2017/07/25/OkHttp%E6%BA%90%E7%A0%81%E8%A7%A3%E6%9E%90/  
http://frodoking.github.io/2015/06/29/android-okhttp-connectionpool-http1-x-http2-x/  
https://www.jianshu.com/p/6166d28983a2  
https://fucknmb.com/2018/04/16/%E5%BD%93OkHttp%E9%81%87%E4%B8%8AHttp-2-0/  

证书管理       https://www.jianshu.com/p/2212f9be835c  
代理与路由  https://www.jianshu.com/p/5c98999bc34f  
https://blog.csdn.net/chunqiuwei/article/details/74079916  
https://www.jianshu.com/p/5c98999bc34f  
https://blog.csdn.net/chunqiuwei/article/details/74079916  
https://www.wolfcstech.com/2016/10/14/OkHttp3%E4%B8%AD%E7%9A%84%E4%BB%A3%E7%90%86%E4%B8%8E%E8%B7%AF%E7%94%B1/  



https://www.wolfcstech.com/2016/10/27/OkHttp3%E8%BF%9E%E6%8E%A5%E5%BB%BA%E7%AB%8B%E8%BF%87%E7%A8%8B%E5%88%86%E6%9E%90/  
OkHttp3中的HTTP2首部压缩  
https://www.wolfcstech.com/2016/12/15/OkHttp3%E4%B8%AD%E7%9A%84HTTP2%E9%A6%96%E9%83%A8%E5%8E%8B%E7%BC%A9/   

okio  
https://blog.csdn.net/p892848153/article/details/51214054  
http://www.baitouwei.com/2015/01/08/okio/  
https://blog.csdn.net/zhyooo123/article/details/50536781  
https://blog.csdn.net/zoudifei/article/details/51232711  
https://juejin.im/entry/580ec75ba0bb9f0061cebba6  
https://www.jianshu.com/p/f033a64539a1  
https://www.jianshu.com/p/a42170233a32  
https://blog.csdn.net/evan_man/article/details/51204469  
https://www.jianshu.com/p/ccf24a63dca8  
https://blog.csdn.net/qq_31694651/article/details/52748134  
