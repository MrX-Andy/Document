### 参数传递  
java的参数传递，引⽤传递、值传递。 java为引⽤按值传递(copy⼀份引⽤的地址，按值传递过去)。  
1..  传基本数据类型  int  
```
int  age = 20;  
fun(age);  
print(age);

void fun(int value){
    value = 123;
}
//  输出   20  
```
2.. 传一个 string  
```
String  name = "alex";  
fun(name);  
print(name);
void fun(String text){
    text = "123";
}
//  输出   alex  
```
3... 对 Entity 的内容修改  
```
Entity entity = new Entity();
entity.name = "alex";
fun(entity);  
print(entity.name);

void fun(Entity entity){
    entity.name = "456";
}
class Entity{
    String name = "123";
}
//  输出 456  
```
4... 对 Entity 的地址修改  
```
Entity entity = new Entity();
entity.name = "alex";
fun(entity);  
print(entity.name);

void fun(Entity entity){
    entity = new Entity();
    entity.name = "456";
}
class Entity{
    String name = "123";
}
//  输出 alex    
```
5... 对 list 的地址修改  
```
List<String> list = null;  
fun(list);  
print("list = "+list)

private static void fun(List<String> list) {
    if (list == null) {
        list = new ArrayList<>();
    }
    list.add("alex");
}
//  输出 list = null  
```
