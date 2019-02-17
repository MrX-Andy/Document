### 高阶函数实现接口回调  

```
@Bindable
fun setPhone(phone: String) {
    this.phone = phone
    phoneChangeListener.invoke(phone)
    notifyPropertyChanged(BR.phone)
    notifyChange()
}

fun setOnUserPhoneChangeListener(block: (String) -> Unit) {
    phoneChangeListener = block
}

var phoneChangeListener: (String) -> Unit = {}
```