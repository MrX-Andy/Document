### cancel  
RealCall#cancel  
RetryAndFollowUpInterceptor#cancel  
StreamAllocation#cancel  
Http1Codec#cancel  
RealConnection#cancel  
Util#closeQuietly  
socket.close  

抛异常的地方  
okhttp3.internal.http.CallServerInterceptor#intercept  
okhttp3.internal.http1.Http1Codec#finishRequest  
RealBufferedSink#flush  
AsyncTimeout#write  
OutputStreamSink#write  
SocketOutputStream#write  socket已经关闭,  再获取输出流, 就会抛异常 SocketException 
 
 