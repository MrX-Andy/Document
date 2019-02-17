### 切入点  
以 execution  为例，最为简单；  
execution(修饰符 返回值  包.类.方法名(参数) throws异常)  
◑ 修饰符，一般省略，有  
public            公共方法  
\*                   任意  
◑ 返回值，不能省略，有  
void               返回没有值  
String            返回值字符串  
\*                  任意  
◑ 包，可以省略  
com.itheima.crm                  固定包  
com.itheima.crm.*.service     crm包下面子包任意（例如：com.itheima.crm.staff.service）  
com.itheima.crm..                crm包下面的所有子包（含自己）  
com.itheima.crm.*.service..   crm包下面任意子包，固定目录service，service目录任意包  
java.lang.String  匹配String类型  
java.*.String  
匹配java包下的任何“一级子包”下的String类型，如匹配java.lang.String，但不匹配java.lang.ss.String  
java..*  
匹配java包及任何子包下的任何类型,如匹配java.lang.String、java.lang.annotation.Annotation  
java.lang.*ing  
匹配任何java.lang包下的以ing结尾的类型  

java.lang.Number+  
匹配java.lang包下的任何Number的自类型，如匹配java.lang.Integer，也匹配java.math.BigInteger  

◑ 类，可以省略  
UserServiceImpl                  指定类  
\*Impl                                  以Impl结尾  
User*                                  以User开头  
\*                                        任意  
◑ 方法名，不能省略  
addUser                               固定方法  
add*                                   以add开头  
*Do                                    以Do结尾  
\*                                        任意  
◑ 参数  
()                                        无参  
(int)                                    一个整型  
(int,int)                              两个  
(..)                                      参数任意  
(..,java.lang.String)   表示匹配接受java.lang.String类型的参数结束，且其前边可以接受有任意个参数的方法；  
(java.lang.String,..)  表示匹配接受java.lang.String类型的参数开始，且其后边可以接受任意个参数的方法；  
(\*,java.lang.String) 表示匹配接受java.lang.String类型的参数结束，且其前边接受有一个任意类型参数的方法；  

◑ throws,可省略，一般不写。  

