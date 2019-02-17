### gson解析不抛异常  
#### 先看一组数据类    
```
data class UserEntity(
        var name: String = "",
        val hobbitList: List<String> = listOf(),
        val enLevel: String = "",
        val address: AddressEntity = AddressEntity(),
        val skillEntity: SkillEntity = SkillEntity()
)

data class AddressEntity(
        val province: String = "",
        val city: String = "",
        val county: String = ""
)

data class SkillEntity(
        val name: String = "",
        val extra: SkillExtraEntity = SkillExtraEntity()
)

data class SkillExtraEntity(
        val desc: String ? = null,
        val period: Int = 0,
        val attrList: List<String>? = null
)
```
#### 再看一组json  
```
val jsonArray = listOf<String>(
        """
    {
        "name":"Alex",
        "address":{
            "province":"安徽1",
            "city":"合肥",
            "county":"蜀山区"
            },
        "hobbitList":["AA","BB"],
        "enLevel":"CET-4",
    }
""".trimIndent(),  /*第一个 是完整的 数据*/
        """
    {
        "hobbitList":"AA",
        "name":"Alex",
        "address":{
            "province":"安徽2",
            "city":"合肥",
            "county":"蜀山区"
            },
        "enLevel":"CET-4",
    }
""".trimIndent(),  /*应该是 jsonArray  结果却是 ""  */
        """
    {
        "hobbitList":{},
        "name":"Alex",
        "address":{
            "province":"安徽3",
            "city":"合肥",
            "county":"蜀山区"
            },
        "enLevel":"CET-4",
    }
""".trimIndent(),   /*应该是 jsonArray  结果却是 {}  */
        """
    {
        "name":[],
        "hobbitList":["AA"],
        "address":{
            "province":"安徽4",
            "city":"合肥",
            "county":"蜀山区"
            },
        "enLevel":"CET-4",
    }
""".trimIndent(),  /*应该是 ""  结果却是 []  */
        """
    {
        "name":{},
        "hobbitList":["BB"],
        "address":{},
        "enLevel":"CET-4",
    }
""".trimIndent(),  /*应该是 ""  结果却是 {}  */
        """
    {
        "address":[],
        "name":{},
        "hobbitList":["CC"],
        "enLevel":"CET-4",
    }
""".trimIndent(),  /*应该是 {}  结果却是 []  */
        """
    {
        "address":"AA",
        "name":{},
        "hobbitList":["DD"],
        "enLevel":"CET-4",
    }
""".trimIndent()  /*应该是 {}  结果却是 ""  */,
        """
    {
        "address":"AA",
        "name":{},
        "hobbitList":"",
        "enLevel":[],
    }
""".trimIndent()  /*类型全都 不匹配 ""  */,
        """
    {
        "name":null,
        "address":null,
        "hobbitList":null,
        "enLevel":null
    }
""".trimIndent()  /*没有 null  */,
        """
    {
        "name":"Alex",
        "address":null,
        "hobbitList":null,
        "enLevel":null,
        "skillEntity":""
    }
""".trimIndent()
)
```
#### 开始解析    
```
public class Test {
    public static void main(String[] args) {
        List<String> stringList = JsonKt.getJsonArray();
        List<UserEntity> entityList = new ArrayList<>();
        for (int i = 0; i < stringList.size(); i++) {
            UserEntity userEntity = new GsonBuilder()
                    .create()
                    .fromJson(stringList.get(i), UserEntity.class);
            entityList.add(userEntity);
        }
        entityList.forEach(it ->
                LogTrack.w("结果  = " + it));
    }
}
```
#### 运行结果  
```
LogTrack  22:25:47:0642  WARN  [ (Test.java:26) #lambda$main$0] 结果  = UserEntity(name=Alex, hobbitList=[AA, BB], enLevel=CET-4, address=AddressEntity(province=安徽1, city=合肥, county=蜀山区), skillEntity=SkillEntity(name=, extra=SkillExtraEntity(desc=null, period=0, attrList=null)))
LogTrack  22:25:47:0643  WARN  [ (Test.java:26) #lambda$main$0] 结果  = UserEntity(name=Alex, hobbitList=[], enLevel=CET-4, address=AddressEntity(province=安徽2, city=合肥, county=蜀山区), skillEntity=SkillEntity(name=, extra=SkillExtraEntity(desc=null, period=0, attrList=null)))
LogTrack  22:25:47:0643  WARN  [ (Test.java:26) #lambda$main$0] 结果  = UserEntity(name=Alex, hobbitList=[], enLevel=CET-4, address=AddressEntity(province=安徽3, city=合肥, county=蜀山区), skillEntity=SkillEntity(name=, extra=SkillExtraEntity(desc=null, period=0, attrList=null)))
LogTrack  22:25:47:0643  WARN  [ (Test.java:26) #lambda$main$0] 结果  = UserEntity(name=, hobbitList=[AA], enLevel=CET-4, address=AddressEntity(province=安徽4, city=合肥, county=蜀山区), skillEntity=SkillEntity(name=, extra=SkillExtraEntity(desc=null, period=0, attrList=null)))
LogTrack  22:25:47:0644  WARN  [ (Test.java:26) #lambda$main$0] 结果  = UserEntity(name=, hobbitList=[BB], enLevel=CET-4, address=AddressEntity(province=, city=, county=), skillEntity=SkillEntity(name=, extra=SkillExtraEntity(desc=null, period=0, attrList=null)))
LogTrack  22:25:47:0644  WARN  [ (Test.java:26) #lambda$main$0] 结果  = UserEntity(name=, hobbitList=[CC], enLevel=CET-4, address=AddressEntity(province=, city=, county=), skillEntity=SkillEntity(name=, extra=SkillExtraEntity(desc=null, period=0, attrList=null)))
LogTrack  22:25:47:0644  WARN  [ (Test.java:26) #lambda$main$0] 结果  = UserEntity(name=, hobbitList=[DD], enLevel=CET-4, address=AddressEntity(province=, city=, county=), skillEntity=SkillEntity(name=, extra=SkillExtraEntity(desc=null, period=0, attrList=null)))
LogTrack  22:25:47:0644  WARN  [ (Test.java:26) #lambda$main$0] 结果  = UserEntity(name=, hobbitList=[], enLevel=, address=AddressEntity(province=, city=, county=), skillEntity=SkillEntity(name=, extra=SkillExtraEntity(desc=null, period=0, attrList=null)))
LogTrack  22:25:47:0645  WARN  [ (Test.java:26) #lambda$main$0] 结果  = UserEntity(name=, hobbitList=[], enLevel=, address=AddressEntity(province=, city=, county=), skillEntity=SkillEntity(name=, extra=SkillExtraEntity(desc=null, period=0, attrList=null)))
LogTrack  22:25:47:0645  WARN  [ (Test.java:26) #lambda$main$0] 结果  = UserEntity(name=Alex, hobbitList=[], enLevel=, address=AddressEntity(province=, city=, county=), skillEntity=SkillEntity(name=, extra=SkillExtraEntity(desc=null, period=0, attrList=null)))
```
  
[我只是修改了 ReflectiveTypeAdapterFactory 的源码](https://github.com/Alex-Cin/belle/tree/master/src/main/java/com/google/gson)  
