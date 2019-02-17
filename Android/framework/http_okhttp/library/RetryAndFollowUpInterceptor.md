###  RetryAndFollowUpInterceptor  
实例化一个 streamAllocation   

### intercept  
进入while循环  
1.. 首先查看请求是否已经取消  
2.. 调用 RealInterceptorChain 的 proceed 处理这个请求并把刚创建的 StreamAllocation 传递进去  
3.. 如果前面第二步没有出现异常, 则说明请求完成, 设置 releaseConnection 为 false, 出现异常则将 releaseConnection 置为true,并释放前面创建的 StreamAllocation  
4.. priorResponse 不为空, 则说明前面已经获取到了响应, 这里会结合当前获取的 Response 和先前的 Response  
5.. 调用 followUpRequest 查看响应是否需要重定向, 如果不需要重定向则返回当前请求  
6.. 重定向次数+1, 并且判断 StreamAllocation 是否需要重新创建  
7.. 重新设置 request, 并把当前的 Response 保存到 priorResponse, 继续while循环  
8.. 请求已破坏掉, 抛出异常停止循环;  
9.. 如果响应线路, 和请求相同, 复用, 否则, 关闭重建响应;   


### boolean recover   
如果客户端不允许失败重试,  OkHttpClient#retryOnConnectionFailure = false, 直接返回 false  
如果当前 exception 是致命的 如: ProtocolException CertificateException  SSLHandshakeException,  直接返回 false  
如果 streamAllocation 没有更多路由线路, 直接返回 false  
如果 重复发送请求体, 直接返回 false  
 

### Request followUpRequest  
如果不需要重定向, 直接返回 null;  
分析 HTTP status code;  
如果客户端不允许重定向, OkHttpClient#followRedirects  直接返回 null;  
获取head中客户端重定向url; 
最大重定向次数为20次, 超限会抛异常;  


### 参考  
https://blog.csdn.net/new_abc/article/details/53008506  
