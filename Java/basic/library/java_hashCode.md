### HashCode的问题

int 的 hashCode  

```
LogTrack.e("29 = "+Integer.valueOf(29).hashCode());
输出结果就是 29
```
### String#hashCode

hashCode 的源码  
```
public int hashCode() {
    int h = hash;
    if (h == 0 && value.length > 0) {
        char val[] = value;

        for (int i = 0; i < value.length; i++) {
            h = 31 * h + val[i];
        }
        hash = h;
    }
    return h;
}
```

那么hash的数值是多少  
```
/** Cache the hash code for the string */
private int hash; // Default to 0
```

所以 "a" 的hashCode就是 97  
所以"aa" 的hashCode就是 97+31*97 = 3104  


参考  
https://www.cnblogs.com/skywang12345/p/3324958.html  

```
LogTrack.e("a = "+"a".hashCode());
输出结果就是 97
LogTrack.e("aa = "+"aa".hashCode());
输出结果就是 3104
```

### boolean#hashCode
```
@Override
public int hashCode() {
    return Boolean.hashCode(value);
}

public static int hashCode(boolean value) {
    return value ? 1231 : 1237;
}
```
事实上就是 "true"  和 "false"， 的hashCode值  
```
LogTrack.e("true = "+Boolean.TRUE.hashCode());
输出结果就是 1231

LogTrack.e("false = "+Boolean.FALSE.hashCode());
输出结果就是 1237

LogTrack.e("true = "+Boolean.valueOf("true").hashCode());
输出结果就是 1231

LogTrack.e("false = "+Boolean.valueOf("false").hashCode());
输出结果就是 1237
```

### apache.HashCodeBuilder自定义 Entity的 hashCode

利用  org.apache.commons.lang3.builder.HashCodeBuilder() 插件 生成 hashCode  
```
public class UserEntity {
    private int age;
    private String name;
    private Integer height;

    @Override
    public int hashCode() {
        return new org.apache.commons.lang3.builder.HashCodeBuilder()
                .append(age)
                .append(name)
                .append(height)
                .toHashCode();
    }
}
```

HashCodeBuilder  
```
public class HashCodeBuilder implements Builder<Integer> {
    public HashCodeBuilder() {
        iConstant = 37;
        iTotal = 17;
    }
    
    public HashCodeBuilder append(final int value) {
        iTotal = iTotal * iConstant + value;
        return this;
    }
    
    public HashCodeBuilder append(final Object object) {
        if (object == null) {
            iTotal = iTotal * iConstant;

        } else {
            if (object.getClass().isArray()) {
                // factor out array case in order to keep method small enough
                // to be inlined
                appendArray(object);
            } else {
                iTotal = iTotal * iConstant + object.hashCode();
            }
        }
        return this;
    }
    
    public int toHashCode() {
        return iTotal;
    }
    
    
}    
```

### apache 的 EqualsBuilder    
```
@Override
public boolean equals(Object obj) {
    if (obj == null) {
        return false;
    }
    if (obj == this) {/*比较地址， 如果地址一样了， 对象一定是 相等了*/
        return true;
    }
    if (obj.getClass() != getClass()) {
        return false;
    }
    UserEntity rhs = (UserEntity) obj;
    return new org.apache.commons.lang3.builder.EqualsBuilder()
            .append(this.age, rhs.age)
            .append(this.name, rhs.name)
            .append(this.height, rhs.height)
            .isEquals();
}
```

### IDE 生成的 hashCode  
事实上 Float.floatToIntBits = Float.hashCode  
参考Effective Java  
```
@Override
public int hashCode() {
    int result = 17;
    result = 31 * result + age;
    result = 31 * result + name.hashCode();
    result = 31 * result + (balance != +0.0f ? Float.floatToIntBits(balance) : 0);
    result = 31 * result + (age != 0 ? Integer.hashCode(age) : 0);
    return result;
}
```
假设字段是 tmpKey      
int 类型:  tmpKey  
boolean 类型:  tmpKey? 1 : 0  
byte ,  char ,  short 类型:  (int) tmpKey  
long 类型:  (int) (tmpKey ^ tmp >>>32)  
float 类型:  Float.floatToInBits(tmpKey)  
double 类型:  Double.doubleToLongBits(tmpKey)  
如果 引用类型:  tmpKey==null ? 0 : tmpKey.HashCode()   


