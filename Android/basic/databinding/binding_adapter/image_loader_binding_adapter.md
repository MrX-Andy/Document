###### 关联ImageView

> ImageLoaderBindingAdapter
```
public class ImageLoaderBindingAdapter {
    /**
     * 加载图片
     * 无需手动调用此方法
     *
     * @param view ImageView
     * @param url  图片地址
     */
    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView view, String url) {
        Glide.with(view.getContext()).load(url).into(view);
    }

}
```

> module_account_message_item
```
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools"
        xmlns:app="http://schemas.android.com/apk/res-auto">

    <data>

        <variable
            name="accountMessageBean"
            type="com.alex.andfun.account.entity.AccountMessageBean"/>
    </data>

    <RelativeLayout
        android:layout_width="match_parent"
        android:layout_height="80dp"
        tools:ignore="ContentDescription,HardcodedText,RtlHardcoded">

        <ImageView
            android:id="@+id/imageView"
            android:layout_width="60dp"
            android:layout_height="60dp"
            android:layout_centerVertical="true"
            android:layout_marginLeft="16dp"
            android:background="#EEEEEE"
            app:imageUrl="@{accountMessageBean.thumb}"
            />
 

    </RelativeLayout>
</layout>
```

