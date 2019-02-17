### 对LayoutInflater的理解  

####  inflate   
public View inflate(@LayoutRes int resource, @Nullable ViewGroup root, boolean attachToRoot) ;  

#### 假设我翻译的是对的 
根据xml的资源id，创建一个有层级结构view；  
如果运行时发生error，会抛出 InflateException；  
resource 表示xml布局的资源id；  

root与attachToRoot    
如果attachToRoot是true， root就是 “具有层级结构的view” 的parent节点；  
如果attachToRoot是false， root仅仅会提供LayoutParams给这个被创建出来的view；  

returnValue  
如果 attachToRoot 是true，返回值就是root；  
如果 attachToRoot 是false，返回值就是这个被创建出来的view；  

#### 验证一下  
● root为空 attachToRoot=false  
例如resource 的xml布局，用到  
android:layout_width="match_parent"  
android:layout_height="match_parent"  
事实上，这两个属性依赖于 parent节点的 generateLayoutParams；  
此时把创建的view添加到parent节点上，得到的是  
android:layout_height="wrap_content"  
因为，root并没有提供LayoutParams给被创建出来的view使用；  

