### MIME  

◆ MIME类型组成  
每种MIME类型 由2部分组成 = 类型 + 子类型  
text/css  
text/xml  
application/pdf  

◆ MIME类型形式  
// 形式1：单条记录    
vnd.android.cursor.item/自定义  
// 形式2：多条记录（集合）
vnd.android.cursor.dir/自定义   
// 1. vnd：表示父类型和子类型具有非标准的、特定的形式。  
// 2. 父类型已固定好（即不能更改），只能区别是单条还是多条记录    
// 3. 子类型可自定义  

◆ MIME实例说明  
<-- 单条记录 -->  
// 单个记录的MIME类型  
vnd.android.cursor.item/vnd.yourcompanyname.contenttype   

// 若一个Uri如下  
content://com.example.transportationprovider/trains/122     
// 则ContentProvider会通过ContentProvider.geType(url)返回以下MIME类型  
vnd.android.cursor.item/vnd.example.rail  

<-- 多条记录 -->  
// 多个记录的MIME类型  
vnd.android.cursor.dir/vnd.yourcompanyname.contenttype   
// 若一个Uri如下  
content://com.example.transportationprovider/trains   
// 则ContentProvider会通过ContentProvider.geType(url)返回以下MIME类型  
vnd.android.cursor.dir/vnd.example.rail  
"vnd.android.cursor.item/email_v2"							邮箱  
"vnd.android.cursor.item/phone_v2"						手机号  
"vnd.android.cursor.item/name"									姓名  
"vnd.android.cursor.item/postal-address_v2"		通信地址  

