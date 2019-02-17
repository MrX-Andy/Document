### 想要EditText不弹出软键盘，但是又要支持粘贴，复制剪切功能。
```
public static void setEditTextNoSoftInput(EditText editText) {

    Class editClass = editText.getClass().getSuperclass();
    Class textClass = editClass.getSuperclass();
    try {
        Field editorField = textClass.getDeclaredField("mEditor");
        editorField.setAccessible(true);
        Object editorObject = editorField.get(editText);
        Class editorClass = editorObject.getClass();
        if (!"Editor".equals(editorClass.getSimpleName())) {
            editorClass = editorClass.getSuperclass(); // 防止类似于华为使用的自身的HwEditor
        }
        Field mShowInput = editorClass.getDeclaredField("mShowSoftInputOnFocus");
        mShowInput.setAccessible(true);
        mShowInput.set(editorObject, false);
    } catch (NoSuchFieldException e) {
        e.printStackTrace();
    } catch (IllegalAccessException e) {
        e.printStackTrace();
    }
}
```