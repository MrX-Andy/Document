###### 自定义getter setter
```
class UserBean {
    var userName: String = ""
        get() {
            return if (field == null) "" else field;
        }
        set(value) {
            field = value;
        }

    var age: Int = 1
        get() {
            return if (field > 18) field else 18;
        }
        set(value) {
            field = value;
        }

    var gender: String = "1"
        get() {
            return if (!"1".equals(field) && !"0".equals(field)) "1" else field;
        }
        set(value) {
            field = value;
        }

    constructor()

    constructor(gender: String) : this() {
        this.gender = gender;
    }

    init {
        LogTrack.w("kotlin语法怪怪的，没见过，说这是初始化方法")
    }

    override fun toString(): String {
        return "UserBean(userName='$userName', age=$age, gender='$gender')"
    }

}
```