  ### HandlerThread 
 
 ```
 @Override
 protected void onCreate(@Nullable Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     handlerThread = new HandlerThread("HandlerThread");
     handlerThread.start();
     Handler handler = new Handler(handlerThread.getLooper()) {
         @Override
         public void handleMessage(Message msg) {
             super.handleMessage(msg);
             // 处理 事件
         }
     };
     new Thread(new Runnable() {
         @Override
         public void run() {
             handler.sendEmptyMessage(0);
         }
     }).start();
 }
 
 @Override
 protected void onDestroy() {
     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
         handlerThread.quitSafely();
     } else {
         handlerThread.quit();
     }
     super.onDestroy();
 }
 ```
❀ HandlerThread 的特点  
HandlerThread 将 loop 转到子线程中处理, 说白了就是将分担 mainLooper 的工作量, 降低了主线程的压力, 使主界面更流畅;  
HandlerThread 拥有自己的消息队列, 它不会干扰或阻塞 UI 线程;  
HandlerThread 本质是一个线程, 在线程内部, 代码是串行处理的;  
但是由于每一个任务, 都将以队列的方式逐个被执行到, 一旦队列中有某个任务执行时间过长, 那么就会导致后续的任务都会被延迟处理;  
对于 网络/IO 操作, HandlerThread 并不适合, 因为它只有一个线程, 还得排队一个一个等着;  


