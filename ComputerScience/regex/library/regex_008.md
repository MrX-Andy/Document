######  是手机号码（第0位是1，第1位是：3456789  剩下9位是0-9），长度必须是11位

```
/**
 * 是手机号码（第0位是1，第1位是：3456789  剩下9位是0-9），长度必须是11位
 * */
public static boolean regex(Object text) {
    if (isEmpty(text)) {
        return false;
    }
    String cardNum = text.toString();
    String regex = "^[1][3456789][0-9]{9}";
    Matcher m = Pattern.compile(regex).matcher(cardNum);
    return m.matches();
}
```