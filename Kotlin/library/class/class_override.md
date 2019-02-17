###### 选择性覆盖

```
interface AFun {
    fun sayGood(balance: Int): String = "AFun 说你有余额 $balance"
}

interface BFun {
    fun sayGood(balance: Int): String = "BFun 说你有余额 $balance"
}

class Person : AFun, BFun {
    override fun sayGood(balance: Int): String {
        if (balance > 100) {
            return "你好有钱"
        }
        if (balance > 80) {
            return super<AFun>.sayGood(balance)
        }
        if (balance > 60) {
            return super<BFun>.sayGood(balance)
        }
        return "你好穷啊"
    }
}
```