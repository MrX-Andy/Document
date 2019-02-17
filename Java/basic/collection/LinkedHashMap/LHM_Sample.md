###### LinkedHashMap 的简单示例

```
linkedHashMap.put("1", "A");
linkedHashMap.put("2", "B");
linkedHashMap.put("3", "C");
linkedHashMap.put("4", "D");
linkedHashMap.put("5", "E");

linkedHashMap.get("3");
linkedHashMap.get("1");
linkedHashMap.get("5");
linkedHashMap.get("2");
linkedHashMap.get("4");

linkedHashMap.forEach((key, value) -> {
    LogTrack.e(key + "=" + value);
});
```
> accessOrder
- true 按访问顺序排序（LRU）；  
LinkedHashMap<String, String> linkedHashMap =   new LinkedHashMap<>(16, 0.75F, true);  
输出结果:    
[ (LinkedHashMap.java:684) #forEach] 3=C  
[ (LinkedHashMap.java:684) #forEach] 1=A  
[ (LinkedHashMap.java:684) #forEach] 5=E  
[ (LinkedHashMap.java:684) #forEach] 2=B  
[ (LinkedHashMap.java:684) #forEach] 4=D  
- false 按插入顺序排序；  
LinkedHashMap<String, String> linkedHashMap =   new LinkedHashMap<>(16, 0.75F, false);  
输出结果:      
[ (LinkedHashMap.java:684) #forEach] 1=A  
[ (LinkedHashMap.java:684) #forEach] 2=B  
[ (LinkedHashMap.java:684) #forEach] 3=C  
[ (LinkedHashMap.java:684) #forEach] 4=D  
[ (LinkedHashMap.java:684) #forEach] 5=E  
