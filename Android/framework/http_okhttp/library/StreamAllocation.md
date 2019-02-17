### StreamAllocation  
请求、连接、流  
它负责为一次"请求"寻找"连接"并建立"流", 从而完成远程通信;  
StreamAllocation 相当于是个管理类, 维护了 Connections、Streams 和 Calls 之间的管理, 该类初始化一个Socket连接对象, 获取输入/输出流对象;  

RouteSelector  是在StreamAllocation中被创建的;  
StreamAllocation#connection  
```
private RealConnection findConnection(int connectTimeout, int readTimeout, int writeTimeout,
    int pingIntervalMillis, boolean connectionRetryEnabled) throws IOException {
    result = new RealConnection(connectionPool, selectedRoute);
    acquire(result, false);
}      
```

```
public void acquire(RealConnection connection, boolean reportedAcquired) {
    assert (Thread.holdsLock(connectionPool));
    if (this.connection != null) throw new IllegalStateException();
    this.connection = connection;
    this.reportedAcquired = reportedAcquired;
    connection.allocations.add(new StreamAllocationReference(this, callStackTrace));
}
```

### findConnection  
1.. 查找是否有完整的连接可用:   
Socket 没有关闭  
输入流没有关闭  
输出流没有关闭  
Http2连接没有关闭  
2.. 连接池中是否有可用的连接, 如果有则可用;  
3.. 如果没有可用连接, 则自己创建一个;  
4.. 开始 TCP 连接以及 TLS 握手操作;  
5.. 将新创建的连接加入连接池;  
### releaseIfNoNewStreams  
如果当前使用的 connection 不能够创建 新的 stream,  
那么释放当前使用的 connection, 并返回一个 socket,  拿到这个 socket 并把他关闭,   
```
private Socket releaseIfNoNewStreams() {
    assert (Thread.holdsLock(connectionPool));
    RealConnection allocatedConnection = this.connection;
    if (allocatedConnection != null && allocatedConnection.noNewStreams) {
      //  根据是否存在了 connection 和 connection 的 noNewStreams 成员变量决定是否释放资源;  noNewStreams 为 true表示该 connection不能再创建流;
      return deallocate(false, false, true);
    }
    return null;
}
```
###  deallocate
```
private Socket deallocate(boolean noNewStreams, boolean released, boolean streamFinished) {
    assert (Thread.holdsLock(connectionPool));

    if (streamFinished) {
      this.codec = null;
    }
    if (released) {
      this.released = true;
    }
    Socket socket = null;
    if (connection != null) {
      if (noNewStreams) {
        connection.noNewStreams = true;
      }
      if (this.codec == null && (this.released || connection.noNewStreams)) {
        // this.codec经过上面赋值为null, connection.noNewStreams为true
       // 移除自己在 connection 中的 allocations 保存的引用
        release(connection);
        if (connection.allocations.isEmpty()) {
          //  若经过移除后, connection 引用列表为空了, 执行下面方法
          //  记录系统计时器当前值
          connection.idleAtNanos = System.nanoTime();
          //  调用连接池的方法, 判断是将该连接移除(返回true), 还是清理连接池(返回false)
          if (Internal.instance.connectionBecameIdle(connectionPool, connection)) {
            socket = connection.socket();
          }
        }
        connection = null;
      }
    }
    return socket;
}
```
###  acquire
```
public void acquire(RealConnection connection, boolean reportedAcquired) {
    assert (Thread.holdsLock(connectionPool));
    if (this.connection != null) throw new IllegalStateException();

    //  在此S treamAllocation中缓存 connection
    this.connection = connection;
    //  从连接池获取的 connection, reportedAcquired为 true, 否则为 false
    this.reportedAcquired = reportedAcquired;
    //  增加 connection 被使用的计数
    connection.allocations.add(new StreamAllocationReference(this, callStackTrace));
}
```