```
    <android.support.v7.widget.SwitchCompat
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="#00000000"
        android:longClickable="false"
        android:text=""
        android:textOff=""
        android:textOn=""
        android:thumb="@drawable/simple_switch_thumb_oval"
        app:switchMinWidth="20dp"
        app:switchPadding="0dp"
        app:thumbTint="#FFFFFF"
        app:track="@drawable/simple_switch_track_rect"
        android:theme="@style/simple_switch_style"
        app:trackTintMode="src_over"/>
```

### simple_switch_thumb_oval  
```
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
       android:shape="oval">
    <size
        android:width="24dp"
        android:height="24dp">
    </size>
    <solid
        android:color="#FFFFFF">
    </solid>
    <stroke android:width="2dp" android:color="#00000000"/>

</shape>
```
### simple_switch_track_rect  
```
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:state_checked="true">
        <shape android:shape="rectangle">
            <solid
                android:color="#FF06C1AE">
            </solid>
            <corners
                android:radius="800dp">
            </corners>
        </shape>
    </item>
    <item android:state_checked="false">
        <shape android:shape="rectangle">
            <solid
                android:color="#FFCCCCCC">
            </solid>
            <corners
                android:radius="800dp">
            </corners>
        </shape>
    </item>
</selector>
```

### simple_switch_style  
```
    <style name="simple_switch_style" parent="Theme.AppCompat.Light">
        <!--  开状态， 轨道的颜色-->
        <item name="colorControlActivated">#FF06C1AE</item>
        <!--关闭时的颜色-->
        <item name="colorSwitchThumbNormal">#00000000</item>
        <!--关闭时的轨迹颜色-->
        <item name="android:colorForeground">#00000000</item>
    </style>
```



