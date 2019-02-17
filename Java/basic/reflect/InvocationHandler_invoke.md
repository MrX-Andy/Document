### InvocationHandler_invoke  invoke触发机制  

事实上， 是 每生成一个目标方法， 都会先调用一次 invoke  

◆ ProxyGenerator.generateProxyClass  

```
public static byte[] generateProxyClass(final String var0, Class<?>[] var1, int var2) {
    ProxyGenerator var3 = new ProxyGenerator(var0, var1, var2);
    final byte[] var4 = var3.generateClassFile();
    if (saveGeneratedFiles) {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
                try {
                    int var1 = var0.lastIndexOf(46);
                    Path var2;
                    if (var1 > 0) {
                        Path var3 = Paths.get(var0.substring(0, var1).replace('.', File.separatorChar));
                        Files.createDirectories(var3);
                        var2 = var3.resolve(var0.substring(var1 + 1, var0.length()) + ".class");
                    } else {
                        var2 = Paths.get(var0 + ".class");
                    }
                    Files.write(var2, var4, new OpenOption[0]);
                    return null;
                } catch (IOException var4x) {
                    throw new InternalError("I/O exception saving generated file: " + var4x);
                }
            }
        });
    }
    return var4;
}
```

◆ ProxyGenerator.generateClassFile  
事实上， 是 每生成一个目标方法， 都会先调用一次 invoke
```
private byte[] generateClassFile() {
    this.addProxyMethod(hashCodeMethod, Object.class);
    this.addProxyMethod(equalsMethod, Object.class);
    this.addProxyMethod(toStringMethod, Object.class);
 
    for(var3 = 0; var3 < var2; ++var3) {
        for(int var7 = 0; var7 < var6; ++var7) {
            Method var8 = var5[var7];
            this.addProxyMethod(var8, var4);
        }
    }
    Iterator var15;
    try {
        this.methods.add(this.generateConstructor());
        var11 = this.proxyMethods.values().iterator();
        while(var11.hasNext()) {
            while(var15.hasNext()) {
                ProxyGenerator.ProxyMethod var16 = (ProxyGenerator.ProxyMethod)var15.next();
                this.fields.add(new ProxyGenerator.FieldInfo(var16.methodFieldName, "Ljava/lang/reflect/Method;", 10));
                //  断点过去 
                this.methods.add(var16.generateMethod());
            }
        }

        this.methods.add(this.generateStaticInitializer());
    } catch (IOException var10) {
        throw new InternalError("unexpected I/O Exception", var10);
    }
    if (this.methods.size() > 65535) {
        throw new IllegalArgumentException("method limit exceeded");
    } else if (this.fields.size() > 65535) {
        throw new IllegalArgumentException("field limit exceeded");
    } else {
        try {
            var14.writeInt(-889275714);
            var14.writeShort(0);
            var14.writeShort(49);
            this.cp.write(var14);
            var14.writeShort(this.accessFlags);
            var14.writeShort(this.cp.getClass(dotToSlash(this.className)));
            var14.writeShort(this.cp.getClass("java/lang/reflect/Proxy"));
            ... 
            return var13.toByteArray();
        } catch (IOException var9) {
            throw new InternalError("unexpected I/O Exception", var9);
        }
    }
}
```

◆ ProxyGenerator.ProxyMethod.generateMethod  
```
private ProxyGenerator.MethodInfo generateMethod() throws IOException {
    // 找到 InvocationHandler 被使用的 罪证  
    var9.writeShort(ProxyGenerator.this.cp.getFieldRef("java/lang/reflect/Proxy", "h", "Ljava/lang/reflect/InvocationHandler;"));
    ProxyGenerator.this.code_aload(0, var9);
    var9.writeByte(178);
    var9.writeShort(ProxyGenerator.this.cp.getFieldRef(ProxyGenerator.dotToSlash(ProxyGenerator.this.className), this.methodFieldName, "Ljava/lang/reflect/Method;"));
    if (this.parameterTypes.length > 0) {
        ProxyGenerator.this.code_ipush(this.parameterTypes.length, var9);
        var9.writeByte(189);
        var9.writeShort(ProxyGenerator.this.cp.getClass("java/lang/Object"));

        for(int var10 = 0; var10 < this.parameterTypes.length; ++var10) {
            var9.writeByte(89);
            ProxyGenerator.this.code_ipush(var10, var9);
            this.codeWrapArgument(this.parameterTypes[var10], var3[var10], var9);
            var9.writeByte(83);
        }
    } else {
        var9.writeByte(1);
    }
    var9.writeShort(ProxyGenerator.this.cp.getInterfaceMethodRef("java/lang/reflect/InvocationHandler", "invoke", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;"));
    if (var13.size() > 0) {
        Iterator var11 = var13.iterator();

        while(var11.hasNext()) {
            Class var12 = (Class)var11.next();
            var2.exceptionTable.add(new ProxyGenerator.ExceptionTableEntry(var7, var8, var6, ProxyGenerator.this.cp.getClass(ProxyGenerator.dotToSlash(var12.getName()))));
        }

        var9.writeByte(191);
        var6 = (short)var2.code.size();
        var2.exceptionTable.add(new ProxyGenerator.ExceptionTableEntry(var7, var8, var6, ProxyGenerator.this.cp.getClass("java/lang/Throwable")));
        var9.writeShort(ProxyGenerator.this.cp.getMethodRef("java/lang/reflect/UndeclaredThrowableException", "<init>", "(Ljava/lang/Throwable;)V"));
    }
    return var2;
}
```

### InvocationHandler.invoke触发机制  

❀ Proxy.newProxyInstance  
```
public static Object newProxyInstance(ClassLoader loader,Class<?>[] interfaces,InvocationHandler h) throws IllegalArgumentException{
    Objects.requireNonNull(h);
    // 这里会生成 代理类  
    Class<?> cl = getProxyClass0(loader, intfs);
    ... 
    try {
        final Constructor<?> cons = cl.getConstructor(constructorParams);
        ...
        // InvocationHandler 对象，在这里被用到， 断点进去  
        return cons.newInstance(new Object[]{h});
    } catch (IllegalAccessException|InstantiationException e) {
        throw new InternalError(e.toString(), e);
    } 
}
```
❀ Constructor.newInstance  
```
public T newInstance(Object ... initargs) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    ... 
    ConstructorAccessor ca = constructorAccessor;   // read volatile
    if (ca == null) {
        // 这里 可以 看到 ca 就是 NativeConstructorAccessorImpl  
        ca = acquireConstructorAccessor();
    }
    // InvocationHandler 对象，在这里被用到， 断点进去  
    T inst = (T) ca.newInstance(initargs);
    return inst;
}
```
❀ sun.reflect.NativeConstructorAccessorImpl.newInstance  
```
public Object newInstance(Object[] var1) throws InstantiationException, IllegalArgumentException, InvocationTargetException {
    if (++this.numInvocations > ReflectionFactory.inflationThreshold() && !ReflectUtil.isVMAnonymousClass(this.c.getDeclaringClass())) {
        ConstructorAccessorImpl var2 = (ConstructorAccessorImpl)(new MethodAccessorGenerator()).generateConstructor(this.c.getDeclaringClass(), this.c.getParameterTypes(), this.c.getExceptionTypes(), this.c.getModifiers());
        this.parent.setDelegate(var2);
    }

    // InvocationHandler 对象，在这里被用到，
    return newInstance0(this.c, var1);
}
```
❀ private static native Object newInstance0(Constructor<?> var0, Object[] var1)    

