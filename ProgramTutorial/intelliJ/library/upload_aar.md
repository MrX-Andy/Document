### 在module的gradle
```

afterEvaluate { project ->
    uploadArchives {
        repositories {
            mavenDeployer {
                //设置插件的GAV参数
                pom.groupId = 'org.alex.library'
                pom.artifactId = 'module'
                pom.version = "${alexToolModuleVersion}"
                //文件发布到下面目录
                repository(url: uri(minePluginUri))
            }
        }
    }

    /*上传  javaDoc*/
    task androidJavadocs(type: Javadoc) {
        source = android.sourceSets.main.java.srcDirs
        classpath += project.files(android.getBootClasspath().join(File.pathSeparator))
        options {
            encoding 'utf-8'
            charSet 'utf-8'
            links 'http://docs.oracle.com/javase/7/docs/api/'
            linksOffline "https://developer.android.com/reference", "${android.sdkDirectory}/docs/reference"
        }
        exclude '**/BuildConfig.java'
        exclude '**/R.java'
        options.encoding = 'utf-8'
        failOnError false
    }
    task androidJavadocsJar(type: Jar, dependsOn: androidJavadocs) {
        classifier = 'javadoc'
        from androidJavadocs.destinationDir
    }
    task androidSourcesJar(type: Jar) {
        classifier = 'sources'
        from android.sourceSets.main.java.sourceFiles
    }
    artifacts {
        archives androidSourcesJar
        archives androidJavadocsJar
    }
}
```