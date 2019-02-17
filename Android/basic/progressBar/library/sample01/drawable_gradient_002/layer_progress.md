```
<layer-list xmlns:android="http://schemas.android.com/apk/res/android" >

    <!-- 按照拜访顺序， ①先放背景色，②再放第二条，③最后放第一条。展示顺序相反，先看到第一条a -->
    <item android:id="@android:id/background">

        <shape>
            <corners android:radius="5dip" />

            <gradient
                android:angle="0"
                android:centerX="0.5"
                android:centerY="0.5"
                android:endColor="#BDBDBD"
                android:startColor="#E3E3E3" />
        </shape>
    </item>
    <item android:id="@android:id/secondaryProgress">
        <clip>
            <shape>
                <corners android:radius="5dip" />

                <gradient
                    android:angle="0"
                    android:centerX="0.5"
                    android:centerY="0.5"
                    android:endColor="#4Db6AC"
                    android:startColor="#B2DFDB" />
            </shape>
        </clip>
    </item>
    <item android:id="@android:id/progress">
        <clip>
            <shape>
                <corners android:radius="5dip" />

                <gradient
                    android:angle="0"
                    android:centerX="0.5"
                    android:centerY="0.5"
                    android:endColor="#FF5722"
                    android:startColor="#F8BBD0" />
            </shape>
        </clip>
    </item>

</layer-list>
```