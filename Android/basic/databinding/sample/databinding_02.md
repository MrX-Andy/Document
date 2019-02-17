### 常用知识点  

常用的转义字符的问题

```
<data>
    <import type="java.util.List"/>
    <import type="com.alex.andfun.account.model.AccountMessageBean"/>
    <variable
        name="accountMessageList"
        type="List&lt;AccountMessageBean&gt;"/>
</data>

```

### 常用的转义字符

```
显示结果       描述               转义字符       十进制
                           空格               &nbsp;	           &#160;
<	                       小于号	       &lt;                     &#60;
>	                       大于号         &gt;	               &#62;
&	                       与号	           &amp;              &#38;
"	                       引号	           &quot;	           &#34;
‘	                       撇号             &apos;	           &#39;
×	                       乘号	          &times;	           &#215;
÷	                       除号	          &divide;            &#247;
```

### 一些资源需要显示类型调用。
```
Type                                Norma                       Expression Reference
String[]	                        @array                   	   @stringArray
int[]	                            @array  	    	       	   @intArray
TypedArray                @array  	    	       	   @typedArray
Animator	                    @animator    	       	   @animator
StateListAnimator	@animator	     	       @stateListAnimator
color                              @color	      	       	       @color
ColorStateList	        @color	         	           @colorStateList
```


### 修改生成的DataBinding的类名  
```
<!--suppress AndroidUnknownAttribute -->
<data class="com.alex.andfun.account.login.contract.LoginDataBinding"
    >
    <variable
        name="loginViewModel"
        type="com.alex.andfun.account.login.contract.LoginViewModel"/>
</data>
```
### 对于有id 的view  
dataBinding.tvContent.text = "hello"  可以这样直接使用了； 都是final类型；   

