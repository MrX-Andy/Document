在java代码的目录结构对应  
src/debug/java  
src/main/java  
src/main/res  
属性  
aidl	  
The Android AIDL source directory for this source set.    

assets	  
The Android Assets directory for this source set.  

compileConfigurationName	deprecated  
The name of the compile configuration for this source set.  

java	  
The Java source which is to be compiled by the Java compiler into the class output directory.  

jni	  
The Android JNI source directory for this source set.  

jniLibs	  
The Android JNI libs directory for this source set.  

manifest	   
The Android Manifest file for this source set.  

name	  
The name of this source set.  

packageConfigurationName	deprecated  
The name of the runtime configuration for this source set.  

providedConfigurationName	deprecated  
The name of the compiled-only configuration for this source set.  

renderscript	  
The Android RenderScript source directory for this source set.  

res	  
The Android Resources directory for this source set.  

resources	  
The Java resources which are to be copied into the javaResources output directory.   

方法  
setRoot(path)	  
Sets the root of the source sets to a given path. All entries of the source set are located under this root directory.   

```
android {
  ...
  
  sourceSets.jnidebug.setRoot('foo/jnidebug')
  sourceSets {
    main {
    aidl.srcDirs = ['src']
    assets.srcDirs = ['assets']
     java.srcDirs = ['other/java']
     java.srcDirs = ['src']
     renderscript.srcDirs = ['src']
     res.srcDirs = ['other/res1', 'other/res2']
     res.srcDirs = ['res']
     resources.srcDirs = ['src']
     manifest.srcFile 'other/AndroidManifest.xml'
     manifest.srcFile 'AndroidManifest.xml'
    }
    main {
        java {
            srcDir 'src/java'
        }
        resources {
            srcDir 'src/resources'
        }
        
    }
    main.java.srcDirs = ['src/java']
    main.resources.srcDirs = ['src/resources']
    androidTest {
      setRoot 'src/tests'
    }
    androidTest.setRoot('tests')
  }
}

```