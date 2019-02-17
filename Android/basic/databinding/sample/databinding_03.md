###  双向绑定  

Observable  
```
class UserObservable(name: String = "", phone: String = "13146008029") : BaseObservable() {
    var name = ""
    private var phone = ""

    init {
        this.name = name
        this.phone = phone
    }

    @Bindable
    fun getPhone(): String {
        return phone
    }

    @Bindable
    fun setPhone(phone: String) {
        this.phone = phone
        notifyPropertyChanged(BR.phone);
    }
}
```  

### ViewModel  
```

class LoginViewModel(view: LoginContract.View) :
        AbsActivityViewModel<LoginContract.View, LoginContract.Presenter, LoginModuleBinding>(view),
        LoginContract.ViewModel {
    private val userObservable: UserObservable = UserObservable()

    override fun createPresenter() = LoginPresenter(view, this)

    override fun onCreateDataBinding(dataBinding: LoginModuleBinding) {
        dataBinding.loginViewModel = this
    }

    override fun onCreateData(savedInstanceState: Bundle?) {
        userObservable.phone = "13146008029"
        dataBinding.userObservable = userObservable

    }

}


```
### xml  
```
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        xmlns:tools="http://schemas.android.com/tools"
        tools:ignore="RtlSymmetry,SpUsage,HardcodedText,RtlHardcoded,ContentDescription,ScrollViewSize">

    <data
        >

        <variable
            name="loginViewModel"
            type="com.alex.andfun.account.login.contract.LoginViewModel"/>

        <variable
            name="loginRespEntity"
            type="com.alex.andfun.account.login.contract.LoginRespEntity"/>

        <variable
            name="userObservable"
            type="com.alex.andfun.account.login.model.UserObservable"/>
    </data>

    <LinearLayout
        android:id="@+id/layoutRoot"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">


        <org.alex.widget.SimpleTooBar
            android:id="@+id/simpleTooBar"
            android:layout_width="match_parent"
            android:layout_height="@dimen/title_bar_height"
            app:stb_leftImgVisibility="gone"
            app:stb_title="登录"/>

        <RelativeLayout style="@style/base_wm_hw">

            <TextView
                android:id="@+id/tvPhone"
                style="@style/base_ww_hw_text_color_gray_33"
                android:layout_width="62dp"
                android:layout_centerVertical="true"
                android:gravity="center"
                android:text="手机号"/>

            <org.alex.widget.StateEditText
                android:id="@+id/etPhone"
                style="@style/base_wm_h22_input_color_gray_66"
                android:layout_toRightOf="@+id/tvPhone"
                android:digits="@string/digits_phone"
                android:hint="@string/hint_input_phone"
                android:imeOptions="actionNext"
                android:inputType="phone"
                android:maxLength="13"
                android:nextFocusForward="@+id/etPwd"
                android:paddingLeft="12dp"
                android:paddingRight="62dp"
                android:text="@={userObservable.phone}"/>
        </RelativeLayout>

        <View
            style="@style/base_wm_hw"
            android:layout_height="0.5dp"
            android:background="@color/colorAccent"/>

        <RelativeLayout style="@style/base_wm_hw">

            <TextView
                android:id="@+id/tvPwd"
                style="@style/base_ww_hw_text_color_gray_33"
                android:layout_width="62dp"
                android:layout_centerVertical="true"
                android:gravity="center"
                android:text="密码"/>

            <org.alex.widget.StateEditText
                android:id="@+id/etPwd"
                style="@style/base_wm_h22_input_color_gray_66"
                android:layout_toRightOf="@+id/tvPwd"
                android:digits="@string/digits_0_9_a_z_A_Z_"
                android:hint="@string/hint_input_pwd"
                android:imeOptions="actionGo"
                android:paddingLeft="12dp"
                android:paddingRight="16dp"
                android:text="123456"
                />
        </RelativeLayout>

        <View
            style="@style/base_wm_hw"
            android:layout_height="0.5dp"
            android:background="@color/colorAccent"/>


        <TextView
            android:id="@+id/tvLogin"
            style="@style/base_wm_rect_red_1"
            android:layout_width="match_parent"
            android:layout_marginLeft="16dp"
            android:layout_marginTop="32dp"
            android:layout_marginRight="16dp"
            android:text="@{loginRespEntity.name}"
            />

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"

            android:orientation="horizontal">

            <TextView
                android:id="@+id/tvRegister"
                android:layout_width="0dp"
                android:layout_height="44dp"
                android:layout_marginLeft="16dp"
                android:layout_marginRight="16dp"
                android:layout_weight="1"
                android:gravity="left"
                android:paddingLeft="16dp"
                android:paddingTop="24dp"
                android:text="去注册"
                tools:ignore="RtlSymmetry"/>

            <TextView
                android:id="@+id/tvLogout"
                android:layout_width="0dp"
                android:layout_height="44dp"
                android:layout_marginLeft="16dp"
                android:layout_marginRight="16dp"
                android:layout_weight="1"
                android:gravity="left"
                android:onClick="onClickEvent"
                android:paddingLeft="16dp"
                android:paddingTop="24dp"
                android:text="退出登录"
                tools:ignore="RtlSymmetry"/>

            <TextView
                android:id="@+id/tvReGetPwd"
                android:layout_width="0dp"
                android:layout_height="44dp"
                android:layout_marginLeft="16dp"
                android:layout_marginRight="16dp"
                android:layout_weight="1"
                android:gravity="right"
                android:paddingTop="24dp"
                android:text="忘记密码？"/>
        </LinearLayout>

        <TextView
            android:id="@+id/tvContent"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:paddingLeft="16dp"
            android:paddingTop="16dp"
            android:paddingRight="16dp"
            android:text="@{userObservable.phone}"/>

    </LinearLayout>

</layout>

```
### 步骤说名  
1.. 写entity  
2.. 写xml variable 节点  
3.. rebuild  否则，id找不到  
4.. 写 viewModel  

