### StatefulWidget 有状态布局  
为了构建更复杂的体验 - 例如，以更有趣的方式对用户输入做出反应 - 应用程序通常会携带一些状态。 Flutter使用StatefulWidgets来满足这种需求。  
StatefulWidgets是特殊的widget，它知道如何生成State对象，然后用它来保持状态。  

您可能想知道为什么StatefulWidget和State是单独的对象。在Flutter中，这两种类型的对象具有不同的生命周期：   
Widget是临时对象，用于构建当前状态下的应用程序，而State对象在多次调用build()之间保持不变，允许它们记住信息(状态)。  



### 参考  
https://docs.flutter.io/flutter/widgets/StatefulWidget-class.html  
