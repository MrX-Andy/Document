###### 必须包含一个英文 和 一个数字（一共2个字符以上，任意字符）
```
/**
  * 必须包含一个英文 和 一个数字（一共2个字符以上，任意字符）
  */
public static boolean containsEn$Num(Object text) {
    if (isEmpty(text)) {
        return false;
    }
    String cardNum = text.toString();
    String regex = "^(?![^a-zA-Z]+$)(?!\\D+$).{1,}$";
    Matcher m = Pattern.compile(regex).matcher(cardNum);
    return m.matches();
}
```