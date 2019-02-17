###### companion object（伴生对象）
> Kotlin中并没有 public static final 字段，怎么表示静态常量呢？
```
class LoginBean {
    internal var name: String? = null

    companion object {
        var TAG = "TAG"
    }
}
访问的时候，就是
override fun onCreateData(savedInstanceState: Bundle?) {
    LoginBean.TAG;
}
```
> 伴生对象特殊用法
```

interface ModuleService {
    companion object :
            Function0<ModuleService>,
            Function1<OnUploadListener, ModuleService>,
            Function2<OnDownloadListener, OnUploadListener, ModuleService> {
        override fun invoke(onDownloadListener: OnDownloadListener, onUploadListener: OnUploadListener)
                = RetrofitUtil.getService(onUploadListener, onDownloadListener, ModuleService::class.java, C.UrlEnum.BASE_API_URL)

        override fun invoke(onUploadListener: OnUploadListener)
                = RetrofitUtil.getService(onUploadListener, null, ModuleService::class.java, C.UrlEnum.BASE_API_URL)

        override fun invoke()
                = RetrofitUtil.getService(null, null, ModuleService::class.java, C.UrlEnum.BASE_API_URL)

    }
}

private class Model : AccountInfoContract.Model {
    override fun getAccountInfo(reqEntity: GetAccountReqEntity): Observable<WrapperBean<UserBean>> {
        val params = mapOf<String, String>("" to "")
        return ModuleService.invoke().login(params)
    }
}
```