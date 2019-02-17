### Message.callback   
为什么会工作在UI线程?  
```
private fun handler() {
    val handler = Handler();
    val message = Message.obtain(handler, object : Runnable {
        override fun run() {
            "hello $isUiThread".logI()
        }

    });
    handler.sendMessage(message)
}

```
1.. 首先, 这段代码的打印结果是 hello true, 所以 这个 run 函数是工作在UI线程的...  
2.. 论证  
Handler#dispatchMessage  
```
public void dispatchMessage(Message msg) {

    //  A..  
    if (msg.callback != null) {
        handleCallback(msg);
    } else {
        if (mCallback != null) {
            if (mCallback.handleMessage(msg)) {
                return;
            }
        }
        //  B  
        handleMessage(msg);
    }
}

private static void handleCallback(Message message) {
    message.callback.run();
}
```
因为B处代码块, handleMessage 是工作在UI线程, 所以 dispatchMessage 也是工作在UI线程;  
所以 handleCallback 是工作在UI线程;  
事实上, 就是Looper 取出来消息, 交给 对应的Handler去处理  
