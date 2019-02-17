### 清单文件Activity标签属性

● android:allowTaskReparenting   
这个属性用来标记一个Activity实例在当前应用退居后台后，是否能从启动它的那个task移动到有共同affinity的task，  
“true”表示可以移动，“false”表示它必须呆在当前应用的task中，默认值为false。  
如果一个这个Activity的元素没有设定此属性，设定在上的此属性会对此Activity起作用。  
例如在一个应用中要查看一个web页面，在启动系统浏览器Activity后，这个Activity实例和当前应用处于同一个task，  
当我们的应用退居后台之后用户再次从主选单中启动应用，此时这个Activity实例将会重新宿主到Browser应用的task内，  
在我们的应用中将不会再看到这个Activity实例，而如果此时启动Browser应用，就会发现，第一个界面就是我们刚才打开的web页面，  
证明了这个Activity实例确实是宿主到了Browser应用的task内。 

● android:alwaysRetainTaskState   
只是对入口Activity有效这个属性用来标记应用的task是否保持原来的状态，“true”表示总是保持，“false”表示不能够保证，默认为“false”。  
此属性只对task的根Activity起作用，其他的Activity都会被忽略。 默认情况下，如果一个应用在后台呆的太久例如30分钟，  
用户从主选单再次选择该应用时，系统就会对该应用的task进行清理，除了根Activity，其他Activity都会被清除出栈，  
但是如果在根Activity中设置了此属性之后，用户再次启动应用时，仍然可以看到上一次操作的界面。   
这个属性对于一些应用非常有用，例如Browser应用程序，有很多状态，比如打开很多的tab，用户不想丢失这些状态，使用这个属性就极为恰当。 

```
@Override
public void onBackPressed()
{
	/*按返回键返回桌面，不结束Activity*/
	moveTaskToBack(true);
}
```
● android:clearTaskOnLaunch   
这个属性用来标记是否从task清除除根Activity之外的所有的Activity，“true”表示清除，“false”表示不清除，默认为“false”。  
同样，这个属性也只对根Activity起作用，其他的Activity都会被忽略。 如果设置了这个属性为“true”，每次用户重新启动这个应用时，  
都只会看到根Activity，task中的其他Activity都会被清除出栈。如果我们的应用中引用到了其他应用的Activity，  
这些Activity设置了allowTaskReparenting属性为“true”，则它们会被重新宿主到有共同affinity的task中。 
● android:finishOnTaskLaunch   
这个属性和android:allowReparenting属性相似，不同之处在于allowReparenting属性是重新宿主到有共同affinity的task中，  
而finishOnTaskLaunch属性是销毁实例。如果这个属性和android:allowReparenting都设定为“true”，则这个属性好些。  


