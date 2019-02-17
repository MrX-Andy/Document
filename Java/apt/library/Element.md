### Element  

◆ Element  
getSimpleName  简单类名  
getEnclosingElement.tosString()  全包名，全路径名  

◆ TypeElement  
getQualifiedName  全类名  
```
//  第一种  
TypeName targetClassName = ClassName.get("PackageName", "ClassName");   

//  第二种  
Elements elements = processingEnv.getElementUtils();
TypeMirror contextTypeMirror = elements.getTypeElement("全类名").asType();
TypeName targetClassName = TypeName.get(contextTypeMirror)  

//  第三种  
TypeName targetClassName = TypeName.get(element.asType());  
```

◆ ExecutableElement  
表示某个类或接口的方法、构造方法或初始化程序（静态或实例），包括注释类型元素。  
对应@Target(ElementType.METHOD)   @Target(ElementType.CONSTRUCTOR)  

◆ PackageElement  
表示一个包程序元素。提供对有关包极其成员的信息访问。  
对应@Target(ElementType.PACKAGE)  

◆ TypeElement  
表示一个类或接口程序元素。提供对有关类型极其成员的信息访问。  
对应@Target(ElementType.TYPE)  

◆ TypeParameterElement  
表示一般类、接口、方法或构造方法元素的类型参数。  
对应@Target(ElementType.PARAMETER)  

◆ VariableElement  
表示一个字段、enum常量、方法或构造方法参数、局部变量或异常参数。  
对应@Target(ElementType.LOCAL_VARIABLE)  

