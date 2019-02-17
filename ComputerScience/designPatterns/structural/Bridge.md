### 桥接模式(Bridge)  

桥接模式是用于“把抽象和实现分开，这样它们就能独立变化”。   
桥接模式使用了封装、聚合，可以用继承将不同的功能拆分为不同的类。  

有抽象类，或者接口 A， 有抽象类 AWrapper， AWrapper类对象持有A类对象，  
AWrapper提供A所需要的一些数据，使用者调用AWrapper类对象，并不直接操作A，AWrapper在内部调用A执行，  
当A内部发生变化，修改AWrapper内部逻辑，并不影响AWrapper的使用者，形成接口隔离；  

◆ 参考  
http://www.importnew.com/6857.html  
https://www.w3cschool.cn/java/java-bridge-pattern.html    
