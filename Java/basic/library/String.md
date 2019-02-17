执行效率:  
StringBuilder.append > String.concat > string + string > string.format   
其中 stringBuilder.append 与 string.concat 之间的差距, 不算大;  
但是, string + string 性能差, 差距非常大;  

### StringBuilder 与 StringBuffer  
内部维护一个 char [] value;  
默认空闲容量是 16 字节;  
StringBuilder#append(string)  
```
// StringBuilder 没有 synchronized 关键字;  
//  StringBuffer 有 synchronized 关键字;  
// 剩下的完全一致;  
@Override
public synchronized StringBuffer append(String str) {
    toStringCache = null;
    super.append(str);
    return this;
}
```
AbstractStringBuilder#append(string)  
```
//  StringBuilder 与 StringBuffer 通用;  
public AbstractStringBuilder append(String str) {
    if (str == null)
        return appendNull();
    int len = str.length();
    //  先对 value 扩容;   
    //  默认 value.length *=2;  
    //  如果不够用, value.length += str.length;  
    ensureCapacityInternal(count + len);
    // 再把目标数据, 放在 value 中;  
    str.getChars(0, len, value, count);
    count += len;
    return this;
}
```
### + 为什么会慢  
```
long t1 = System.currentTimeMillis();
String str = "hollis";
for (int i = 0; i < 50000; i++) {
    String s = String.valueOf(i);
    str += s;
}
long t2 = System.currentTimeMillis();
System.out.println("+ cost:" + (t2 - t1));
```
用 javap 命令反编译之后  
```
long t1 = System.currentTimeMillis();
String str = "hollis";
for(int i = 0; i < 50000; i++)
{
    String s = String.valueOf(i);
    str = (new StringBuilder()).append(str).append(s).toString();
}

long t2 = System.currentTimeMillis();
System.out.println((new StringBuilder()).append("+ cost:").append(t2 - t1).toString());
```
底层实现也是用的 StringBuilder, 为什么还会慢呢?  
在 for 循环中, 每次都是 new 了一个 StringBuilder, 频繁的创建对象, 不仅仅会耗费时间, 还会造成内存资源的浪费, 可能会引起 GC;  


### ==比较  
```
Q：下列程序的输出结果： 
String s1 = “abc”; 
String s2 = “abc”; 
System.out.println(s1 == s2); 
A：true，均指向常量池中对象。


Q：下列程序的输出结果： 
String s1 = new String(“abc”); 
String s2 = new String(“abc”); 
System.out.println(s1 == s2); 
A：false，两个引用指向堆中的不同对象。


Q：下列程序的输出结果： 
String s1 = “abc”; 
String s2 = “a”; 
String s3 = “bc”; 
String s4 = s2 + s3; 
System.out.println(s1 == s4); 
A：false，因为s2+s3实际上是使用StringBuilder.append来完成，会生成不同的对象。


Q：下列程序的输出结果： 
String s1 = “abc”; 
final String s2 = “a”; 
final String s3 = “bc”; 
String s4 = s2 + s3; 
System.out.println(s1 == s4); 
A：true，因为final变量在编译后会直接替换成对应的值，所以实际上等于s4=”a”+”bc”，而这种情况下，编译器会直接合并为s4=”abc”，所以最终s1==s4。
//  补充  
s1 = "ab" + "cd";
s2 = "abcd";    
System.out.println(s1 == s2);    // true



Q：下列程序的输出结果： 
String s = new String(“abc”); 
String s1 = “abc”; 
String s2 = new String(“abc”); 
System.out.println(s == s1.intern()); 
System.out.println(s == s2.intern()); 
System.out.println(s1 == s2.intern()); 
A：false，false，true。


Q：下列程序的输出结果： 
temp = "hh".intern();
s1 = "a" + temp;
s2 = "ahh";
System.out.println(s1 == s2);    // false

temp = "hh".intern();
s1 = ("a" + temp).intern();
s2 = "ahh";
System.out.println(s1 == s2);    // true


Q：下列程序的输出结果： 
String s3 = new String("1") + new String("1");    // 此时生成了四个对象 常量池中的"1" + 2个堆中的"1" + s3指向的堆中的对象（注此时常量池不会生成"11"）
s3.intern();    // jdk1.7之后，常量池不仅仅可以存储对象，还可以存储对象的引用，会直接将s3的地址存储在常量池
String s4 = "11";    // jdk1.7之后，常量池中的地址其实就是s3的地址
System.out.println(s3 == s4); // jdk1.7之前false， jdk1.7之后true
```
### equals与 hashCode  
equals 先用 == 比较, 两个 string 对象的地址是否一致, 如果不是同一个对象, 遍历每一个 char, 比较其内容是否完全一致;   
hasCode 遍历每个 char 的hashCode;  

###  参考  
https://www.cnblogs.com/Kidezyq/p/8040338.html  
