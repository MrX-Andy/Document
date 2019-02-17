### 怎么拿到IP地址的  
android中有dhcpcd这个服务端负责从网络中获得地址，而系统会通过一个库（libnetutils）去请求和控制服务端。并写入属性共享给开发者。  

### 名词解释  

1.. DHCP  
动态主机设置协议  
Dynamic Host Configuration Protocol  
是一个局域网的网络协议，使用UDP协议工作，主要有两个用途：  
用于内部网或网络服务供应商自动分配IP地址；  
给用户用于内部网管理员作为对所有计算机作中央管理的手段。  

