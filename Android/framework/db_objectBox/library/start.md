全局的gradle  
```
dependencies {         
    classpath 'com.android.tools.build:gradle:2.2.3'         
    classpath 'io.objectbox:objectbox-gradle-plugin:0.9.7'     
} 
```

module的gradle  
```
apply plugin: 'io.objectbox'
dependencies {     
    compile fileTree(dir: 'libs', include: ['*.jar'])     
    compile 'io.objectbox:objectbox-android:0.9.7' 
}
```
写好实体类  
```
package com.alex.objectbox.module.orderlist.model;  
import io.objectbox.annotation.Entity; 
import io.objectbox.annotation.Generated; 
import io.objectbox.annotation.Id;  
/**  * 作者：Alex  * 时间：2017/2/22 15:58  * 简述：  */ 
@Entity public class OrderListBean {     
    @Id     private Long id;     
    private String orderId;     
    private String createTime;     
    private String customer;      
    @Generated     
    public OrderListBean() {     }      
    public Long getId() {         
    return id;     
    }      
    public void setId(Long id) {         
    this.id = id;     
    }  
    public String getOrderId() {         
    return orderId;     
    }      
    public void setOrderId(String orderId) {         
    this.orderId = orderId;     
    }      
    public String getCreateTime() {         
    return createTime;     }      
    public void setCreateTime(String createTime) {         
    this.createTime = createTime;     
    }      
    public String getCustomer() {         
    return customer;     }      
    public void setCustomer(String customer) {         
    this.customer = customer;     
    }      
}
```
Build - Make Project  
写代码  
在 Application 中初始化：  
```
public class App extends Application {      
    private BoxStore boxStore;      
    @Override     
    public void onCreate() {         
    super.onCreate();         
    BaseUtil.getInstance().init(this);         
    boxStore = MyObjectBox.builder().androidContext(this).build();     }          
    public BoxStore getBoxStore() {         return boxStore;     
    } 
}
```