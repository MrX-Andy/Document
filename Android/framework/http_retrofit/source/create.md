#### create  

◆ [Proxy.newProxyInstance动态代理接口](../../../../Java/basic/reflect/java_reflect.md)  
概述就是，利用反射，解class，拿到关于interface的所有方法的声明，  
在内存中生成关于目标接口的代理类，这个代理类所有的方法，在调用之前，都会先调用invokeHandler.invoke方法；  
所以，被造出来的这个代理对象，每调用一个方法，都会触发invokeHandler.invoke的回调；  
讲retrofit源码，首先讲的就是Proxy.newProxyInstance这个方法；  

```
public <T> T create(final Class<T> service) {
       ...
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service},
                new InvocationHandler() {
                    private final Platform platform = Platform.get();
                    @Override
                    public Object invoke(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                        ..
                        // 关注这个方法，
                        ServiceMethod<Object, Object> serviceMethod = (ServiceMethod<Object, Object>) loadServiceMethod(method);
                        OkHttpCall<Object> okHttpCall = new OkHttpCall<>(serviceMethod, args);
                        return serviceMethod.callAdapter.adapt(okHttpCall);
                    }
                });
    }
```

◆ Retrofit#loadServiceMethod  
```
ServiceMethod<?, ?> loadServiceMethod(Method method) {
    ServiceMethod<?, ?> result = serviceMethodCache.get(method);
    if (result != null) return result;
    synchronized (serviceMethodCache) {
        result = serviceMethodCache.get(method);
        if (result == null) {
            result = new ServiceMethod.Builder<>(this, method).build();
            serviceMethodCache.put(method, result);
        }
    }
    return result;
}
```
◆ ServiceMethod.Builder#Builder  
♬  
◑ ServiceMethod.Builder#createCallAdapter  
◑ Retrofit#nextCallAdapter  
◑ RxJava2CallAdapterFactory#get  
关联retrofit.callAdapter(returnType, annotations);  事实上，callAdapter可能不止一个，retrofit会根据ApiService中的每一个method的属性，匹配一个callAdapter；  
根据method的returnType 和 方法体上的注解列表，关联合适的callAdapter；  
♬  
拿到具体的 responseType 例如 retrofit2.Response、retrofit2.adapter.rxjava2.Result、Observable<Wrapper<UserBean>>；    
♬  
◑ ServiceMethod.Builder#createResponseConverter  
◑ Retrofit#nextResponseBodyConverter  
◑ GsonConverterFactory#responseBodyConverter  
关联根据返回值类型，convertFactory  
♬  
◑ ServiceMethod.Builder#parseMethodAnnotation  

构建网络请求， 做了一些准备工作；  
1. 解析Method，拿到其注解，以及具体的提交参数；  

◆ OkHttpCall#OkHttpCall  、RxJava2CallAdapter#adapt  

