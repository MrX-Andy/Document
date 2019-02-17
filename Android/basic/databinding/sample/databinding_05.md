#### OnLifecycleEvent  和 LifecycleObserver  

##### LoginDataTask  
```
class LoginDataTask(lifecycleOwner: LifecycleOwner) : LifecycleObserver {
    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        "开始".logI()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        "暂停".logI()
    }
}
```  

##### LoginActivity  
```
class LoginActivity : AbsActivity<LoginContract.ViewModel>(), LoginContract.View {

    /**
     * 初始化所有基础数据，
     */
    override fun onCreateData(savedInstanceState: Bundle?) {
        lifecycle.addObserver(LoginDataTask(this))

    }
    override fun onResume() {
        super.onResume()
    }
}
```