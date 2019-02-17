### 监听软键盘弹出

```

<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#FFFFFF"
    android:orientation="vertical"
    android:id="@+id/ll_root"
    tools:ignore="ContentDescription,HardcodedText" >
</LinearLayout>	

	llRoot = (LinearLayout) findViewById(R.id.ll_root);
	llRoot.getViewTreeObserver().addOnGlobalLayoutListener(new MyOnGlobalLayoutListener());
	
	private final class MyOnGlobalLayoutListener implements OnGlobalLayoutListener
	{
		@Override
		public void onGlobalLayout()
		{
			int heightDiff = llRoot.getRootView().getHeight() - llRoot.getHeight();
			if (heightDiff > 100)
			{ // 如果高度差超过100像素，就很有可能是有软键盘...
				if (canScroll)
				{
					LogUtils.e("进来了！");
					ScrollView scrollView = (ScrollView) findViewById(R.id.sv);
					scrollView.scrollBy(0, 300);
					canScroll = false;
				}
			} else {
			}
		}

	}
```