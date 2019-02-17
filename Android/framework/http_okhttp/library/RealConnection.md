### RealConnection   
RealConnection 是在 ConnectInterceptor#intercept 中被创建的 ;  
Sink 是在 RealConnection#connectSocket 中被创建的 ;  
Socket 是在 RealConnection#connectSocket 中被创建的 ;  

### connectSocket  
简单的来说connectSocket方法其作用就是打开了一条Socket链接  
source = Okio.buffer(Okio.source(rawSocket));  //  socket 输入流  
sink = Okio.buffer(Okio.sink(rawSocket));  //  socket 输出流  

```
private void connectSocket(int connectTimeout, int readTimeout, Call call,
      EventListener eventListener) throws IOException {
    Proxy proxy = route.proxy();
    Address address = route.address();

    rawSocket = proxy.type() == Proxy.Type.DIRECT || proxy.type() == Proxy.Type.HTTP
        ? address.socketFactory().createSocket()
        : new Socket(proxy);

    eventListener.connectStart(call, route.socketAddress(), proxy);
    rawSocket.setSoTimeout(readTimeout);
    try {
      Platform.get().connectSocket(rawSocket, route.socketAddress(), connectTimeout);
    } catch (ConnectException e) {
      ConnectException ce = new ConnectException("Failed to connect to " + route.socketAddress());
      ce.initCause(e);
      throw ce;
    }

    // The following try/catch block is a pseudo hacky way to get around a crash on Android 7.0
    // More details:
    // https://github.com/square/okhttp/issues/3245
    // https://android-review.googlesource.com/#/c/271775/
    try {
      //  对输入和输出做处理   
      source = Okio.buffer(Okio.source(rawSocket));
      sink = Okio.buffer(Okio.sink(rawSocket));
    } catch (NullPointerException npe) {
      if (NPE_THROW_WITH_NULL.equals(npe.getMessage())) {
        throw new IOException(npe);
      }
    }
  }
```

requiresTunnel  
```
//  当前route的请求是https并且使用了Proxy.Type.HTTP代理
public boolean requiresTunnel() {
    return address.sslSocketFactory != null && proxy.type() == Proxy.Type.HTTP;
}
```

### connect  
1.. 如果当前 route 的请求是 https 并且使用了 Proxy.Type.HTTP 代理, 就开启一个隧道链接;   
2.. 如果1不成立, 则调用 connectSocket 方法创建Socket链接   
3.. 调用 establishProtocol 构建协议;  
```
public void connect(int connectTimeout, int readTimeout, int writeTimeout,
  int pingIntervalMillis, boolean connectionRetryEnabled, Call call,
  EventListener eventListener) {
    if (protocol != null) throw new IllegalStateException("already connected");
    
    RouteException routeException = null;
    List<ConnectionSpec> connectionSpecs = route.address().connectionSpecs();
    ConnectionSpecSelector connectionSpecSelector = new ConnectionSpecSelector(connectionSpecs);
    
    if (route.address().sslSocketFactory() == null) {
      if (!connectionSpecs.contains(ConnectionSpec.CLEARTEXT)) {
        throw new RouteException(new UnknownServiceException(
            "CLEARTEXT communication not enabled for client"));
      }
      String host = route.address().url().host();
      if (!Platform.get().isCleartextTrafficPermitted(host)) {
        throw new RouteException(new UnknownServiceException(
            "CLEARTEXT communication to " + host + " not permitted by network security policy"));
      }
    } else {
      if (route.address().protocols().contains(Protocol.H2_PRIOR_KNOWLEDGE)) {
        throw new RouteException(new UnknownServiceException(
            "H2_PRIOR_KNOWLEDGE cannot be used with HTTPS"));
      }
    }
    
    while (true) {
      try {
        //  如果是通道模式, 则建立通道连接  
        if (route.requiresTunnel()) {
          connectTunnel(connectTimeout, readTimeout, writeTimeout, call, eventListener);
          if (rawSocket == null) {
            // We were unable to connect the tunnel but properly closed down our resources.
            break;
          }
        } else {
          connectSocket(connectTimeout, readTimeout, call, eventListener);
        }
        //  建立 https连接
        establishProtocol(connectionSpecSelector, pingIntervalMillis, call, eventListener);
        eventListener.connectEnd(call, route.socketAddress(), route.proxy(), protocol);
        break;
      } catch (IOException e) {
        closeQuietly(socket);
        closeQuietly(rawSocket);
        socket = null;
        rawSocket = null;
        source = null;
        sink = null;
        handshake = null;
        protocol = null;
        http2Connection = null;
    
        eventListener.connectFailed(call, route.socketAddress(), route.proxy(), null, e);
    
        if (routeException == null) {
          routeException = new RouteException(e);
        } else {
          routeException.addConnectException(e);
        }
    
        if (!connectionRetryEnabled || !connectionSpecSelector.connectionFailed(e)) {
          throw routeException;
        }
      }
    }
    
    if (route.requiresTunnel() && rawSocket == null) {
      ProtocolException exception = new ProtocolException("Too many tunnel connections attempted: "
          + MAX_TUNNEL_ATTEMPTS);
      throw new RouteException(exception);
    }
    
    if (http2Connection != null) {
      synchronized (connectionPool) {
        allocationLimit = http2Connection.maxConcurrentStreams();
      }
    }
}

```
### connectTunnel  
1.. 拼接 HTTP 的 CONNECT 信息  
2.. 通过 Http1Codec 的 writeRequest 向 TCP 链接发起打开隧道请求;   
3.. 构建服务器响应的 Response, 如果状态码是 200, 说明 CONNECT 请求成功, 如果是 HTTP_PROXY_AUTH,说明需要进行代理认证, 认证失败则抛异常,   
如果成功且服务器响应 了 close, 则返回 tunnelRequest; 如果返回其他状态码, 则继续 while 循环;  

```
private void connectTunnel(int connectTimeout, int readTimeout, int writeTimeout)
      throws IOException {
      //1、创建隧道请求对象  
    Request tunnelRequest = createTunnelRequest();
    HttpUrl url = tunnelRequest.url();
    int attemptedConnections = 0;
    int maxAttempts = 21;
    //一个while循环
    while (true) {
       //尝试连接词说超过最大次数
      if (++attemptedConnections > maxAttempts) {
        throw new ProtocolException("Too many tunnel connections attempted: " + maxAttempts);
      }
      //2、打开socket链接
      connectSocket(connectTimeout, readTimeout);
     //3、请求开启隧道并返回tunnelRequest 
      tunnelRequest = createTunnel(readTimeout, writeTimeout, tunnelRequest, url);

     //4、成功开启了隧道, 跳出while循环
      if (tunnelRequest == null) break; /

      //隧道未开启成功, 关闭相关资源, 继续while循环    
      //当然, 循环次数超限后抛异常, 退出wiile循环
      closeQuietly(rawSocket);
      rawSocket = null;
      sink = null;
      source = null;
    }
}
```
### isEligible  
1.. 如果连接达到共享上限, 则不能重用;  
2.. 非host域必须完全一样, 如果不一样不能重用;  
3.. 如果此时host域也相同, 则符合条件, 可以被复用;  
4.. 如果host不相同, 在HTTP/2的域名切片场景下一样可以复用;  
```
public boolean isEligible(Address address, @Nullable Route route) {
    // If this connection is not accepting new streams, we're done.
    //  负载超过指定最大负载, 不可复用
    //  判断此连接上已创建流的数量是否超过允许承载的并发流的最大数量, 和noNewStreams标识是否置为true
    if (allocations.size() >= allocationLimit || noNewStreams) return false;

    // If the non-host fields of the address don't overlap, we're done.
    //  Address对象的非主机部分不相等, 不可复用
    //  判断路由中的Address和当前接口请求的Address中的dns、proxyAuthenticator、protocols、connectionSpecs、proxySelector、proxy、sslSocketFactory、hostnameVerifier、certificatePinner、port是否一致  
    if (!Internal.instance.equalsNonHost(this.route.address(), address)) return false;

    // If the host exactly matches, we're done: this connection can carry the address.
    //  判断host是否一致, 若判断到这里都一致, 则完美匹配, 返回可用
    if (address.url().host().equals(this.route().address().url().host())) {
      //  这个链接完美的匹配
      return true; // This connection is a perfect match.
    }

    // At this point we don't have a hostname match. But we still be able to carry the request if
    // our connection coalescing requirements are met. See also:
    // https://hpbn.co/optimizing-application-delivery/#eliminate-domain-sharding
    // https://daniel.haxx.se/blog/2016/08/18/http2-connection-coalescing/

    // 1. This connection must be HTTP/2.
    //  若满足一下几点条件, 也可复用连接
    if (http2Connection == null) return false;

    // 2. The routes must share an IP address. This requires us to have a DNS address for both
    // hosts, which only happens after route planning. We can't coalesce connections that use a
    // proxy, since proxies don't tell us the origin server's IP address.
    if (route == null) return false;
    if (route.proxy().type() != Proxy.Type.DIRECT) return false;
    if (this.route.proxy().type() != Proxy.Type.DIRECT) return false;
    if (!this.route.socketAddress().equals(route.socketAddress())) return false;

    // 3. This connection's server certificate's must cover the new host.
    if (route.address().hostnameVerifier() != OkHostnameVerifier.INSTANCE) return false;
    if (!supportsUrl(address.url())) return false;

    // 4. Certificate pinning must match the host.
    try {
      address.certificatePinner().check(address.url().host(), handshake().peerCertificates());
    } catch (SSLPeerUnverifiedException e) {
      return false;
    }

    return true; // The caller's address can be carried by this connection.
}
```
### isHealthy  
满足以下条件, 其中一个, 认为不是 可用的 connection  
1.. socket已经关闭;  
2.. 输入流关闭;  
3.. 输出流关闭;  
4.. 如果是HTTP/2连接, 则HTTP/2连接已关闭;  

### 字段解释  
noNewStreams  
If true, no new streams can be created on this connection. Once true this is always true  
如果 noNewStreams 被设为 true, 则 noNewStreams 一直为 true, 不会被改变, 并且表示这个链接不会再创新的 stream 流  
检查连接中的 socket 是否可用, 不可用则标记 connection.noNewStreams 为 true  


### 参考  
https://blog.csdn.net/chunqiuwei/article/details/74936885  
