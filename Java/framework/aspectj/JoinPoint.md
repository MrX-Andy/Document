### JoinPoint  

getThis  
```
/**
 * <p> Returns the currently executing object.  This will always be
 * the same object as that matched by the <code>this</code> pointcut
 * designator.  Unless you specifically need this reflective access,
 * you should use the <code>this</code> pointcut designator to
 * get at this object for better static typing and performance.</p>
 *
 * <p> Returns null when there is no currently executing object available.
 * This includes all join points that occur in a static context.</p>
 */
```
得到当前被执行的类对象，

getTarget  
