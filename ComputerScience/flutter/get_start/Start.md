####  开始  

安装 SDK  
https://flutter.io/sdk-archive/#windows  
环境变量  
https://flutter.io/setup-windows/#update-your-path  

step 01  
```
cd /Users/alex/WorkSpace/sourceTree  
git clone -b beta https://github.com/flutter/flutter.git  
```

step 02  
```
export PUB_HOSTED_URL=https://pub.flutter-io.cn
export FLUTTER_STORAGE_BASE_URL=https://storage.flutter-io.cn
export FLUTTER_HOME=/Users/alex/WorkSpace/sourceTree/flutterSDK
export PATH=$PATH:$FLUTTER_HOME/bin:
```
step 03  
flutter doctor  

step 04  
flutter upgrade    

step 05  
flutter doctor --android-licenses  

step 06  
https://brew.sh/index_zh-cn  
https://www.dartlang.org/tools/sdk  
```
/usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
brew install wget
brew tap dart-lang/dart
brew install dart
brew upgrade dart
```

dart sdk location
```
/Users/alex/WorkSpace/sourceTree/flutterSDK/bin/cache/dart-sdk
```

