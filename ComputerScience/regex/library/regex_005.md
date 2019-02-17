###### 是正整数（单个字符）
```
/**
 * 是正整数（单个字符）
 * */
public static boolean regex(Object text) {
    if (isEmpty(text)) {
        return false;
    }
    String cardNum = text.toString();
    String regex = "^[1-9]d*$";
    Matcher m = Pattern.compile(regex).matcher(cardNum);
    return m.matches();
}
```