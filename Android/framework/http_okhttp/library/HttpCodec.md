### HttpCodec  
writeRequestHeaders(Request request) ：写入请求头   
createRequestBody(Request request, long contentLength) ：写入请求体  
flushRequest()  相当于flush,把请求刷入底层socket  
finishRequest() throws IOException : 相当于flush，把请求输入底层socket并不在发出请求  
readResponseHeaders(boolean expectContinue)  //读取响应头  
openResponseBody(Response response) //读取响应体  
void cancel() ：取消请求  

openResponseBody  
```
@Override 
public ResponseBody openResponseBody(Response response) throws IOException {
    long contentLength = HttpHeaders.contentLength(response);
    if (contentLength != -1) {
      Source source = newFixedLengthSource(contentLength);
      return new RealResponseBody(contentType, contentLength, Okio.buffer(source));
    }
}
```

Http1Codec 创建过程  
```
ConnectInterceptor#intercept  
StreamAllocation#newStream  
RealConnection#newCodec  
Http1Codec#Http1Codec  
```

### 参考  
https://www.jianshu.com/p/92ce01caa8f0  





