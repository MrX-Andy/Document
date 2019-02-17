### Parcelable 和 Serializable  

● Serializable（Java自带）：    
Serializable 用于 数据持久化、网络流；    
Serializable是序列化的意思，表示将一个对象转换成可存储或可传输的状态。序列化后的对象可以在网络上进行传输，也可以存储到本地；  
Serializable代码量少，写起来方便；  
Serializable 比较慢是使用了反射，这种机制会在序列化的时候创建许多的临时对象，容易触发垃圾回收;  


● Parcelable（android 专用）：  
Parcelable 用于 内存间 传递数据；   
除了Serializable之外，使用Parcelable也可以实现相同的效果，  
不过不同于将对象进行序列化，Parcelable方式的实现原理是将一个完整的对象进行分解，  
而分解后的每一部分都是Intent所支持的数据类型，这样也就实现传递对象的功能了。  
Parcelable代码多一些，增减字段不方便；  


● 选择序列化方法的原则  
在使用内存的时候，Parcelable比Serializable性能高，所以推荐使用Parcelable；  
Serializable在序列化的时候会产生大量的临时变量，从而引起频繁的GC；  
Parcelable不能使用   在，将数据存储在磁盘上的情况，因为Parcelable不能很好的保证数据的持续性在外界有变化的情况下。  
尽管Serializable效率低点，但此时还是建议使用Serializable 。  

● Parcelable原理  
通过 writeToParcel 将JavaBean映射成 Parcel 对象，再通过 createFromParcel 将Parcel对象映射成你的对象，也可以将Parcel看成是一个流，  
通过writeToParcel把对象写到流里面，在通过createFromParcel从流里读取对象，只不过这个过程需要你来实现，因此写的顺序和读的顺序必须一致。  

### Parcelable 的读写顺序必须一致  
利用 mData, mDataPos, mDataCapacity 这三个变量, 控制数据的存储位置,  
mData  是指向parcel缓存的首地址;  
mDataSize  是指 存储的数据大小, 使用的空间大小;  
mDataCapacity  表示表示parcel缓存容量;  
mDataPos 当前数据的位置, 和读文件的游标类似, 指向parcel缓存中空闲区域的首地址;   
mObjects  flat_binder_object 对象的位置数据, 其实就是个数组, 里面保存的不是数据, 而且地址的偏移;  
mObjectsSize  mObjects.size  
mObjectsCapacity  objects 偏移地址的空间大小;  
主要函数有:  
writeInt32, writeInt64, writeFloat, writeDouble, writeIntArray;  
writeString8, writeString16, 
整个parcel缓存是一块连续的内存;  
物理地址 = 有效地址+偏移地址;  
1.. 首先会判断先写入的int数据的字节数是否超过了data的容量;  
2.. 如果没有超过, 会执行数据的写入;  
3.. 修改偏移地址, 将偏移地址加上新增加的数据的字节数;  

4.. 如果增加的数据, 大于容量的话, 那么首先扩展parcel的缓存空间, 扩容成功继续写入;  

扩容算法 = (原数据的大小 + 新数据的大小) *3 /2 ;    

### Parcel机制的实现逻辑  
Parcel.cpp大概的实现逻辑就是在初始化的时候, 开辟了一块内存空间, 用来存储 parcel 数据;  
而且读写时是4字节对齐的;  
无论存储的是基本数据类型 或 引用数据类型的变量, 都是以32bit基本单位作为偏移量;    
```
#define PAD_SIZE(s) (((s)+3)&~3)
考虑16进制, ～3 就是 对 00000011 取反码, 就是 11111100;  
做& 运算, 就是 前面高位不变, 把最后2位变成0, 也就是4的倍数;  
+3的目的是, 把当前的值向上取最近的整除4的值, 如果本身就是那就不变;  
所以就和宏的目的就是按4字节对齐; 
常见的是int32 和int64等;  
```
读取的时候, 根据目标数据的偏移量的位置 curr_position, 以及偏移量的大小 size, 这样就可以读取之前存入的数据;  

parcel 每添加一个基本数据, 都会记录其地址偏移量, 也就是占内存大小;  
parcel 每序列化一个对象, 都会根据 position 控制其位置;  
也就是说, 写入操作, 是一段一段的写入到这块内存中, 整个过程是连续 而且 是按顺序的;  
所以在我们反序列化的时候也是, 根据 position 和字段偏移量, 一段一段取出内存中数据, 在通过Parcel的readXXX()方法还原成对象中的数据;  
这两个过程writeXXX()和readXXX()的顺序必须是一样的, 这样才能保证序列化和反序列化的成功;  
写入函数, 最终调用 writeAligned, 意思是对其写入;  
PAD_SIZE() 函数 ;  

parcel 的本质还是在 native 层实现的, 所以作为一个壳子, java 的 Parcel 会使用一个 long 型变量保存底层的 native 对象的 pointer;  
Parcel 在内部维护了一个对象池, 当 parcel pool 中没有 可用的 parcel 对象, 才会 new 出来新的对象;  
使用对象池的目的, 是为了防止大批量生产, 和回收对象触发gc, 造成内存抖动的现象;  
parcel pool 的初始容量是 POOL_SIZE = 6;   
Parcel 在 native 中, 对象结构:  
```
uint8_t*            mData;
size_tmDataSize;  //  size应该是最大使用到的内存的位置
size_tmDataCapacity;  //  一共分配的内存的边界  
mutablesize_tmDataPos;  //  pos 应该是指当前读写的位置, 可以任意挪动  
```


### 参考
google  Parcel::writeAligned    
https://segmentfault.com/a/1190000012522154  
http://androidxref.com/5.0.0_r2/xref/frameworks/base/core/jni/android_os_Parcel.cpp  
https://juejin.im/post/5c358751f265da611c27294e  
https://zhuanlan.zhihu.com/p/23685198  
https://hk.saowen.com/a/58785654f710fa448191eb12398a94ec8a5beed443f8585e9289f81b9ef2779c  
https://www.cnblogs.com/deman/p/4752222.html  

