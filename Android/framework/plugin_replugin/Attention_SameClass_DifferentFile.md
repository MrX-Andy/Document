###### 相同的类，不同的文件

> 先描述一下，什么是【相同的类，不同的文件】

我们假设，有Host 模块，和JackPlugin 模块。有两个完全一样的类，但是会出现类签名不一致的问题。

> 注意事项
- Plugin 的Fragment不能添加到 Host的Activity
- Plugin 的对话框不能在 Host里很好滴表现，例如对话框有RecyclerView，列表一定会全屏，强制设置高度，背景会是黑色的
- Plugin 的Intent传值，传递实体类，必须实现parcelable 接口，需要序列化的字段不能为 null
- Plugin 的RecyclerView不能使用Host的Adapter




