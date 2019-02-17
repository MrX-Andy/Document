cd ~  
touch .bash_profile  
open -e .bash_profile  

在 Users/alex/.bash_profile  文件下  
```
GRADLE_HOME=/Users/alex/WorkSpace/Gradle/gradle-4.5;

export GRADLE_HOME

export PATH=$PATH:$GRADLE_HOME/bin
```
source .bash_profile  
gradle -version  


使用gradle  
./gradlew buildJar  
