gradle -q taskY taskX  
```
task taskX << {
    println '输出 taskX'
}
task taskY << {
    println '输出 taskY'
}
task taskZ << {
    println '输出 taskZ'
}


taskY.mustRunAfter taskX
# gradle -q taskY taskX 
输出 taskX
输出 taskY

taskY.shouldRunAfter taskX
# gradle -q taskY taskX  
输出 taskX
输出 taskY  

taskX.dependsOn taskY
taskY.dependsOn taskZ
taskZ.shouldRunAfter taskX
# gradle -q taskX
输出 taskZ
输出 taskY
输出 taskX

```
```
task customTask1 {
    doFirst {
        println 'customTask1:doFirst'
    }
    doLast {
        println 'customTask1:doLast'
    }
}

tasks.create("customTask2") {
    doFirst {
        println 'customTask2:doFirst'
    }
    doLast {
        println 'customTask2:doLast'
    }
}
```
```
task ex35Hello << {
    println 'hello'
}

task ex35World << {
    println 'world'
}

task ex35Main(dependsOn: ex35Hello) {
    doLast {
        println 'main'
    }
}

task ex35MultiTask {
    dependsOn ex35Hello,ex35World
    doLast {
        println 'multiTask'
    }
}
```
```
task ex36Hello << {
    println 'dowLast1'
}

ex36Hello.doFirst {
    println 'dowFirst'
}

ex36Hello.doLast {
    println project.hasProperty('ex36Hello')
    println 'dowLast2'
}
```
```
task queryProjectInfo<<{
    println "项目名  ${project.name}"
    println "项目相对路径  ${project.path}"
    println "项目描述  ${project.description}"
    println "项目的绝对路径   ${project.projectDir}"
    println "项目的build文件绝对路径  ${project.buildDir}"
    println "项目所在的group  ${project.group}"
    println "项目的版本号  ${project.version}"
    println "项目的ant对象  ${project.ant}"
}
```
◆ 参考   
https://github.com/rujews/android-gradle-book-code/tree/master/chapter04  
