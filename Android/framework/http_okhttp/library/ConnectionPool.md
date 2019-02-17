### ConnectionPool  
频繁的进行建立Sokcet连接（TCP三次握手）和断开Socket（TCP四次分手）是非常消耗网络资源和浪费时间的,   
HTTP中的 keep-alive 连接对于, 降低延迟和提升速度有非常重要的作用;  
复用连接就需要对连接进行管理, 这里就引入了连接池的概念;  

OkHttp 支持 5 个并发 KeepAlive, 默认链路生命为5分钟(链路空闲后, 保持存活的时间), 连接池由 ConnectionPool 实现, 对连接进行回收和管理;  
okHttp 允许每个地址同时可以有五个连接, 每个连接空闲时间最多为五分钟;  

创建一个适用于单个应用程序的新连接池;
该连接池的参数将在未来的 okHttp 中发生改变;  
目前最多可容乃 5 个空闲的连接, 每个链接存活期是5分钟;  

maxIdleConnections 是指每个地址上最大的空闲连接数;  
put: 放入新连接  
get: 从连接池中获取连接  
evictAll: 关闭所有连接  
connectionBecameIdle: 连接变空闲后调用清理线程  
deduplicate: 清除重复的多路复用线程  
### 连接池的清理工作, 触发流程  
ConnectInterceptor#intercept    
StreamAllocation#newStream  
StreamAllocation#findHealthyConnection  
StreamAllocation#findConnection  
okhttp3.ConnectionPool#put  
```
void put(RealConnection connection) {
    assert (Thread.holdsLock(this));
    if (!cleanupRunning) {
      cleanupRunning = true;
      //  如果没有正确得到 connection , 就会新建一个 Connection ,就会往连接池put一次;  
      //  每一次put操作,都会触发清理连接池的task  
      executor.execute(cleanupRunnable);
    }
    connections.add(connection);
}
```
### cleanup  
查询此连接内部的 StreamAllocation 的引用数量;  
标记空闲连接;  
如果空闲连接超过5个或者 keepAlive 时间大于5分钟, 则将该连接清理掉;  
返回此连接的到期时间, 供下次进行清理;  
全部都是活跃连接, 5分钟时候再进行清理;  
没有任何连接, 跳出循环;  
关闭连接, 返回时间0, 立即再次进行清理;  

清理任务就是异步执行的, 遵循两个指标, 最大空闲连接数量和最大空闲时长, 满足其一则清理空闲时长最大的那个连接, 然后循环执行,   
要么等待一段时间, 要么继续清理下一个连接, 直到清理所有连接, 清理任务才结束, 下一次put的时候, 如果已经停止的清理任务则会被再次触发  

```
//  清理超过空闲连接次数和空闲时间限制的连接, 返回下次执行清理需要等待时长；如果不需要清理, 返回-1；
long cleanup(long now) {
    //  记录在使用的connection
    int inUseConnectionCount = 0;
    //  记录空闲的connection
    int idleConnectionCount = 0;
    //  记录空闲时间最长的connection
    RealConnection longestIdleConnection = null;
    //  记录最长的空闲时间
    long longestIdleDurationNs = Long.MIN_VALUE;
    synchronized (this) {
        //遍历所有的连接, 标记处不活跃的连接, 标记泄漏的连接  
      for (Iterator<RealConnection> i = connections.iterator(); i.hasNext(); ) {
        RealConnection connection = i.next();

        //1. 查询此连接内部的StreanAllocation的引用数量;
        //  如果连接在使用, 继续搜索需要清理的connection;
        //  判断该连接是否非闲置连接
        if (pruneAndGetAllocationCount(connection, now) > 0) {
          inUseConnectionCount++;
          continue;
        }

        //  统计空闲连接数量
        idleConnectionCount++;

        //2. 标记空闲连接;
       // idleAtNanos的值为now-keepAliveDurationNs, 所以idleDurationNs等于keepAliveDurationNs, 这样做避免误差
        long idleDurationNs = now - connection.idleAtNanos;
        //  比较闲置最久的连接, 用longestIdleConnection临时保存
        if (idleDurationNs > longestIdleDurationNs) {
          //  找出空闲时间最长的连接以及对应的空闲时间
          longestIdleDurationNs = idleDurationNs;
          longestIdleConnection = connection;
        }
      }
      //  如果最长空闲时间的连接所包含的socket空闲连接超过最大空闲连接限制或者超过最长空闲时间；那么此连接为待清理的连接  
      //  空闲时间超过限制或空闲connection数量超过限制, 则移除空闲时间最长的connection
      if (longestIdleDurationNs >= this.keepAliveDurationNs
          || idleConnectionCount > this.maxIdleConnections) {
        //3. 如果空闲连接超过5个或者keepalive时间大于5分钟, 则将该连接清理掉;
        //  若闲置时间超时(默认5min)或闲置连接数超出允许最大闲置连接数(默认5个), 则移除该连接
        //  在符合清理条件下, 清理空闲时间最长的连接
        //  存在闲置连接, 闲置最久连接仍未超时, 则返回该连接剩余闲置时间
        connections.remove(longestIdleConnection);
      } else if (idleConnectionCount > 0) {
        //4. 返回此连接的到期时间, 供下次进行清理;
        //  不符合清理条件, 则返回下次需要执行清理的等待时间, 也就是此连接即将到期的时间
        return keepAliveDurationNs - longestIdleDurationNs;
      } else if (inUseConnectionCount > 0) {
        //5. 全部都是活跃连接, 5分钟时候再进行清理;
        //  没有空闲的连接, 则隔keepAliveDuration(分钟)之后再次执行
        //  不存在闲置连接, 则返回一个闲置时间周期
        return keepAliveDurationNs;
      } else {
        //6. 没有任何连接, 跳出循环;
        //  不存在连接, cleanupRunning标识置为false, 返回-1, 退出清理任务
        cleanupRunning = false;
        return -1;
      }
    }

    //7. 关闭连接, 返回时间0, 立即再次进行清理;
    //  特意放到同步锁的外面释放, 减少持锁时间
    closeQuietly(longestIdleConnection.socket());
    return 0;
}
```
### pruneAndGetAllocationCount  
标记泄露连接  
返回一个connection剩余的活跃的 allocation数量;  
```
private int pruneAndGetAllocationCount(RealConnection connection, long now) {
    List<Reference<StreamAllocation>> references = connection.allocations;
    for (int i = 0; i < references.size(); ) {
      Reference<StreamAllocation> reference = references.get(i);

       //  如果 弱引用 StreamAllocation 正在被使用, 则跳过进行下一次循环, 
      if (reference.get() != null) {
        i++;
        continue;
      }

      // We've discovered a leaked allocation. This is an application bug.
      //  没有持有它的对象, 说明上层持有RealConnection已经被回收了
      //  引用实际已被释放, 正常情况下会从引用列表中移除该引用
      StreamAllocation.StreamAllocationReference streamAllocRef =
          (StreamAllocation.StreamAllocationReference) reference;
      String message = "A connection to " + connection.route().address().url()
          + " was leaked. Did you forget to close a response body?";
      Platform.get().logCloseableLeak(message, streamAllocRef.callStackTrace);

      references.remove(i);
      connection.noNewStreams = true;

      // If this was the last allocation, the connection is eligible for immediate eviction.
      //  如果列表为空则说明此连接没有被引用了, 则返回0, 表示此连接是空闲连接
      //  如果这个 connection所有的allocation都没有被引用, 那么这个连接应该马上被清理, 设置该connection的已经空闲 了keepAliveDurationNs时间；
      //  若该connection的引用列表为空, 即为闲置连接, 返回0
      if (references.isEmpty()) {
        connection.idleAtNanos = now - keepAliveDurationNs;
        return 0;
      }
    }
    //  返回引用个数, 表示该连接上仍有多少个流在使用
    return references.size();
}
```
### cleanupRunnable  
```
private final Runnable cleanupRunnable = new Runnable() {
    @Override public void run() {
      while (true) {
        // 清除连接, 并返回执行下一次cleanup的间隔时间, 若返回-1, 则表示不需要再清理
        long waitNanos = cleanup(System.nanoTime());
        if (waitNanos == -1) return;
        if (waitNanos > 0) {
          long waitMillis = waitNanos / 1000000L;
          waitNanos -= (waitMillis * 1000000L);
          synchronized (ConnectionPool.this) {
            try {
              // 等待间隔时间
              ConnectionPool.this.wait(waitMillis, (int) waitNanos);
            } catch (InterruptedException ignored) {
            }
          }
        }
      }
    }
};
```
### connectionBecameIdle  
```
boolean connectionBecameIdle(RealConnection connection) {
    assert (Thread.holdsLock(this));
    if (connection.noNewStreams || maxIdleConnections == 0) {
      //  若noNewStreams为true或连接池最大容量为0, 则移除该连接
      connections.remove(connection);
      return true;
    } else {
      //  唤醒清理线程, 执行清理任务
      notifyAll(); // Awake the cleanup thread: we may have exceeded the idle connection limit.
      return false;
    }
}
```
### deduplicate
```
@Nullable 
Socket deduplicate(Address address, StreamAllocation streamAllocation) {
    assert (Thread.holdsLock(this));
    for (RealConnection connection : connections) {
      //  判断连接是否可用、是否HTTP/2、是否重复
      if (connection.isEligible(address, null)
          && connection.isMultiplexed()
          && connection != streamAllocation.connection()) {
        //  替换streamAllocation中的连接, 并返回旧连接中的socket, 后续关闭该socket
        return streamAllocation.releaseAndAcquire(connection);
      }
    }
    return null;
}

```
