```

Xml配置方式
简单初始化 JavaBean
OnlySpringBean
public class OnlySpringBean {     
    private String label;     
    private String desc;      
    public String getLabel() {         
        return label;     
    }      
    public void setLabel(String label) {         
        this.label = label;     
    }      
    public String getDesc() {         
        return desc;     
    }      
    public void setDesc(String desc) {         
        this.desc = desc;     
    }      
    public void printfLog() {         
        LogTrack.e(this);     
    }      
    @Override     
    public String toString() {         
        return "OnlySpringBean{" +                 "label='" + label + '\'' +                 ", desc='" + desc + '\'' +                 '}';     
    }
}



applicationContext.xml
<bean id="OnlySpringBean" class="com.alex.spmb.module.onlyspring.OnlySpringBean">     
<property name="desc" value="描述点内容吧"/>     
<property name="label" value="标签写点什么呢？"/> 
</bean>



测试
public class Zest {     
    public static void main(String[] args) {          
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/applicationContext.xml");         
        OnlySpringBean onlySpringBean = (OnlySpringBean) context.getBean("OnlySpringBean");         
        LogTrack.e(onlySpringBean);      
    } 
}


构造函数初始化JavaBean  

CarBean
public class CarBean {     
    private double price;     
    private String brand;     
    private int wheelCount;      
    public CarBean(String brand) {         
        this.brand = brand;     
    }
}

applicationContext.xml
<bean id="CarBean" class="com.alex.spmb.module.onlyspring.CarBean">     
<constructor-arg index="0" value="江淮汽车"/>    
<constructor-arg index="1" value="2" type="int"/>  
</bean>

测试
public class Zest {     
    public static void main(String[] args) {         
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/applicationContext.xml");         
        CarBean bean = (CarBean) context.getBean("CarBean");         
        LogTrack.e(bean);     
    }
 }


如果value包含特殊字符
<bean id="CarBean" class="com.alex.spmb.module.onlyspring.CarBean">     
<constructor-arg index="0">         <!--        <![CDATA[         ]]]>       -->         
<value type="java.lang.String"><![CDATA[<特殊的汽车>]]]></value>     
</constructor-arg>   
<constructor-arg index="1" value="2" type="int"/> 
</bean>
value是一个实体类

applicationContext.xml
<bean id="CarBean" class="com.alex.spmb.module.onlyspring.CarBean">     
<constructor-arg index="0">         <!--        <![CDATA[         ]]]>       -->         
<value type="java.lang.String"><![CDATA[<特殊的汽车>]]]></value>     
</constructor-arg>     
<constructor-arg index="1" value="2" type="int"/> 
</bean>  

<bean id="PersonBean" class="com.alex.spmb.module.onlyspring.PersonBean">     
<constructor-arg index="0" value="Alex" type="java.lang.String"/>     
<constructor-arg index="1" ref="CarBean"/> 
</bean>


applicationContext.xml（内部bean）
<bean id="PersonBean" class="com.alex.spmb.module.onlyspring.PersonBean">     
<property name="name" value="Alex"/>    
 <property name="carBean">         
 <bean class="com.alex.spmb.module.onlyspring.CarBean">             
 <constructor-arg index="0" value="江淮汽车" type="java.lang.String"/>             
 <constructor-arg index="1" value="4" type="int"/>         
 </bean>    
 </property> 
 </bean>
 
 测试
public class Zest {      
    public static void main(String[] args) {         
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/applicationContext.xml");         
        PersonBean bean = (PersonBean) context.getBean("PersonBean");         
        LogTrack.e(bean);     
    }
 }

value是一个List

applicationContext.xml
<bean id="homeTownBean" class="com.alex.spmb.module.onlyspring.HomeTownBean"       
p:province="安徽" p:city="亳州" p:county="蒙城" />
  <bean id="carBean" class="com.alex.spmb.module.onlyspring.CarBean" 
p:brand="江淮" p:price="1000" p:wheelCount="4"/> 
<bean id="carBean2"       parent="carBean"       p:brand="奇瑞" />  
<util:list id="carBeanList" list-class="java.util.ArrayList">     
<ref bean="carBean">
</ref>     
<ref bean="carBean2"></ref> </util:list>
  <bean id="personBean"       class="com.alex.spmb.module.onlyspring.PersonBean"       p:name="Alex"       
p:homeTownBean-ref="homeTownBean"       p:carBeanList-ref="carBeanList" />


value是一个Map


applicationContext.xml
<bean id="PersonBean" class="com.alex.spmb.module.onlyspring.PersonBean">     
<property name="name" value="Alex"/>     
<property name="carBeanMap">        
<map>             
<entry key="JH" >                 
<bean class="com.alex.spmb.module.onlyspring.CarBean" >                     
<property name="brand" value="江淮"/>                 
</bean>             
</entry>             
<entry key="QY" >                 
<bean class="com.alex.spmb.module.onlyspring.CarBean" >                     
<property name="brand" value="奇瑞"/>                 
</bean>             
</entry>         
</map>     
</property>
  </bean>



applicationContext.xml配置全局的数据源
配置namespace
<beans xmlns="http://www.springframework.org/schema/beans"        
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"        
xmlns:context="http://www.springframework.org/schema/context"        
xmlns:jee="http://www.springframework.org/schema/jee"        
xmlns:mvc="http://www.springframework.org/schema/mvc" 
xmlns:tx="http://www.springframework.org/schema/tx"        
xmlns:util="http://www.springframework.org/schema/util"        
xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd         
http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.2.xsd         
http://www.springframework.org/schema/jee http://www.springframework.org/schema/jee/spring-jee-4.2.xsd         
http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc-4.2.xsd         
http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd         
http://mybatis.org/schema/mybatis-spring http://mybatis.org/schema/mybatis-spring.xsd         
http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util.xsd">




 applicationContext.xml
  <bean class="com.alex.spmb.module.onlyspring.CarBean" id="carBean0">     
 <constructor-arg index="0" value="江淮汽车" type="java.lang.String"/>     
 <constructor-arg index="1" value="4" type="int"/> </bean>
  <bean class="com.alex.spmb.module.onlyspring.CarBean" id="carBean1">     
 <constructor-arg index="0" value="奇瑞汽车" type="java.lang.String"/>     
 <constructor-arg index="1" value="4" type="int"/> </bean>  <util:list id="carList" value-type="java.util.ArrayList">     
 <value type="com.alex.spmb.module.onlyspring.CarBean">carBean0</value>     
 <value type="com.alex.spmb.module.onlyspring.CarBean">carBean1</value> </util:list>  
 
 <bean id="PersonBean" class="com.alex.spmb.module.onlyspring.PersonBean">      
 <property name="name" value="Alex"></property>     
 
 <property name="carBeanList" ref="carList"/>  
 </bean>

```