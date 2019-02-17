###### 密封类 与 枚举类

```
/*
* sealed class  的子类，只能是他的内部类，或者在同一个.kt文件 中
* */
sealed class PlayerCmd {


}

class Play(val url: String, val position: Long = 0) : PlayerCmd()


class Seek(val position: Long = 0) : PlayerCmd()

object Pause : PlayerCmd()

object Resume : PlayerCmd()

object Stop : PlayerCmd()

enum class PlayerSate {
    IDLE, PAUSE, PLAYING, STOP
}
```