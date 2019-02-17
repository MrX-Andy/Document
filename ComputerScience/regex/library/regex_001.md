###### 必须包含一个数字

```
必须包含一个数字
/**
 * 必须包含 数字
 *
 * @param text
 * @return
 */
public static boolean containsNum(Object text) {
    if (isEmpty(text)) {
        return false;
    }
    String cardNum = text.toString();
    String regex = "(.*?)\\d+(.*?)";
    Matcher m = Pattern.compile(regex).matcher(cardNum);
    return m.matches();
}
```