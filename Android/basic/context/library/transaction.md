### transaction  
1.. replace  加入回退栈, Fragment不销毁, 但是切换回销毁视图和重新创建视图;  
2.. replace  未加回退栈, Fragment销毁掉;  
3.. hide. show. Fragment不销毁，也不销毁视图, 隐藏和显示不走生命周期;  

replace, AFragment 加入回退栈  
在同一个位置, 第一次 replace AFragment, 第二次replace BFragment;    
```
A: onAttach -> onCreate -> onCreateView -> onActivityCreated -> onStart -> onResume;  
A: onPause -> onStop -> onDestroyView   
B: onAttach -> onCreate -> onCreateView -> onActivityCreated -> onStart -> onResume;  
```



replace, AFragment 未加回退栈  
在同一个位置, 第一次 replace AFragment, 第二次replace BFragment;    
```
A: onAttach -> onCreate -> onCreateView -> onActivityCreated -> onStart -> onResume;  
A: onPause -> onStop -> onDestroyView -> onDestroy -> onDetach  
B: onAttach -> onCreate -> onCreateView -> onActivityCreated -> onStart -> onResume;  
```


detach 与 attach  
fragmentTransaction.detach(fragmentA);  
```
onPause -> onStop -> onDestroyView  
```
fragmentTransaction.attach(fragmentA);  
```
onCreateView -> onActivityCreated -> onStart -> onResume  
```

### fragmentManager  

popBackStack(String tag,int flags)  
```
如果  tag = null, flags = 0, 弹出回退栈中最上层的那个fragment  
如果  tag = null, flags = 1, 弹出回退栈中所有fragment  
如果  tag != null, flags = 0, 弹出该fragment以上的所有Fragment, 不包括 tag
如果  tag != null, flags = 1, 弹出该fragment以上的所有Fragment, 包括 tag  
原本 D -> C -> B -> A ;  
执行 
```