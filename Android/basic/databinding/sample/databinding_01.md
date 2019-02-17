###### 数据绑定
参考  
https://developer.android.google.cn/topic/libraries/data-binding/expressions  

> 单项数据绑定

```
<data
    >

    <variable
        name="accountInfoViewModel"
        type="com.alex.andfun.account.AccountInfoViewModel"/>

    <variable
        name="userBean"
        type="com.alex.andfun.model.UserBean"/>

</data>

<TextView
    android:id="@+id/tvContent"
    android:layout_width="0dp"
    android:layout_height="0dp"
    android:gravity="center"
    android:text="@{userBean.toString()}"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintTop_toBottomOf="@+id/simpleTooBar"
    tools:ignore="HardcodedText"/>

    
```