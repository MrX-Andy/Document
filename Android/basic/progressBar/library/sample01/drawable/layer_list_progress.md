```
<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android" >

    <!-- 进度 -->
    <item android:id="@android:id/progress">
        <clip>
            <shape>

                <!-- 圆角化 -->
                <corners android:radius="0dip" />

                <gradient
                    android:angle="270"
                    android:centerY="0.25"
                    android:endColor="#1E88E5"
                    android:startColor="#1E88E5" />
            </shape>
        </clip>
    </item>

</layer-list>
```