### View  保存状态  
```
public class StateView extends android.support.v7.widget.AppCompatTextView {

    private String message;
    private static final String KS_MESSAGE = "message";
    private static final String KS_SUPER = "super_data";

    public StateView(Context context) {
        super(context);
    }

    public StateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        setText(message);
    }

    /**
     * 例如 点击 home键，会被调用；
     * 例如 内存不足，被杀死，会被调用；
     */
    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        LogTrack.w("我在 保存 现场");
        bundle.putString(KS_MESSAGE, message);
        bundle.putParcelable(KS_SUPER, super.onSaveInstanceState());
        return bundle;
    }

    /**
     * 只有在 内存不足，所在页面重启，会被调用；
     */
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        Parcelable superData = bundle.getParcelable(KS_SUPER);
        LogTrack.w("我在 恢复 现场 ");
        message = bundle.getString(KS_MESSAGE, "");
        setText(message);
        super.onRestoreInstanceState(superData);
    }
}

```

### 参考  
https://blog.csdn.net/artzok/article/details/50172657  
