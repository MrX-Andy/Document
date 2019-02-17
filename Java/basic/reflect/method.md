#### Method  

◑ getDeclaringClass  
返回，该方法所属的类，的全类名；  

◑ getAnnotations  
拿到方法体上所声明的注解列表；  形如：  
@POST("updateVersion/query")  
@FormUrlEncoded  
Observable<WrapperBean<Update>> checkUpdate(@FieldMap Map<String, String> params);  
得到的就是 @POST  和 @FormUrlEncoded 两个；  


