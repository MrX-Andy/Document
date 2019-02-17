
[适配特殊json](sample/TypeAdapter.md)   
[gson解析不抛异常](sample/gson_silence.md)  

别名  
```
@SerializedName(value = "name", alternate = {"name", "NAME", "_name"})
private String name;
@SerializedName("_extra")
private String extra;
```

```
//  @Expose(serialize = false) 
//  private String lastName;  起作用  
.excludeFieldsWithoutExposeAnnotation()    

//  实体类，转 gson时，实体类为空， 强制输出 "xxEntity":null  
.serializeNulls()  

// 实体类 转 gson 时， 会有格式化  换行；
.setPrettyPrinting()    

//  转 json 的时候，会忽略掉 这个字段  
transient String description = "";  
```

操作-修改json字符串的内容  
```
@SuppressWarnings("SameParameterValue")
private static String map(Serializable jsonEntity, String key) {
    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    try {
        JsonElement jsonElement = gson.toJsonTree(jsonEntity);
        JsonElement keyElement = jsonElement.getAsJsonObject().get(key);
        Object targetEntity = gson.fromJson(keyElement.getAsString(), new TypeToken<Object>() {
        }.getType());
        JsonElement element = gson.toJsonTree(targetEntity);
        jsonElement.getAsJsonObject().add(key, element);
        return jsonElement.toString();
    } catch (Exception ex) {
        return gson.toJson(jsonEntity);
    }
}
```  



参考  
https://www.jianshu.com/p/a03bc97875b8    
https://blog.csdn.net/chunqiuwei/article/details/49535419  
https://blog.csdn.net/chunqiuwei/article/details/49338053  
https://blog.csdn.net/chunqiuwei/article/details/49401733  
https://blog.csdn.net/chunqiuwei/article/details/49229089  
https://blog.csdn.net/chunqiuwei/article/details/49204879  
https://blog.csdn.net/chunqiuwei/article/details/49202759  
https://blog.csdn.net/chunqiuwei/article/details/49160321  

