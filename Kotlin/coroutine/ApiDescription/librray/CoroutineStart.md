#####  CoroutineStart  
```
override fun onClick(v: View) {
	val id = v.id
	if (id == R.id.bt1) {
		"A".logI()
		launch(UI, CoroutineStart.UNDISPATCHED) {
			"B".logI()
			delay(3000)
			"C".logI()
		}
		"D".logI()
		return
	}
}
```
● CoroutineStart.UNDISPATCHED  执行结果：  
A  
B  
D  
C  

● CoroutineStart.DEFAULT  执行结果：  
A
D
B
C
