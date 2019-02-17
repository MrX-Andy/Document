### throttle     

◆ throttleFirst  
先发射一个事件；  
如果在 timeout 内， 所有的连续事件 的时间间隔 都比较小， 下游 每隔 timeout时间，会收到当前时间间隔内的第一个事件；  
◆ throttleLast  
如果在 timeout 内， 所有的连续事件 的时间间隔 都比较小， 下游 每隔 timeout时间，会收到当前时间间隔内的最后一个事件；  
◆ throttleWithTimeout  
