### 开始自定义plugin  
假设我们的插件的 module 叫 plugin_aspectj  

那么 plugin_aspectj 的 build.gradle   
```
apply plugin: 'groovy'
apply plugin: 'maven'
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile gradleApi()
    compile localGroovy()
    //  下面两个 是扩展需要的  
    compile 'org.aspectj:aspectjtools:1.8.13'
    compile 'org.aspectj:aspectjrt:1.8.13'
    compile 'com.android.tools.build:gradle:3.0.1'
}
repositories {
    mavenCentral()
}
sourceSets {
    //  注意， groovy 的代码，必须写在 groovy ，这个主包内， 写在 java，这个主包内，就不行， 暂时 这么理解  
    main {
        groovy {
            srcDir 'src/main/groovy'
        }

        resources {
            srcDir 'src/main/resources'
        }
    }
}
uploadArchives {
    repositories {
        mavenDeployer {
            //设置插件的GAV参数，和 classpath 'org.alex.plugin:aspectj:1.0.0'  一致的
            pom.groupId = 'org.alex.plugin'
            pom.artifactId = 'aspectj'
            pom.version = '1.0.0'
            //文件发布到下面目录， minePluginUri = file:///Users/alex/WorkSpace/Gradle/MinePlugin/repo/  
            repository(url: uri(minePluginUri))
        }
    }
}

```  

◆  properties    
resources/META-INF/gradle-plugins/org.alex.plugin.aspectj.properties  
这个 properties 的文件，他的文件名很重要， 因为如果 我们用到  这个插件的话，apply plugin: 'org.alex.plugin.aspectj'    
没错， properties 的名字， 和 apply plugin 的 索引值，是一样的；  

记得先执行 uploadArchives， 再同步 和 引用；  
