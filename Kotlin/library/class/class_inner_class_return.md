#### 标记@  
标记叫什么无所谓，例如 loop@,  func@, method@   
```
override fun loginWithLiveData(req: LoginReq): MutableLiveData<WrapperEntity<UserEntity>> {
    val loginRepLiveData = MutableLiveData<WrapperEntity<UserEntity>>()
    func@ StringObservable()
            .map { SuccessWrapperBean(UserEntity()) }
            .compose(RxHelper.defaultTransformer(this))
            .compose(RxHelper.wrap2DataTransformer<WrapperEntity<UserEntity>, UserEntity>(this@LoginPresenter))
            .subscribe(object : LiteObserver<UserEntity>() {
                override fun onNext(result: UserEntity) {
                    SpUtil.putString(C.SpKey.userToken, result.userToken)
                    SpUtil.putString(C.SpKey.userId, result.id)
//                        view.onLoginSuccess("登录成功")
                    loginRepLiveData.value = SuccessWrapperBean(UserEntity())
                    LogTrack.i(viewModel)
                    result.logI()
                    return@func loginRepLiveData
                }
            })
    return loginRepLiveData
}
```