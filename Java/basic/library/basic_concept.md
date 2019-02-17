### Java的基本知识
❀ object中定义了哪些方法？
clone(), equals(), hashCode(), toString(), notify(), notifyAll(), wait(), finalize(), getClass()  
❀ 内存泄漏
长生命周期的对象持有短生命周期对象的引用,  使得被持有的短生命周期的对象, 得不到释放, 就很可能发生内存泄露;  

### 数据类型
基本数据类型 -  整型  
bit  1位, 0 1  
boolean 1位,   0 1 
byte  8位, -128 ~ 127  
short  16位, -32768 ~ 32767  
int   32位, -2 147 483 648 ~ 2 147 483 647  
char  16位,  0 ~ 65535  
long  64位, -9 223 372 036 854 775 808 ~ 9 223 372 036 854 775 807  
基本数据类型 -  浮点型  
float  32位,   
double  64位, 

操作码    
returnAddress  
指向一条虚拟机指令的操作码;    

引用数据类型  
类 类型  class type  
数组类型  array type  
接口类型  interface type  
引用数据类型, 有一个特殊值 null; 当一个引用, 不指向任何对象的时候, TA的值就用null表示;  

### boolean  
虽然定义了boolean这种数据类型, 但是只对它提供了非常有限的支持。在Java虚拟机中没有任何供boolean值专用的字节码指令,   
Java语言表达式所操作的boolean值, 在编译之后都使用Java虚拟机中的int数据类型来代替,   
而boolean数组将会被编码成Java虚拟机的byte数组,   每个元素boolean元素占8位,   
这样我们知道 boolean 类型单独使用是4个字节, 在数组中又是1个字节。  
那虚拟机为什么要用int来代替boolean呢？为什么不用byte或short, 这样不是更节省内存空间吗？  
实际上, 使用int的原因是, 对于当下32位的CPU来说, 一次进行32位的数据交换更加高效。  
《Java虚拟机规范》给出了“单独时使用4个字节, boolean数组时1个字节”的定义, 具体还要看虚拟机实现是否按照规范来,   

### final  
被final修饰的类不可以被继承;   
被final修饰的方法不可以被重写;   
被final修饰的变量不可以被改变;    
被final修饰的方法, JVM会尝试将其内联, 以提高运行效率;    
被final修饰的常量, 在编译阶段会存入常量池中;    

### 对 finalize方法的认识   
请不要手动调用 finalize 方法, 并不能达到回收资源效果, 反而会影响性能;  

finalize 方法的执行过程  
当对象不可达时, GC会判断该对象是否重写了finalize()方法, 如没有重写则直接将其回收,     
否则, 若对象未执行过finalize()方法, 将其放入F-Queue队列, 由一低优先级线程执行该队列中对象的finalize()方法。    
执行finalize()方法完后, GC会再次判断该对象是否可达, 若不可达则进行回收。否则对象“复活”。  

finalize 方法的缺点  
1.. 不能保证finalize()方法会被执行;  
2.. 不能保证finalize()方法会被及时的执行;  
3.. 不能保证finalizer方法并被正确的执行, 不同的JVM实现中finalizer的效果是不同的。  
4.. 手动调用finalizer() 方法, 耗时较长, 比较影响性能;  

### 对象引用 
强引用  
通常我们通过new来创建一个新对象时返回的引用就是一个强引用, 若一个对象通过一系列强引用可到达, 它就是强可达的, 那么它就不被回收  

软引用  
若一个对象只能通过软引用到达, 那么这个对象在内存不足时会被回收,   

弱引用  
若一个对象只能通过弱引用到达, 那么它就会被回收（即使内存充足）,    
弱引用对象的存在不会阻止它所指向的对象被垃圾回收器回收。  
假设垃圾收集器在某个时间点决定一个对象是弱可达的, 也就是说当前指向它的全都是弱引用,   
这时垃圾收集器会清除所有指向该对象的弱引用, 然后把这个弱可达对象标记为可终结(finalizable)的, 这样它随后就会被回收。  
与此同时或稍后, 垃圾收集器会把那些刚清除的弱引用放入创建弱引用对象时所指定的引用队列(Reference Queue)中。  

虚引用  
虚引用是Java中最“弱”的引用, 通过它甚至无法获取被引用的对象, 它存在的唯一作用就是当它指向的对象回收时, 
它本身会被加入到引用队列中, 这样我们可以知道它指向的对象何时被销毁。

### 匿名内部类, 引用外部变量需要 加上 final 修饰  
```
class Main$1 implements FunLisenter {
    Main$1(Main var1, String var2) {
        this.this$0 = var1;
        this.val$nameInner = var2;
    }

    public void fun() {
        System.out.println(this.val$nameInner);
    }
}
```
反编译之后, 发现分明是两个对象, 不是一个; 如果 外部变量不加上 final , 就需要 内部类 和 外部类, 做变量的同步,   
结构体不需要同步, 但是基础数据类型一定需要;好像解决不了, 所以干脆 加上 final 就不需要同步了;    


## Java代码规范

对于集合框架, StringBuild等可变数据类型, 最好给初始容量  
向HashMap 或者 StringBuild 在扩容的时候是比较浪费时间的, 所以在使用的时候, 尽量能预估计其容量;  

多多使用 final  
如果一个 属性, 变量, 不会被二次实例化, 最好用final 修饰;  
如果一个函数, 较少次被引用, 最好用 final 修饰, 在java编译期, 会被inline 过去,     
不会出现方法调用的 入栈 和 出栈操作, 等于说是拿空间换时间;    

尽量用 int 或者 注解 来代替枚举, 尤其是来代替boolean   
首先,  枚举比较占内存;然后 boolean在JVM本身中本身就是用 1 和 非1  来代替的;  
其次 boolean 之后 两种状态,  很多业务需求, 最早只有2种状态, 后续就变成 多种状态;  

最好不要在 for 循环中 创建引用  
尽量用第二种编码方式,  减少对象引用的存在;
第一种编码方式  
```
List<UserBen> list = new ArrayList<UserBean>(10);
for 0.. 10{
    UserBean bean = new UserBean();
    bean.set();
    ...
    list.add(bean)
}
```
第二种编码方式  
```
List<UserBen> list = new ArrayList<UserBean>(10);
UserBean bean = null;
for 0.. 10{
    bean = new UserBean();
    bean.set();
    ...
    list.add(bean)
}
```

多使用方法调用,  减少直接修改全局变量  
首先 方法的入栈 出栈 是基于 栈内存的,  是线程私有的;  
假设出现数据问题, 例如 现在有个 activityCode , 被 50次 修改了, 但是并不知道在哪里修改的,    
如果每次修改 activityCode 都是 通过方法调用, 而不是直接对 全局变量进行操作   
这个时候,  在 updateActivityCode 打印方法调用栈, 就能排查 是谁修改了 activityCode; 

方法的参数, 个数最好不要超过4个  
如果参数过多,  最好要有对应的 paramsEntity;  
例如前端后端交互的时候,  最好使用 reqEntity  和 respEntity 来代替 getFoo(String name, String pwd, String label, String category ... )

关于GC  
对于不用的 变量 userEntity  最好不要 使用 userEntity = null; 来假装优化,  但是千万不要手动调用 System.gc();    
置null的做法对GC的帮助微乎其微, 有时候反而会导致代码混乱, 手动gc还会影响性能;

### 参考   
http://ifeve.com/gc-oriented-java-programming/  
