### Activity启动模式与任务栈  

● standard  默认值，多实例模式    

● singleTop  栈顶复用模式  
如果A不在栈顶，会触发onCreate创建新的实例；  
如果A已经在栈顶，不会触发onCreate方法，会按顺序触发onPause、onNewIntent、onResume 方法；  

● singleTask  栈内复用模式  
如果A不在栈内，会触发onCreate创建新的实例；  
如果A已经在栈内，不会触发onCreate方法，会按顺序触发onPause、onNewIntent、onResume 方法；  
如果A不在栈顶，一定会清空A之上所有的元素；  
例如浏览器的主界面。不管从多少个应用启动浏览器，只会启动主界面一次，其余情况都会走onNewIntent，并且会清空主界面上面的其他页面。  

● singleInstance  独栈模式  
闹铃的响铃界面。 你以前设置了一个闹铃：上午6点。在上午5点58分，你启动了闹铃设置界面，并按 Home 键回桌面；  
在上午5点59分时，你在微信和朋友聊天；在6点时，闹铃响了，并且弹出了一个对话框形式的 Activity(名为 AlarmAlertActivity)；  
提示你到6点了(这个 Activity 就是以 SingleInstance 加载模式打开的)，你按返回键，回到的是微信的聊天界面，  
这是因为 AlarmAlertActivity 所在的 Task 的栈只有他一个元素， 因此退出之后这个 Task 的栈空了。  
如果是以 SingleTask 打开 AlarmAlertActivity，那么当闹铃响了的时候，按返回键应该进入闹铃设置界面。  

◆ 示例  

等于清单文件的 STANDARD  
public static final int STANDARD = Intent.FLAG_ACTIVITY_NEW_TASK;  

等于清单文件的 SING_TOP， start之后，之前Activity的数据会保留不变  
public static final int SING_TOP = Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK;  

等于清单文件的 SING_TASK， start之后，之前Activity的数据会保留不变    
public static final int SING_TASK = Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK;  

比如说原来栈中情况是A,B,AppCon,D,在D中启动B(加入该flag),中间过程是A,B,C依次destory,    
D先onPause,随后BonCreate,onStart,onResume.D再onStop,onDestory.最后只有一个B在栈底.(无论taskAffinity..?)  
public static final int CLEAR_TASK = Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK;

比如说原来栈中情况是A,B,AppCon,D,在D中启动B(加入该flag)，栈中的情况会是A,AppCon,D,B.(调用onNewIntent())  
public static final int REORDER_2_FRONT = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK;  

A启动B(加入该Flag),B启动C.在C返回,将直接返回到A.B在A正常onResume后,才会调用onStop,onDestory...  
而且被这个flag启动的activity,它的onActivityResult()永远不会被调用  
public static final int NO_HISTORY = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK;  

多个Activity的值传递。A通过startActivityForResult启动B,B启动C，但B为过渡页可以finish了，  
A在期望C把结果返回. 这种情况,B可以在启动C的时候加入该flag.  
public static final int FORWARD_RESULT = Intent.FLAG_ACTIVITY_FORWARD_RESULT | Intent.FLAG_ACTIVITY_NEW_TASK;  

即 A---> B --->AppCon，若B启动C时用了这个标志位，那在启动时B并不会被当作栈顶的Activity，而是用A做栈顶来启动C。  
此过程中B充当一个跳转页面。  
典型的场景是在应用选择页面，如果在文本中点击一个网址要跳转到浏览器，而系统中又装了不止一个浏览器应用，  
此时会弹出应用选择页面。在应用选择页面选择某一款浏览器启动时，就会用到这个Flag。  
然后应用选择页面将自己finish，以保证从浏览器返回时不会在回到选择页面。经常与FLAG_ACTIVITY_FORWARD_RESULT 一起使用。  
public static final int PREVIOUS_IS_TOP = Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP | Intent.FLAG_ACTIVITY_FORWARD_RESULT | Intent.FLAG_ACTIVITY_NEW_TASK;  


FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS：Activity不会被放入到“最近启动的Activity”列表中。



### 如果历史栈中包含Activity，打开此Activity从栈中放到栈顶层而不是从新打开Activity  
```
Intent intent = new Intent(ReorderFour.this, ReorderTwo.class);  
intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  
startActivity(intent);  
```

### 参考  
https://www.jianshu.com/p/0d08f7f98666  
https://www.jianshu.com/p/c1386015856a  
