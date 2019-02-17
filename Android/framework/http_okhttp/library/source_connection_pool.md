### 连接池复用  
在timeout空闲时间内, 连接不会关闭, 相同重复的 request 将复用原先的 connection, 减少握手的次数, 大幅提高效率;   

在 ConnectInterceptor 中 创建一个 Connection , 在 CallServerInterceptor 中, 需要使用这个 Connection;  
先看创建过程:  
1.. ConnectInterceptor#intercept  
 ```
@Override public Response intercept(Chain chain) throws IOException {
 RealInterceptorChain realChain = (RealInterceptorChain) chain;
  //  入口 
 HttpCodec httpCodec = streamAllocation.newStream(client, chain, doExtensiveHealthChecks);
 StreamAllocation streamAllocation = realChain.streamAllocation();
 RealConnection connection = streamAllocation.connection();
 return realChain.proceed(request, streamAllocation, httpCodec, connection);
}
 ```
2.. StreamAllocation#newStream  
 ```
public HttpCodec newStream(
   OkHttpClient client, Interceptor.Chain chain, boolean doExtensiveHealthChecks) {

 try {
    //  入口  
   RealConnection resultConnection = findHealthyConnection(connectTimeout, readTimeout,
       writeTimeout, pingIntervalMillis, connectionRetryEnabled, doExtensiveHealthChecks);
   HttpCodec resultCodec = resultConnection.newCodec(client, chain, this);

   synchronized (connectionPool) {
     codec = resultCodec;
     return resultCodec;
   }
 } catch (IOException e) {
   throw new RouteException(e);
 }
}
 ```  
3... StreamAllocation#findHealthyConnection  
 ```
private RealConnection findHealthyConnection(int connectTimeout, int readTimeout,
      int writeTimeout, int pingIntervalMillis, boolean connectionRetryEnabled,
      boolean doExtensiveHealthChecks) throws IOException {
    while (true) {
    
      //  获取一个连接
      RealConnection candidate = findConnection(connectTimeout, readTimeout, writeTimeout,
          pingIntervalMillis, connectionRetryEnabled);

      // If this is a brand new connection, we can skip the extensive health checks.
      synchronized (connectionPool) {
        //  判断是新建的连接还是复用的连接, 新建的successCount值为0
        if (candidate.successCount == 0) {
          return candidate;
        }
      }

      // Do a (potentially slow) check to confirm that the pooled connection is still good. If it
      // isn't, take it out of the pool and start again.
      // 检查连接中的socket是否可用, 不可用则标记connection.noNewStreams为true、从连接池中移除connection、关闭connection中的socket
      if (!candidate.isHealthy(doExtensiveHealthChecks)) {
        noNewStreams();
        continue;
      }

      return candidate;
    }
}
 ```
###  findConnection   
4.. StreamAllocation#findConnection  
1).. 先找是否有已经存在的连接, 如果有已经存在的连接, 并且可以使用(!noNewStreams)则直接使用;  
2).. 根据已知的address在connectionPool里面找, 如果有连接, 则返回
3).. 更换路由, 更换线路, 在connectionPool里面再次查找, 如果有则返回。
4).. 如果以上条件都不满足则直接new一个RealConnection出来
5).. new出来的RealConnection通过acquire关联到connection.allocations上
6).. 做去重判断, 如果有重复的socket则关闭

//  查看当前streamAllocation是否有之前已经分配过的连接, 有则直接使用  
//  从连接池中查找可复用的连接, 有则返回该连接  
//  配置路由, 配置后再次从连接池中查找是否有可复用连接, 有则直接返回  
//  新建一个连接, 并修改其StreamAllocation标记计数, 将其放入连接池中  
//  查看连接池是否有重复的多路复用连接, 有则清除  
 ```
private RealConnection findConnection(int connectTimeout, int readTimeout, int writeTimeout, 
    int pingIntervalMillis, boolean connectionRetryEnabled) throws IOException {
 boolean foundPooledConnection = false;
 RealConnection result = null;
 Route selectedRoute = null;
 Connection releasedConnection;
 Socket toClose;
 synchronized (connectionPool) {
   if (released) throw new IllegalStateException("released");
   if (codec != null) throw new IllegalStateException("codec != null");
   if (canceled) throw new IOException("Canceled");

   // Attempt to use an already-allocated connection. We need to be careful here because our
   // already-allocated connection may have been restricted from creating new streams.
   releasedConnection = this.connection;
   //  判断connection是否可用, 不可用则返回connection中的socket, 并释放connection
   toClose = releaseIfNoNewStreams();
   if (this.connection != null) {
     // We had an already-allocated connection and it's good.
     result = this.connection;
     releasedConnection = null;
   }
   if (!reportedAcquired) {
     // If the connection was never reported acquired, don't report it as released!
     releasedConnection = null;
   }

   if (result == null) {
     // Attempt to get a connection from the pool.
     //  试图从连接池中找到可复用的连接
     // 当前StreamAllocation中没有保存可用的connection, 从连接池中查找。若连接池中存在可用connection, 则会赋值给当前StreamAllocation的connection成员变量
     Internal.instance.get(connectionPool, address, this, null);
     if (connection != null) {
       foundPooledConnection = true;
       result = connection;
     } else {
       selectedRoute = route;
     }
   }
 }
 //  关闭前面调用releaseIfNoNewStreams方法返回的socket
 closeQuietly(toClose);

 //  如果从连接池中获取的connection可用, 直接返回连接
 if (releasedConnection != null) {
   eventListener.connectionReleased(call, releasedConnection);
 }
 if (foundPooledConnection) {
   eventListener.connectionAcquired(call, result);
 }
 if (result != null) {
   // If we found an already-allocated or pooled connection, we're done.
   return result;
 }

 // If we need a route selection, make one. This is a blocking operation.
 //  获取路由配置, 所谓路由其实就是代理, ip地址等参数的一个组合
 boolean newRouteSelection = false;
 if (selectedRoute == null && (routeSelection == null || !routeSelection.hasNext())) {
   newRouteSelection = true;
   routeSelection = routeSelector.next();
 }

 synchronized (connectionPool) {
   if (canceled) throw new IOException("Canceled");

   if (newRouteSelection) {
     // Now that we have a set of IP addresses, make another attempt at getting a connection from
     // the pool. This could match due to connection coalescing.
     List<Route> routes = routeSelection.getAll();
     for (int i = 0, size = routes.size(); i < size; i++) {
       Route route = routes.get(i);
       //  遍历路由, 查找匹配的connection
       //  拿到路由后可以尝试重新从连接池中获取连接, 这里主要针对http2协议下清除域名碎片机制
       Internal.instance.get(connectionPool, address, this, route);
       if (connection != null) {
         foundPooledConnection = true;
         result = connection;
         this.route = route;
         break;
       }
     }
   }

   if (!foundPooledConnection) {
     if (selectedRoute == null) {
       selectedRoute = routeSelection.next();
     }

     // Create a connection and assign it to this allocation immediately. This makes it possible
     // for an asynchronous cancel() to interrupt the handshake we're about to do.
     route = selectedRoute;
     refusedStreamCount = 0;
     //  如果一直都没有 匹配到, 就 new 出来一个  
     result = new RealConnection(connectionPool, selectedRoute);
     //  加入连接池队列  
     //  修改result连接stream计数, 方便connection标记清理
     acquire(result, false);
   }
 }

 // If we found a pooled connection on the 2nd time around, we're done.
 if (foundPooledConnection) {
   eventListener.connectionAcquired(call, result);
   return result;
 }

 // Do TCP + TLS handshakes. This is a blocking operation.
 //  新创建的connection开始握手连接
 result.connect(connectTimeout, readTimeout, writeTimeout, pingIntervalMillis,
     connectionRetryEnabled, call, eventListener);
 //  新建的连接一定可用, 所以将该连接移除黑名单  
 routeDatabase().connected(result.route());

 Socket socket = null;
 synchronized (connectionPool) {
   reportedAcquired = true;

   // Pool the connection.
   //  把连接放到连接池中
   Internal.instance.put(connectionPool, result);

   // If another multiplexed connection to the same address was created concurrently, then
   // release this connection and acquire that one.
   //  如果这个连接是多路复用
   //  如果同时存在多个连向同一个地址的多路复用连接, 则关闭多余连接, 只保留一个
   //  判断该connection是否支持多路复用, 即是否是HTTP/2协议
   if (result.isMultiplexed()) {
     //  调用connectionPool的deduplicate方法去重
     //  若连接池中存在相同host的连接, 则释放重复的连接(保留连接池中的连接)
     socket = Internal.instance.deduplicate(connectionPool, address, this);
     result = connection;
   }
 }
 closeQuietly(socket);

 eventListener.connectionAcquired(call, result);
 return result;
}
 ```
5.. OkHttpClient  
 ```
 static {
     Internal.instance = new Internal() {
     
       @Override public RealConnection get(ConnectionPool pool, Address address,
           StreamAllocation streamAllocation, Route route) {
         return pool.get(address, streamAllocation, route);
       }
       @Override public void put(ConnectionPool pool, RealConnection connection) {
         pool.put(connection);
       }
 
     };
   }
 ```
6... ConnectionPool#get  
 ```
   @Nullable RealConnection get(Address address, StreamAllocation streamAllocation, Route route) {
     assert (Thread.holdsLock(this));
     for (RealConnection connection : connections) {
       if (connection.isEligible(address, route)) {
         streamAllocation.acquire(connection, true);
         return connection;
       }
     }
     return null;
   }
 ```
7.. RealConnection#isEligible  
 [参见](RealConnection.md)

### acquire  
加入连接池队列  
8.. StreamAllocation#acquire  
```
public void acquire(RealConnection connection, boolean reportedAcquired) {
   assert (Thread.holdsLock(connectionPool));
   if (this.connection != null) throw new IllegalStateException();

   this.connection = connection;
   this.reportedAcquired = reportedAcquired;
   connection.allocations.add(new StreamAllocationReference(this, callStackTrace));
}
```
### release  
释放连接  StreamAllocation#release  
ConnectInterceptor#intercept  
StreamAllocation#newStream  
StreamAllocation#findHealthyConnection  
StreamAllocation#findConnection      
StreamAllocation#releaseIfNoNewStreams   
StreamAllocation#deallocate    
StreamAllocation#release    


Http1Codec.AbstractSource#endOfInput    
StreamAllocation#streamFinished        
StreamAllocation#deallocate  
StreamAllocation#release  



StreamAllocation#release()    
StreamAllocation#noNewStreams    
StreamAllocation#streamFailed  
StreamAllocation#releaseAndAcquire  

### idleAtNanos  
在请求结束的时候, 会更新时间戳,   
okhttp3.internal.http1.Http1Codec.FixedLengthSource#read  
okhttp3.internal.http1.Http1Codec.AbstractSource#endOfInput  
okhttp3.internal.connection.StreamAllocation#streamFinished  
okhttp3.internal.connection.StreamAllocation#deallocate  
```
connection.idleAtNanos = System.nanoTime();
```
在调用 ConnectionPool#cleanup 方法的时候, 会检查每一个连接的时间戳,   
如果空闲连接超过5个或者 keepAlive 时间大于5分钟, 则将该连接清理掉。  
ConnectionPool#cleanup 方法[详见](ConnectionPool.md)  



                 
 
 