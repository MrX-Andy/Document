### 超时时间  

### connectTimeout  
AndroidPlatform#connectSocket  
```
@Override 
public void connectSocket(Socket socket, InetSocketAddress address,
      int connectTimeout) throws IOException {
    try {
      //  连接超时
      socket.connect(address, connectTimeout);
    } catch (AssertionError e) {
      if (Util.isAndroidGetsocknameError(e)) throw new IOException(e);
      throw e;
    } catch (SecurityException e) {
      // Before android 4.3, socket.connect could throw a SecurityException
      // if opening a socket resulted in an EACCES error.
      IOException ioException = new IOException("Exception in connect");
      ioException.initCause(e);
      throw ioException;
    } catch (ClassCastException e) {
      // On android 8.0, socket.connect throws a ClassCastException due to a bug
      // see https://issuetracker.google.com/issues/63649622
      if (Build.VERSION.SDK_INT == 26) {
        IOException ioException = new IOException("Exception in connect");
        ioException.initCause(e);
        throw ioException;
      } else {
        throw e;
      }
    }
}
```

### readTimeout  
ConnectInterceptor#intercept  
StreamAllocation#newStream  
RealConnection#newCodec  
RealBufferedSource#timeout  
Timeout#timeoutNanos  
```
  public HttpCodec newCodec(OkHttpClient client, Interceptor.Chain chain,
      StreamAllocation streamAllocation) throws SocketException {
    if (http2Connection != null) {
      return new Http2Codec(client, chain, streamAllocation, http2Connection);
    } else {
      socket.setSoTimeout(chain.readTimeoutMillis());
      //  读写 超时
      source.timeout().timeout(chain.readTimeoutMillis(), MILLISECONDS);
      sink.timeout().timeout(chain.writeTimeoutMillis(), MILLISECONDS);
      return new Http1Codec(client, streamAllocation, source, sink);
    }
  }
``` 