### 红黑树  

◆ 应用举例  
java的HashMap、TreeMap、TreeSet，Linux 的虚拟内存管理；   
◆ 基本属性  
每个节点，都包含5个属性：color、key、left、right和parent；  
如果一个节点没有子节点，或者没有父节点，则其对应属性值为NULL；  
我们可以把这些NULL视为指向二叉搜索树的叶子节点指针、外部指针；  
把带关键字的节点视为树的内部节点；  
一般红黑树必须是满足以下属性的 二叉搜索树：  
● 根节点 是黑色；  
● 每个节点 是红色、或者是黑色；  
● 每个NULL节点 是黑色；  
● 如果一个节点是红色的，那么他的左右子节点都是黑色的；  
● 对于每个节点，从该节点到 其所有子节点的简单路径上， 均包含相同数量的黑色节点；  

◆ 关于哨兵  
为了便于处理红黑树的代码边界条件，使用哨兵来代表NULL；  
对于一个红黑树T，哨兵T.nil 是一个与树中的普通节点属性相同的对象；  
它的属性为BLACK，而其他属性 parent、left、right和key都可以设置为任意数值；  
  
使用哨兵后，可以将节点N的NULL孩子视为一个普通的节点，T.nil 的父节点就是N；  

◆ 黑高  
从某个节点N出发，达到一个叶子节点，经过任意一个简单路径，其黑色节点的个数称之为黑高，记为bh；  
红黑树必须满足，任意一个节点，到其任意一个叶子节点，经过任意的简单路径，其黑高相等；  

◆ [红黑树的插入，基本步骤](RBT_insert.md)  
◆ [红黑树的插入，简单示例](RBT_insert_sample.md)  
◆ [红黑树的删除，基本步骤](RBT_delete.md)   
◆ [红黑树的删除，简单示例 1](../image_files/RBT_delete_1.png)   
◆ [红黑树的删除，简单示例 2](../image_files/RBT_delete_2.png)   
◆ 参考  
https://www.cnblogs.com/skywang12345/p/3245399.html  
http://blog.chinaunix.net/uid-26548237-id-3480169.html  
http://blog.csdn.net/u010367506/article/details/23962671  
http://gengning938.blog.163.com/blog/static/1282253812011420103852696/  
◆ 在线演示红黑树  
http://sandbox.runjs.cn/show/2nngvn8w  


