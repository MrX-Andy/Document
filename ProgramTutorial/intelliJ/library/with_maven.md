### 配置Maven库  
官网  
http://maven.apache.org/download.cgi  
下载文件  
找一个位置存储已下载的文件，解压缩  
打开配置文件  
修改localRepository  

关联maven仓库  
新建一个 pom.xml  
```
<?xml version="1.0" encoding="UTF-8"?>
 <project xmlns="http://maven.apache.org/POM/4.0.0"          
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"          
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">    
     <modelVersion>4.0.0</modelVersion>      
     <groupId>com.alex.springmvcapp</groupId>     
     
     <artifactId>springmvcapp</artifactId>     
     <version>1.0-SNAPSHOT</version>     
     
    <build>        
    <plugins>            
        <plugin>                 
            <groupId>org.apache.maven.plugins</groupId>                 
            <artifactId>maven-compiler-plugin</artifactId>                 
            <configuration>                     
                <source>1.8</source>                     
                <target>1.8</target>                 
            </configuration>             
        </plugin>         
        </plugins>     
    </build>      
           
    <dependencies>          
        <!-- https://mvnrepository.com/artifact/com.google.code.gson/gson -->         
        <dependency>             
        <groupId>com.google.code.gson</groupId>             
        <artifactId>gson</artifactId>             
        <version>2.8.0</version>         
        </dependency>        
    </dependencies>
 </project>
```