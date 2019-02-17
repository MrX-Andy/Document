### lift  
```
Observable.merge(justA, justB)
.lift<String>(object : ObservableOperator<String, String> {
    override fun apply(observer: Observer<in String>): Observer<in String> {
        return object : LiteObserver<String>() {
            override fun onNext(result: String) {
                
            }

        }
    }
})
```