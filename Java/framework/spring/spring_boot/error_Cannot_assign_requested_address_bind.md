### Cannot assign requested address: bind
```
2017-10-18 22:02:02.898 debug 8000 --- [           main] o.a.coyote.http11.Http11NioProtocol      : Failed to start end point associated with ProtocolHandler ["https-jsse-nio-192.168.0.8-8446"]

java.net.BindException: Cannot assign requested address: bind
...
...
2017-10-18 22:02:02.899 debug 8000 --- [           main] o.apache.catalina.core.StandardService   : Failed to start connector [Connector[HTTP/1.1-8446]]

org.apache.catalina.LifecycleException: Failed to start component [Connector[HTTP/1.1-8446]]
```
#### 解决办法

> 先看一下本机地址 和 SpringBoot/application.properties 的配置参数

- IPv4 地址 . . . . . . . . . . . . : 192.168.0.5

- server.port=8446
- server.address=192.168.0.8

显然IP地址不对，修改一下application.properties 的配置参数

问题顺利解决


