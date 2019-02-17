### 简单工厂（Simple Factory Pattern）  

简单工厂模式，又称为静态工厂方法(Static Factory Method)模式，它属于类创建型模式；  
根据不同的参数，返回不同类的实例；  
专门定义一个类来负责创建其他类的实例，被创建的实例通常都具有共同的父类；  

◆ 优点  
通过使用工厂类，外界不再需要关心如何创建产品，只要提供对应参数，就可以得到对应的的产品；  
并且可以按照接口规范来调用产品对象的所有功能（方法），构造容易、逻辑简单；  

◆ 缺点    
◑ 简单工厂模式中的if else判断非常多，如果有一个新产品要加进来，就要建一个对应类、再加入一个 else if 分支，对系统的维护和扩展不利；  
◑ 工厂类中集合了所有的类的实例创建逻辑，什么时候它不能工作了，整个系统都会受到影响；  
◑ 一般只在很简单的情况下应用，  比如当工厂类负责创建的对象比较少时；  
◑ 简单工厂模式由于使用了静态工厂方法，造成工厂角色无法形成基于继承的等级结构。  

◆ 简单工厂模式在Java中的应用  
◑ DateFormat  
public final static DateFormat getDateInstance();  
public final static DateFormat getDateInstance(int style);  
public final static DateFormat getDateInstance(int style,Locale locale);  
◑ Cipher  
Cipher cp = Cipher.getInstance("AES");  

