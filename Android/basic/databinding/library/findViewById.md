#### 为什么dataBinding.viewId的性能会优于 findViewById  

#####  LoginDataBindingImpl 
```
@Nullable
private static final android.util.SparseIntArray sViewsWithIds;
static {
    sIncludes = null;
    sViewsWithIds = new android.util.SparseIntArray();
    sViewsWithIds.put(R.id.simpleTooBar, 6);
    sViewsWithIds.put(R.id.tvPhone, 7);
    sViewsWithIds.put(R.id.tvPwd, 8);
    sViewsWithIds.put(R.id.tvRegister, 9);
    sViewsWithIds.put(R.id.tvLogout, 10);
    sViewsWithIds.put(R.id.tvReGetPwd, 11);
}

public LoginDataBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
    this(bindingComponent, root, mapBindings(bindingComponent, root, 12, sIncludes, sViewsWithIds));
}
```

##### ViewDataBinding 源码  
```
protected static Object[] mapBindings(DataBindingComponent bindingComponent, View root,
        int numBindings, IncludedLayouts includes, SparseIntArray viewsWithIds) {
    Object[] bindings = new Object[numBindings];
    mapBindings(bindingComponent, root, bindings, includes, viewsWithIds, true);
    return bindings;
}

private static void mapBindings(DataBindingComponent bindingComponent, View view,
        Object[] bindings, IncludedLayouts includes, SparseIntArray viewsWithIds,
        boolean isRoot) {
    final int indexInIncludes;
    final ViewDataBinding existingBinding = getBinding(view);
    if (existingBinding != null) {
        return;
    }
    Object objTag = view.getTag();
    final String tag = (objTag instanceof String) ? (String) objTag : null;
    boolean isBound = false;
    if (isRoot && tag != null && tag.startsWith("layout")) {
        final int underscoreIndex = tag.lastIndexOf('_');
        if (underscoreIndex > 0 && isNumeric(tag, underscoreIndex + 1)) {
            final int index = parseTagInt(tag, underscoreIndex + 1);
            if (bindings[index] == null) {
                bindings[index] = view;
            }
            indexInIncludes = includes == null ? -1 : index;
            isBound = true;
        } else {
            indexInIncludes = -1;
        }
    } else if (tag != null && tag.startsWith(BINDING_TAG_PREFIX)) {
        int tagIndex = parseTagInt(tag, BINDING_NUMBER_START);
        if (bindings[tagIndex] == null) {
            bindings[tagIndex] = view;
        }
        isBound = true;
        indexInIncludes = includes == null ? -1 : tagIndex;
    } else {
        // Not a bound view
        indexInIncludes = -1;
    }
    if (!isBound) {
        final int id = view.getId();
        if (id > 0) {
            int index;
            if (viewsWithIds != null && (index = viewsWithIds.get(id, -1)) >= 0 &&
                    bindings[index] == null) {
                bindings[index] = view;
            }
        }
    }

    if (view instanceof  ViewGroup) {
        final ViewGroup viewGroup = (ViewGroup) view;
        final int count = viewGroup.getChildCount();
        int minInclude = 0;
        for (int i = 0; i < count; i++) {
            final View child = viewGroup.getChildAt(i);
            boolean isInclude = false;
            if (indexInIncludes >= 0 && child.getTag() instanceof String) {
                String childTag = (String) child.getTag();
                if (childTag.endsWith("_0") &&
                        childTag.startsWith("layout") && childTag.indexOf('/') > 0) {
                    // This *could* be an include. Test against the expected includes.
                    int includeIndex = findIncludeIndex(childTag, minInclude,
                            includes, indexInIncludes);
                    if (includeIndex >= 0) {
                        isInclude = true;
                        minInclude = includeIndex + 1;
                        final int index = includes.indexes[indexInIncludes][includeIndex];
                        final int layoutId = includes.layoutIds[indexInIncludes][includeIndex];
                        int lastMatchingIndex = findLastMatching(viewGroup, i);
                        if (lastMatchingIndex == i) {
                            bindings[index] = DataBindingUtil.bind(bindingComponent, child,
                                    layoutId);
                        } else {
                            final int includeCount =  lastMatchingIndex - i + 1;
                            final View[] included = new View[includeCount];
                            for (int j = 0; j < includeCount; j++) {
                                included[j] = viewGroup.getChildAt(i + j);
                            }
                            bindings[index] = DataBindingUtil.bind(bindingComponent, included,
                                    layoutId);
                            i += includeCount - 1;
                        }
                    }
                }
            }
            if (!isInclude) {
                mapBindings(bindingComponent, child, bindings, includes, viewsWithIds, false);
            }
        }
    }
}
```
##### ViewDataBinding 分析  
事实上每次findViewById操作，都会遍历一次xml的树结构；   
mapBindings 方法，也是遍历书结构，但是只会遍历一次，之后，就会将View 的对象变成全局常量，以空间换时间的方式，效率变高；  

