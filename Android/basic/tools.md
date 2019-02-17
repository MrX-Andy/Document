### tools 命名空间    
```
xmlns:tools="http://schemas.android.com/tools"  


// textView  button  editText  
tools:text="这些在预览时展示，并会在预览时优先于 android:text 展示"

//  recyclerView 在xml中 预览，10条数据  
tools:itemCount="10"


// fragment  布局预览  
tools:layout="@layout/module_main_item"


// listView 布局预览  
tools:listitem 、 tools:listheader 、 tools:listfooter  

//  recyclerView 布局预览  
//  LinearLayoutManager    GridLayoutManager    StaggeredGridLayoutManager
//  horizontal  vertical  
tools:itemCount="10"
tools:layoutManager="GridLayoutManager"
tools:listitem="@layout/module_main_item"
tools:orientation="vertical"
tools:spanCount="2"

// view  布局预览 可见性
tools:visibility="visible"  

//  与 <include layout="@layout/testlayout2"/>  结合  
tools:showIn="@layout/module_main"

//  在预览界面中将 DrawerLayout 打开。取值说明	end、left、right、start。
tools:openDrawer="right"

// preview 生效  
merge  tools:parentTag  
<merge xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    tools:ignore="HardcodedText"
    tools:parentTag="android.widget.RelativeLayout"
/>  
``` 

