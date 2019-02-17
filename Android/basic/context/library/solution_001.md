#### Fragment重叠异常  

重叠？  
假设Activity被回收了，再回到这个Activity时， 还会再次走onCreate, 应该 savedInstanceState为空,创建否则 不；  
Fragment也会走onCreate，但是Fragment的属性mHidden没有被保存， 也就是Fragment还会被show出来，  
上述两个条件，会导致，多造了Fragment， 并展示出来了；  

Activity  
```
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity);

    TargetFragment targetFragment;
    HideFragment hideFragment;
  
    if (savedInstanceState != null) {  // “内存重启”时调用
        targetFragment = getSupportFragmentManager().findFragmentByTag(TargetFragment.class.getName);
        hideFragment = getSupportFragmentManager().findFragmentByTag(HideFragment.class.getName);
        // 解决重叠问题
        getFragmentManager().beginTransaction()
                .show(targetFragment)
                .hide(hideFragment)
                .commit();
    }else{  // 正常时
        targetFragment = TargetFragment.newInstance();
        hideFragment = HideFragment.newInstance();

        getFragmentManager().beginTransaction()
                .add(R.id.container, targetFragment, targetFragment.getClass().getName())
                .add(R.id,container,hideFragment,hideFragment.getClass().getName())
                .hide(hideFragment)
                .commit();
    }
}
```  

Fragment  
```
override fun onCreateData(bundle: Bundle?) {
    fragmentManager?.beginTransaction()?.let {
        it.hide(this)
        it.commitAllowingStateLoss()
    }
}
```