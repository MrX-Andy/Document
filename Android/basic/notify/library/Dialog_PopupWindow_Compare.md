### Dialog 和 PopupWindow 的比较  

1. PopupWindow 在显示之前一定要设置宽高， Dialog 无此限制。  
2. PopupWindow 默认不会响应物理键盘的back，除非显示设置了popup.setFocusable(true); 而Dialog会响应back键，默认情况 dialog  会消失。  
3. PopupWindow 不会给页面其他的部分添加蒙层，而Dialog会。  
4. PopupWindow 没有标题，Dialog默认有标题，可以通过dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) 取消标题；  
5. 二者显示的时候都要设置Gravity。如果不设置，Dialog默认是Gravity.CENTER；  
6. 二者都有默认的背景，都可以通过 setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));去掉。  
7. PopupWindow 不能在Activity.onCreate 方法展示出来， Dialog却可以。  
8. 其中最本质的差别就是：  
AlertDialog是非阻塞式对话框：AlertDialog弹出时，后台还可以做事情；  
而PopupWindow是阻塞式对话框：PopupWindow弹出时，程序会等待，在PopupWindow退出前，程序一直等待，只有当我们调用了dismiss方法的后，PopupWindow退出，程序才会向下执行。  
这两种区别的表现是：AlertDialog弹出时，背景是黑色的，但是当我们点击背景，AlertDialog会消失，证明程序不仅响应AlertDialog的操作，还响应其他操作，其他程序没有被阻塞，  
这说明了AlertDialog是非阻塞式对话框；PopupWindow弹出时，背景没有什么变化，但是当我们点击背景的时候，程序没有响应，只允许我们操作PopupWindow，使得其他控件不能被操作，  
也就是说，虽然PopupWindow在展示的时候，不会像Dialog那样有背景遮罩，但是其他的控件的点击事件，也不会得到响应。  

  

