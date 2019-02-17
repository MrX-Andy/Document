$S   匹配字符串  


◆ $T  匹配  
示例 1  
```
builder.addStatement("Request request = new Request.Builder()\n" +
        ".activityClass($T.class)\n" +
        ".build()", value.toString());
```
结果 1  
```
Request request = new Request.Builder()
    .activityClass(NewAActivity.class)
    .build();
```  
◆ $N  匹配  
示例 1  
```
builder.addStatement("Request request = new Request.Builder()\n" +
                    ".activityClass($N.class)\n" +
                    ".build()", processingEnv.getElementUtils().getName(annotationFormatEntity.valueClassName()));
```
结果 1  
```
Request request = new Request.Builder()
    .activityClass(com.alex.andfun.apt.NewAActivity.class)
    .build();
```  
com.squareup.javapoet.CodeBlock.Builder#addArgument  
```
com.sun.tools.javac.code.Type$ClassType

com.sun.tools.javac.code.Type  
com.sun.tools.javac.code.AnnoConstruct   
javax.lang.model.AnnotatedConstruct  
javax.lang.model.type.TypeMirror  
```  

◆ 为什么是$T  
```
private TypeName argToType(Object o) {
  if (o instanceof TypeName) return (TypeName) o;
  if (o instanceof TypeMirror) return TypeName.get((TypeMirror) o);
  if (o instanceof Element) return TypeName.get(((Element) o).asType());
  if (o instanceof Type) return TypeName.get((Type) o);
  throw new IllegalArgumentException("expected type but was " + o);
}

```
◆ 为什么是$N  
```
private String argToName(Object o) {
  if (o instanceof CharSequence) return o.toString();
  if (o instanceof ParameterSpec) return ((ParameterSpec) o).name;
  if (o instanceof FieldSpec) return ((FieldSpec) o).name;
  if (o instanceof MethodSpec) return ((MethodSpec) o).name;
  if (o instanceof TypeSpec) return ((TypeSpec) o).name;
  throw new IllegalArgumentException("expected name but was " + o);
}
```