###### 有自己的上下文的 领域定义语言

```
class IOExtension {
    fun File.mkdirsNewFile(): File {
        this.absolutePath.logW()
        val parentFile = File(this.parent)
        if (!parentFile.exists()) {
            parentFile.mkdirs()
        }
        if (!this.exists()) {
            this.createNewFile()
        }
        return this
    }

}

fun <R> ioFun(function: IOExtension.() -> R): R = IOExtension().function()
ioFun { 
    File("").mkdirsNewFile()
}



inline fun <R> ifOr(source: R, vararg arrays: R, action: (R) -> Unit) {
    val flag = arrays.any {
        it == source
    }
    if (flag) {
        action(source)
    }
}

inline fun <R> ifAnd(source: R, vararg arrays: R, action: (R) -> Unit) {
    val flag = arrays.all {
        it == source
    }
    if (flag) {
        action(source)
    }
}
```