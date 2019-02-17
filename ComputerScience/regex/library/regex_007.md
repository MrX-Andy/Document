###### 非0开头的纯数字（大于等于1个字符）
```
/**
 * 非0开头的纯数字（大于等于1个字符）
 * */
public static boolean regex(Object text) {
    if (isEmpty(text)) {
        return false;
    }
    String cardNum = text.toString();
    String regex = "^[1-9][0-9]*$";
    Matcher m = Pattern.compile(regex).matcher(cardNum);
    return m.matches();
}
```