###### 文件操作
```
File
File(AppCon.jsonPath + "/Tmp.json").writeText("打开源文件  清空写； 源文件 不存在， 新建 再写入")
File(AppCon.jsonPath + "/Tmp.json").appendText("打开源文件  追加写； 源文件 不存在， 新建 再写入")

File(AppCon.jsonPath + "/Tmp.json").inputStream().bufferedReader().use {
    reader ->
    reader.readText()
}

/**
 * 删除 所有子目录 子文件
 */
deleteRecursively

/**
* 源文件 拷贝到 目标文件；
* 如果 目标文件 、 目标路径不存在，会自动创建；
* @param overwrite
 *      true 目标文件 允许被 清空写入
 *      false 目标文件 如果存在，停止拷贝， 源文件 目标文件 都保持不变
* */
copyTo(targetFile, false, 1024)

/**
 * 源文件 拷贝到 目标文件；
 * 如果 目标文件 、 目标路径不存在，会自动创建；
 * @param overwrite
 *      true 目标文件 允许被 清空写入
 *      false 目标文件 如果存在，停止拷贝， 源文件 目标文件 都保持不变
 * 如果 发生了 error，      流程 怎么继续 依赖 参数 onError
 * */
.copyRecursively(targetFile, true, { _, ex ->
    ex.logW()
    OnErrorAction.SKIP
})

/**
 * 获取一个访问该目录及其所有内容的序列以自上而下的顺序。
 * 使用深度优先搜索，并在所有文件之前访问目录。
 */
.walkTopDown()

/**
 * 获取一个访问该目录及其所有内容的序列以自下而上的顺序。
 * 使用深度优先搜索，并在所有文件之前访问目录。
 */
.walkBottomUp()
```