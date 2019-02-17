### 命名参数  与 默认参数
命名参数必须 被 {} 包裹；  
默认参数必须是 常量 static const；  
```

class TutorialHome extends StatelessWidget {
  static const Color defaultBackgroundColor = Color(0xFFFF5722);
  final Widget centerChild;
  final Color backgroundColor;
  TutorialHome({this.centerChild, this.backgroundColor: defaultBackgroundColor});
}
```