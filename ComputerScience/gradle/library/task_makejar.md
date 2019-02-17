需求，根据class文件，生成jar包  
假设，class文件在：  build/intermediates/classes/basic/release  
在 module 的 gradle文件  
```
task makeJar(type: Jar) {
    baseName 'AndFun_App'
    from('build/intermediates/classes/basic/release')
    into( 'build/libs')
    exclude('android/')
    exclude('**/BuildConfig.class')
    exclude('**/BuildConfig\$*.class')
    exclude('**/R.class')
    exclude('**/R\$*.class')
    include('**/*.class')
}
makeJar.dependsOn(build)
```

```
task makeJar(type: Jar) {
    baseName 'alex_tools'
    version = "1.0.0"
    from('build/intermediates/classes/debug/')
//    into( 'build/libs')
    exclude('android/')
    exclude('**/BuildConfig.class')
    exclude('**/BuildConfig\$*.class')
    exclude('**/R.class')
    exclude('**/R\$*.class')
    exclude('com/**')
    exclude('io/**')
    include('/org/alex/util/**/*.class')
    include('/org/alex/okhttp/**/*.class')
}
makeJar.dependsOn(build)
```

