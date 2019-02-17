### 双向数据绑定  

StuEntity  
```
class StuEntity {
    val phoneField: ObservableField<String> = ObservableField()
    val nameField: ObservableField<String> = ObservableField()
}
```  

### xml 
```

<data
    >
    <variable
        name="stuEntity"
        type="com.alex.andfun.account.login.model.StuEntity"/>
</data>

<org.alex.widget.StateEditText
    android:id="@+id/etPwd"
    style="@style/base_wm_h22_input_color_gray_66"
    android:layout_toRightOf="@+id/tvPwd"
    android:digits="@string/digits_0_9_a_z_A_Z_"
    android:hint="@string/hint_input_pwd"
    android:imeOptions="actionGo"
    android:paddingLeft="12dp"
    android:paddingRight="16dp"
    android:text="@={stuEntity.phoneField}"
    />
    
<TextView
    android:id="@+id/tvMessage"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:paddingLeft="16dp"
    android:paddingTop="16dp"
    android:paddingRight="16dp"
    android:text="@={stuEntity.phoneField}"
    />
    
```
### ViewModel  
```
val stuEntity = StuEntity()
dataBinding.stuEntity = stuEntity
stuEntity.phoneField.set("123456")  
```