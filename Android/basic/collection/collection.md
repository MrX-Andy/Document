### Collection  
[java_collection](/Java/java_collection.md)  

### SimpleArrayMap  

SimpleArrayMap主要有两个数组，mHashes 存放key的hashCode，mArray 依次存放key和value，也就是说，成对的key、value，是存放在相邻的位置的；  
SimpleArrayMap在计算与查找索引位置，使用的是二分法查找，时间复杂度是log2N；  
当数组元素达到容量限制时，会倍增扩容，用System.arraycopy拷贝老数组元素；  
当数据量较小的情况下，事实上安卓平台下，数据量与后端相比较而言，数据量显得微不足道，使用SimpleArrayMap相对来说，是比较节约内存空间的； 
 

ArrayMap  
ArraySet  
SparseArray  
SparseBooleanArray  
SparseIntArray  
SparseLongArray  
LongSparseArray  
### SparseArray  

SparseArray主要包含两个数组，int[] mKeys和Object[] mValues；  
省去int拆装箱的过程；  

### 用法比较  
ArrayMap（android.support.v4.util.ArrayMap）  
键值对，一组key、value元素；  
key、value 都可以为空（允许多个不同的key，value为空；）；  
key相同，value会被覆盖；  
迭代器迭代，迭代时，按照key的hashCode顺序；  

SimpleArrayMap（android.support.v4.util.SimpleArrayMap）  
键值对，一组key、value元素；  
key、value 都可以为空（允许多个不同的key，value为空；）；  
key相同，value会被覆盖；  
下标索引迭代，迭代时，按照key的hashCode顺序；  

ArraySet（android.support.v4.util.ArraySet）  
不是键值对，只有一个元素；  
允许放入 null；  
放入重复元素，会被覆盖；  
迭代器迭代，迭代时，按照hashCode顺序；  

SparseArray（android.util.SparseArray）  
键值对，一组key、value元素；  
key规定必须是int类型；  
value允许放入 null；  
放入重复元素，会被覆盖；  
下标索引迭代，迭代时，按照hashCode顺序；  

SparseBooleanArray（android.util.SparseBooleanArray）   
SparseIntArray（android.util.SparseIntArray）   
SparseLongArray（android.util.SparseLongArray）   
键值对，一组key、value元素；  
key规定必须是int类型；  
value只能放入 boolean，而不是 Boolean；  
放入重复元素，会被覆盖；  
下标索引迭代，迭代时，按照hashCode顺序；  

LongSparseArray（android.support.v4.util.LongSparseArray）  
键值对，一组key、value元素；  
key规定必须是long类型；  
value允许放入 null；  
放入重复元素，会被覆盖；  
下标索引迭代，迭代时，按照hashCode顺序；  


比较  
ArrayMap、SparseArray与HashMap的比较  
安卓的集合框架，纯用数组实现；    
Java的HashMap，用数组、链表、红黑树实现；  

每次插入数据   
ArrayMap的做法是，根据key生成的hash值，把hash值放在一个哈希表里面，用二分法算出插入的位置，  如果遇到hash冲突，会位移所有的hash值，保证其顺序性，也就是线性探测法；  
HashMap的做法是，根据key生成的hash值，与哈希表的length做位运算换取其在哈希表对应的index，如果遇到index冲突，记为哈希冲突，再用拉链法解决哈希冲突，  
当某个位置的链表过长，会把当前hash值对应的链表树化，就是转换成红黑树，当HashMap存入元素过多会对哈希表扩容，在这个查询与添加的算法上，hashMap明显有剩余ArrayMap，  
在数据量较小的安卓平台下，安卓的集合框架，更节省内存，时间复杂度是log2n，性能表现不俗；  
如果是数据量较大，例如超过1K，还是用HashMap做，更加合理一些；  
同样也不是线程安全的；  

### ArrayMap 与 HashMap的put测试    
arrayMap 100 次实验， 每put 10 次，平均耗时：42 微秒  
hashMap 100 次实验， 每put 10 次，平均耗时：21 微秒  

arrayMap 100 次实验， 每put 100 次，平均耗时：153 微秒  
hashMap 100 次实验， 每put 100 次，平均耗时：95 微秒  

arrayMap 100 次实验， 每put 1000 次，平均耗时：959 微秒  
hashMap 100 次实验， 每put 1000 次，平均耗时：764 微秒  

arrayMap 100 次实验， 每put 1W 次，平均耗时：7 ms  
hashMap 100 次实验， 每put 1W 次，平均耗时：2 ms  

arrayMap 100 次实验， 每put 10W 次，平均耗时：441 ms  
hashMap 100 次实验， 每put 10W 次，平均耗时：26 ms  

arrayMap 1 次实验， 每put 100W 次，平均耗时：79578 ms  
hashMap 1 次实验， 每put 100W 次，平均耗时：861 ms  

### 参考  
http://gityuan.com/2019/01/13/arraymap/  









