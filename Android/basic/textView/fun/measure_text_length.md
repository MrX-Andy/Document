### 测量文本宽度-长度  
在5.5英寸 1080P的手机上，  
中文标点符号 都是48px；  
汉字都是48px；  
英文' 是8px；  
英文, 是9px；  
空格  是12px；  
英文. 是13px；  
英文a是26px；    
英文A是32px；  
```
public class EllipsizeTextView extends android.support.v7.widget.AppCompatTextView {
    private String endCharSeq = "...";
    private int maxLine = 1;
    private int realWidth = -1;
    private int maxRealWidth = -1;
    
    public EllipsizeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        maxLine = getMaxLines();
    }
    
    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (realWidth < 0) {
            realWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        }
        if (maxRealWidth < 0) {
            maxRealWidth = realWidth * maxLine;
        }
        CharSequence mCharSeq = getText();
        int length = mCharSeq.length();
        int widthCount = 0;
        int lastIndex = length;
        for (int i = 0; i < length; i++) {
            CharSequence subSeq = mCharSeq.subSequence(i, i + 1);
            widthCount += Layout.getDesiredWidth(subSeq, 0, 1, getPaint());
            LogTrack.i(subSeq + "  " + widthCount);
            if (widthCount >= maxRealWidth) {
                lastIndex = i;
                LogTrack.w(subSeq + "  " + widthCount + "  " + maxLine + "  " + realWidth);
                break;
            }
        }
        setText(mCharSeq.subSequence(0, lastIndex-1));
    }
}
```