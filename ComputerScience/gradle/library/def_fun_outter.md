### 外部函数的声明与引用  
假设入口文件是 build.gradle，我们在 alex_tools.gradle文件，声明了一个函数，在app/build.gradle 怎么引用这个函数？   
首先在 项目的入口 build.gradle 文件顶部，引入 apply from: 'alex_tools.gradle'   

假设，我们定义的函数 是 log  
```
def log(String msg) {
    log(null, msg)
}

def log(String tag, String msg) {
    if (loadKeyAtProperty(rootProject.file('gradle.properties'), 'gradleLogEnable') == 'true') {
        println("[日志 LogTrack${tag == null ? "" : tag}]:$msg")
    }
}
```
还需要在 alex_tools.gradle 文件底部进行函数声明  
```
/*函数在这里声明， 其他地方 直接调用*/
ext {
    getGitInfo = this.&getGitInfo
    loadProperties = this.&loadProperties
    log = this.&log
}
```

在 app/build.gradle中 引用  
```
android {
    compileSdkVersion 28
    defaultConfig {
        log "hello"  
        log("hello")
    }
    
    buildTypes {
    
    }
}
```