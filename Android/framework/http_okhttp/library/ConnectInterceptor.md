### ConnectInterceptor  
初始化 HttpCodec ;  
初始化 RealConnection , 

初始化 RealConnection 的过程   
ConnectInterceptor#intercept ->  
StreamAllocation#newStream ->   
StreamAllocation#findHealthyConnection ->   
StreamAllocation#findConnection ->   
OkHttpClient#static#Internal#instance#put ->   


读取数据的调用过程  
ConnectInterceptor#intercept  
StreamAllocation#newStream  
StreamAllocation#findHealthyConnection  
StreamAllocation#findConnection  
RealConnection#connect  
RealConnection#connectSocket  
Okio#sink(java.net.Socket)  


