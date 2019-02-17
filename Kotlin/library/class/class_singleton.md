###### 单例怎么写

```
class BaseUtil private constructor() {
   
	ompanion object {
        private var application: Application? = null
        var instance: BaseUtil = BaseUtil()
            get(): BaseUtil {
                if (field == null) {
                    synchronized(BaseUtil::class.java) {
                        field = if (field == null) BaseUtil() else field
                    }
                }
                return field;
            }
    }

}

单例类
object LoginBean {
    internal var name: String? = null

    fun foo(){

    }

}
访问
override fun onCreateData(savedInstanceState: Bundle?) {
    /*等同于 调用 LoginBean.getInstance().foo()*/
    LoginBean.foo()

}
```