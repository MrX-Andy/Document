```

public static void showPopupWindow(PopupWindow pop, int popupHeight, View v) {
    if (pop == null || v == null) {
        return;
    }
    Context context = v.getContext();
    int[] location = new int[2];
    v.getLocationOnScreen(location);
    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    DisplayMetrics outMetrics = new DisplayMetrics();
    if (wm != null) {
        wm.getDefaultDisplay().getMetrics(outMetrics);
    }

    int heightPixels = outMetrics.heightPixels;
    if (location[1] >= heightPixels / 2) {//pop 展示 在 上面
        int bindViewHalfHeight = (int) (v.getHeight() * 0.5F);
        if (popupHeight + bindViewHalfHeight > location[1]) {
            int statusBarHeight = getStatusBarHeight(context);
            pop.setHeight(location[1] - statusBarHeight);
            pop.showAtLocation(v, Gravity.NO_GRAVITY, location[0] - pop.getWidth() / 2,
                    statusBarHeight);
        } else {
            pop.showAtLocation(v, Gravity.NO_GRAVITY, location[0] - pop.getWidth() / 2,
                    location[1] - popupHeight);
        }

    } else {// pop 展示 在下面
        if (location[1] + v.getHeight() + popupHeight > heightPixels) {
            pop.setHeight(heightPixels - location[1] - v.getHeight());
        }
        pop.showAsDropDown(v);
    }
}

private static int getStatusBarHeight(Context context) {
    int result = 0;
    if(context==null || context.getResources()==null){
        return result;
    }
    int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
            "android");
    if (resourceId > 0) {
        result = context.getResources().getDimensionPixelSize(resourceId);
    }
    return result;
}
```