### Job  

Job是协程创建的后台任务的概念，它持有该协程的引用。Job接口实际上继承自CoroutineContext类型。一个Job有如下三种状态：  

● New (optional initial state)  新建 （可选的初始状态）  非激活态  未完成  
● Active (default initial state)  活动中（默认初始状态）  是激活态  未完成  
● Completed (final state)  已结束（最终状态）  非激活态  已完成  

[job.join函数](../job/join.md)；  
[cancel](../job/cancel.md)；  
cancelAndJoin = cancel + join；  

