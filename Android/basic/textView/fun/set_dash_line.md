### 设置删除线  

```
/**
 * 设置删除线
 */
public static <V extends TextView> void throughLine(V view) {
    if (view == null) {
        return;
    }
    view.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG | Paint.STRIKE_THRU_TEXT_FLAG);
}

/**
 * 设置下划线
 */
public static <V extends TextView> void underLine(V view) {
    if (view == null) {
        return;
    }
    view.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG | Paint.UNDERLINE_TEXT_FLAG);
}


/**
 * 文本加粗
 */
public static <V extends TextView> void fakeBold(V view) {
    if (view == null) {
        return;
    }
    view.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
}

```