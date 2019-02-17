### Focus  

强制让【最后一个占用焦点的输入框】失去焦点 
```
fun View.clearEditorFocus() {
    this.isFocusableInTouchMode = true;
    this.isFocusable = true;
    this.requestFocus()
}
```
假设有A、B、C三个输入框；  
默认情况下，焦点会交给A，  
只有手动点击输入框B，A才会失去焦点；  
当调用clearEditorFocus方法，会让最后一个获得焦点的输入框失去焦点；  
