######
```
表达式-lamada表达式
1. 假设我们定义一个 做加法运算的函数 sum
fun sum(a:Int, b:Int):Int{
    return a+b
}

2. 我们可以改成这样
/*
* sum 是一个对象，他的数据类型是一个函数
* */
val sum = fun(a: Int, b: Int): Int {
    return a + b
}

2.1看一下对应的java class
public final class TestKt {
   @NotNull
   private static final Function2 sum;

   public static final void main(@NotNull String[] args) {
      Intrinsics.checkParameterIsNotNull(args, "args");
      LogExtensionKt.logE(sum.invoke(Integer.valueOf(1), Integer.valueOf(2)));
   }

   @NotNull
   public static final Function2 getSum() {
      return sum;
   }

   static {
      sum = (Function2)null.INSTANCE;
   }
}
public interface Function2<in P1, in P2, out R> : Function<R> {
    /** Invokes the function with the specified arguments. */
    public operator fun invoke(p1: P1, p2: P2): R
}


3. 我们可以改成这样
/*
* sum 是一个对象，他的数据类型是一个函数
* */
val sum = fun(a: Int, b: Int) = a + b

4. 我们可以改成lamada
/*
* sum 是一个对象，他的数据类型是一个函数（实际上对应java的接口类型）
* */
val sum = { a: Int, b: Int ->
    "$a  +  $b = ${a + b}".logW()
    /*最后一句 作为 函数的 返回值*/
    a + b
}

5. 难一点的lamada

fun main(args: Array<String>) {

    val nameArray = arrayListOf<String>("A", "B", "C", "D", "E")
    nameArray.mForEach {
        it.logW()
    }
    nameArray.mForEach {
        item -> item.logW()
    }
    nameArray.mForEach {
        "hello".logW()
        "Alex".logW()
        it.logW()
    }
    nameArray.mForEach {
        it.apply {
            it.javaClass.simpleName.logE()
        }
        "hello".logW()
        "Alex".logW()
    }
}

/**
 * 这是一个 ArrayList的扩展方法，那么只有ArrayList的对象，可以调用这个方法
 * 这个方法的入口参数是action一个 block，相当于java的接口，
 * 对应Java的理解
 *     mForEach会把 ArrayList里面所有元素 迭代出来，并且 会执行 action 这个接口 的 invoke方法
 * 对应Kotlin的理解
 *     mForEach会把ArrayList里面的所有元素迭代出来，并且会执行action对应的 block（代码块）
 * */
inline fun <T> ArrayList<T>.mForEach(action: (T) -> Unit): Unit {
    for (item in this) {
        action(item)
    }
}



```