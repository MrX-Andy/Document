### Retention  
@Retention(RetentionPolicy.SOURCE)   
源码时注解，一般用来作为编译器标记。就比如Override, Deprecated, SuppressWarnings这样的注解。  

@Retention(RetentionPolicy.RUNTIME)   
运行时注解，一般在运行时通过反射去识别的注解。  

@Retention(RetentionPolicy.CLASS)  
编译时注解，在编译时处理。  
