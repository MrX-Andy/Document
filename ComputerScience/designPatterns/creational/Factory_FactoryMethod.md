### 工厂方法(Factory Method Pattern)  
工厂父类负责定义公共接口，而工厂子类则负责生成具体的产品对象，即通过工厂子类来确定究竟应该实例化哪一个具体产品类；  
工厂类，有接口，有继承关系；产品类，也有接口，也有继承关系；  
工厂类，需要创建；产品类，也需要创建；  
每一种工厂，生产一种产品；  
◆ 优点    
◑ 工厂可以自主确定创建何种产品对象， 而如何创建这个对象的细节则完全封装在具体工厂内部；  
◑ 在系统中加入新产品时，无须修改抽象工厂和抽象产品提供的接口，也无须修改其他的具体工厂和具体产品，而只要添加一个具体工厂和具体产品就可以了；  

◆ 缺点  
◑ 在添加新产品时，需要编写新的具体产品类，而且还要提供与之对应的具体工厂类，系统中类的个数将成对增加；  
◑ 由于考虑到系统的可扩展性，需要引入抽象层，增加了系统的抽象性和理解难度，且在实现时可能需要用到DOM、反射等技术，增加了系统的实现难度；  

◆ 工厂方法模式在Java中应用  
```
Connection conn=DriverManager.getConnection("jdbc:microsoft:sqlserver://localhost:1433; DatabaseName=DB;user=sa;password=");
Statement statement=conn.createStatement();
ResultSet rs=statement.executeQuery("select * from UserInfo");
```