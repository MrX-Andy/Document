### filter  
```
.filter(new Predicate<String>() {
    @Override
    public boolean test(String result) throws Exception {
        return "5".equalsIgnoreCase(result);
    }
})
```  
test返回值true， 下游可以收到元素， test返回值false，下游收不到；  
