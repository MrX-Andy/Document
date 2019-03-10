SharePreferences 是 Android 系统提供的, 轻量级数据存储方案, 主要基于键值对方式保存数据;  
在磁盘上存储在 /data/data/包名/shared_prefs/自定义_FileName.xml;  
SharePreference 和 Editor 是两个接口, 对应的实现类是 SharePreferenceImpl 和   SharePreferenceImpl.EditorImpl;  

获取 SharedPreferencesImpl, 是通过 sp 的文件名获取的, 一个文件名对应一个 SharedPreferencesImpl;  


SharePreferenceImpl 在初始化时, 同步方式从磁盘读取数据, 写入内存中;  
如果在读取的过程中, 进行了获取操作, 例如 getString, 会阻塞式等待数据加载完成, 被 notifyAll 之后, 才会顺序往下执行;  
所有的 get 操作, 都是从内存中(hashMap)读取;  

所有的写入操作, 例如 putString, 都是通过 synchronized 代码块的形式, 放在一个临时的 HashMap mModified 中, 必须经过 commit 或者 apply 才会生效;  
apply 方式, 同步写入到内存中, 异步写入到磁盘;  
commit 方式, 同步写入到内存, 同步写入到磁盘;  

apply 方式没有 返回值, commit 方式有 boolean 类型的返回值;  


❀ 为什么 SharePreference 不可以跨进程通信? 数据最终都是存储在磁盘上的;  
假设App 有主进程A, 和一个单独进程的 Service, 它在B进程;  
在A进程中通过 Context 获取一个 SharePreference 对象, 在这个对象初始化的时候, 就会加载所有的磁盘数据到内存中;  
同样的, B进程中也会加载磁盘数据到内存中;  
内存数据是依附在进程的, 也就是进程间是不可见的;  
以后所有的数据更新操作, 无论是 apply 还是 commit 方式提交的数据, 都不会重复的把磁盘的数据, 读取到内存中, 只是增量的改变内存和磁盘中的数据;  
所以, A 进程写入的数据, B进程是看不到的;  
在 api <=10 的时候, 如果 SharePreference 的模式是 MODE_MULTI_PROCESS, 那么在获取对象的时候, 会从磁盘中全量读取数据, 但是从 api11 开始, 已经没有相关的操作了;  
及时是 api10 之前, 也要没读写一次数据, 就要获取一次 SharePreference 对象, 否则也是做不到跨进程通信的;  

❀ 为什么不能存储超大的数据  
第一次初始化 SharePreference 对象, 需要从磁盘解析xml文件, 并读取到内存中, 可能会阻塞主线程, 使界面卡顿, 掉帧;  
这些数据跟随进程的生命周期, 永久占据内存;  

❀ 优化操作  
xml文件越大, 读取越慢, 数据分开放;  
频繁变动的, 和不经常变动的, 分开存储, 提高运行效率;  
架构化的数据, 单独存储;  
数据修改, 集中提交;  
如果不要求立即的到反馈, 最好使用 apply 方式提交数据;  

❀ 怎么能让 SharePreference 跨进程通信  
1.. SharePreference 从磁盘往内存写入数据, 发生在 SharePreference 对象, 初始化的时候;  
2.. 内存缓存了 SharePreference 对象;   
所以, 只要在需要读取其他进程的数据时, 重新创建一个 SharePreference 对象, 即可;  
```
android.app.ContextImpl#sSharedPrefsCache  
@Override
public SharedPreferences getSharedPreferences(File file, int mode) {
    SharedPreferencesImpl sp;
    synchronized (ContextImpl.class) {
        final ArrayMap<File, SharedPreferencesImpl> cache = getSharedPreferencesCacheLocked();
        sp = cache.get(file);
        if (sp == null) {
            checkMode(mode);
            sp = new SharedPreferencesImpl(file, mode);
            cache.put(file, sp);
            return sp;
        }
    }
    return sp;
}
private ArrayMap<File, SharedPreferencesImpl> getSharedPreferencesCacheLocked() {
    if (sSharedPrefsCache == null) {
        sSharedPrefsCache = new ArrayMap<>();
    }
    final String packageName = getPackageName();
    ArrayMap<File, SharedPreferencesImpl> packagePrefs = sSharedPrefsCache.get(packageName);
    if (packagePrefs == null) {
        packagePrefs = new ArrayMap<>();
        sSharedPrefsCache.put(packageName, packagePrefs);
    }
    return packagePrefs;
}
```
### 参考  
https://juejin.im/post/5c34615bf265da614171bf8a  
https://juejin.im/post/5c361469f265da61776c29d0  


