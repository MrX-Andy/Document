### CallServerInterceptor  

创建 Response, 得到 服务器的返回数据  
```
@Override 
public Response intercept(Chain chain) throws IOException {
    HttpCodec httpCodec = realChain.httpStream();
    StreamAllocation streamAllocation = realChain.streamAllocation();
    RealConnection connection = (RealConnection) realChain.connection();
    //  写入 response head  
    httpCodec.writeRequestHeaders(request);
    //  写入 response body  
    httpCodec.finishRequest();
    if (responseBuilder == null) {
        realChain.eventListener().responseHeadersStart(realChain.call());
        //  读取响应头
        responseBuilder = httpCodec.readResponseHeaders(false);
    }
    
    Response response = responseBuilder
        .request(request)
        .handshake(streamAllocation.connection().handshake())
        .sentRequestAtMillis(sentRequestMillis)
        .receivedResponseAtMillis(System.currentTimeMillis())
        .build();
            
    if (forWebSocket && code == 101) {
      // Connection is upgrading, but we need to ensure interceptors see a non-null response body.
      //  读取响应体
      response = response.newBuilder()
          .body(Util.EMPTY_RESPONSE)
          .build();
    } else {
        response = response.newBuilder()
          //  得到 服务器的返回数据  
          .body(httpCodec.openResponseBody(response))
          .build();
    } 
    

}
```
写入 response body 调用过程  
```
okhttp3.internal.http.CallServerInterceptor#intercept  
httpCodec.finishRequest();
Http1Codec#finishRequest  
RealBufferedSink.flush  
AsyncTimeout$sink$1.write  
OutputStreamSink#write  
```

Http1Codec#openResponseBody  [参见](HttpCodec.md)  

### 参考  
https://blog.csdn.net/chunqiuwei/article/details/76767500  



