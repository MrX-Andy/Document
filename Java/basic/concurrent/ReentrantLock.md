### ReentrantLock  
ReentrantLock, Condition, AbstractQueuedSynchronizer;  
ReentrantLock#Sync 继承与 AbstractQueuedSynchronizer;  
AbstractQueuedSynchronizer 继承与 AbstractOwnableSynchronizer;  
ReentrantLock#FairSync 继承与 Sync;  
ReentrantLock#NonfairSync 继承与 Sync;  

支持重入性, 表示能够对共享资源能, 重复加锁, 即当前线程获, 再次获取该锁时, 不会被阻塞;
由于锁会被获取n次, 那么只有, 锁在被释放同样的n次之后, 该锁才算是完全释放成功;    
synchronized 关键字, 隐式支持重入性;  
ReentrantLock 类似于 synchronized 的增强版, synchronized 的特点是使用简单, 一切交给JVM去处理, 但是功能上是比较薄弱的;  
ReentrantLock 功能更加丰富, 它具有可重入, 可中断, 可限时, 公平锁等特点;  
两者性能是不相上下的, 如果是简单的实现, 不要刻意去使用 ReentrantLock;  


公平锁指的是线程获取锁的顺序, 是按照加锁顺序来的;  
非公平锁, 指的是抢锁机制, 先lock的线程, 不一定先获得锁;  

公平锁, 每次获取到的锁, 为同步队列中的第一个节点, 保证请求资源时间上的绝对顺序;  
而非公平锁, 有可能出现, 刚释放锁的线程, 下次还会继续获取该锁, 可能导致其他线程, 永远无法获取到锁, 造成“饥饿”现象;  

公平锁为了保证, 时间上的绝对顺序, 需要频繁的上下文切换, 而非公平锁会降低一定的上下文切换, 降低性能开销;  
ReentrantLock 默认选择的是非公平锁, 则是为了减少一部分上下文切换, 保证了系统更大的吞吐量;  

ReentrantLock#lock  
如果锁空闲, 直接占用锁;  
如果锁被当前线程占用, 直接占用锁;  
如果锁被其他线程占用, 线程入队;  
前驱节点是head, 并且成功获取锁, 设置当前节点为head, 原节点出队, 返回中断状态;  

### lock  简单的 锁  
```
private static final class MyRunnable implements Runnable {
        private final ReentrantLock lock = new ReentrantLock();
        int count = 0;

        @Override
        public void run() {
            lock.lock();
            ThreadUtil.sleep(1000);
            count++;
            LogTrack.w(Thread.currentThread().getName() + ", count = " + count);
            lock.unlock();
        }
    }
```
### tryLock  超时 锁
多个线程执行，如果有一个超时了，A代码块 就不会再有锁了，  

```
public void run() {
    ThreadUtil.tryLock(lock, 500);
    {  // A 代码块
    ThreadUtil.sleep(1000);
    count++;
    LogTrack.w(Thread.currentThread().getName() + ", count = " + count);
    }
    if (lock.isHeldByCurrentThread()) {
        lock.unlock();
    }
}
```
### API  
tryLock 能获得锁就返回 true, 不能就立即返回 false, 也就是说，这个方法无论如何都会立即返回;  

tryLock(long timeout,TimeUnit unit), 在拿不到锁时, 会等待一定的时间;  
等待过程中, 可以被中断;  
超过时间, 依然获取不到, 则返回false;  

lock 能获得锁就返回 true, 不能的话一直等待获得锁;  
如果采用 lock, 必须主动去释放锁, 并且在发生异常时, 不会自动释放锁;  

lock 和 lockInterruptibly, 如果两个线程分别执行这两个方法, 但此时中断这两个线程, lock不会抛出异常, 而 lockInterruptibly 会抛出异常;  

Condition newCondition();  返回一个绑定该 lock 的 Condition 对象;  
在 Condition#await 之前, 锁会被该线程持有;  
Condition#await 会自动释放锁, 在 wait 返回之后, 会自动获取锁;  

```
getHoldCount() 查询当前线程保持此锁的次数，也就是执行此线程执行lock方法的次数
getQueueLength（）返回正等待获取此锁的线程估计数，比如启动10个线程，1个线程获得锁，此时返回的是9
getWaitQueueLength（Condition condition）返回等待与此锁相关的给定条件的线程估计数。比如10个线程，用同一个condition对象，并且此时这10个线程都执行了condition对象的await方法，那么此时执行此方法返回10
hasWaiters(Condition condition)查询是否有线程等待与此锁有关的给定条件(condition)，对于指定contidion对象，有多少线程执行了condition.await方法
hasQueuedThread(Thread thread)查询给定线程是否等待获取此锁
hasQueuedThreads()是否有线程等待此锁
isFair()该锁是否公平锁
isHeldByCurrentThread() 当前线程是否保持锁锁定，线程的执行lock方法的前后分别是false和true
isLock()此锁是否有任意线程占用
lockInterruptibly（）如果当前线程未被中断，获取锁
tryLock（）尝试获得锁，仅在调用时锁未被线程占用，获得锁
tryLock(long timeout TimeUnit unit)如果锁在给定等待时间内没有被另一个线程保持，则获取该锁
```  

### Condition  
Condition 一般是与 Lock 配合使用, 应用在多线程协同工作的场景中;  

// 使当前线程处于等待状态，释放与Condition绑定的lock锁
// 直到 singal()方法被调用后，被唤醒（若中断，就game over了）
// 唤醒后，该线程会再次获取与条件绑定的 lock锁
void await() throws InterruptedException;

// 相比较await()而言，不响应中断
void awaitUninterruptibly();

// 在wait()的返回条件基础上增加了超时响应，返回值表示当前剩余的时间
// < 0 ，则表示超时
long awaitNanos(long nanosTimeout) throws InterruptedException;

// 同上，只是时间参数不同而已
boolean await(long time, TimeUnit unit) throws InterruptedException;

// 同上，只是时间参数不同而已
boolean awaitUntil(Date deadline) throws InterruptedException;

// 表示条件达成，唤醒一个被条件阻塞的线程
void signal();

// 唤醒所有被条件阻塞的线程。
void signalAll();

### AbstractQueuedSynchronizer#Node  
Node SHARED = new Node();  //  表示Node处于共享模式  
Node EXCLUSIVE = null;  //  表示Node处于独占模式  

int CANCELLED = 1;  
//  因为超时或者中断, Node 被设置为取消状态;  
//  被取消的 Node, 不应该去竞争锁, 只能保持取消状态不变, 不能转换为其他状态;  
//  处于这种状态的 Node, 会被踢出队列, 被GC回收;  

int SIGNAL = -1;  //  表示这个Node, 的继任(后驱) Node 被阻塞了, 到时需要通知它;  
int CONDITION = -2;  //  表示这个Node, 在条件队列中, 因为等待某个条件而被阻塞;  
int PROPAGATE = -3;  //  使用在共享模式头Node, 有可能处于这种状态,  表示锁的下一次获取, 可以无条件传播;  
int waitStatus;  //  0, 新 Node 会处于这种状态;  
Node prev;  //  队列中某个 Node 的前驱 Node;  
Node next;  //  队列中某个 Node 的后继 Node;  
Thread thread;  //  这个 Node 持有的线程, 表示等待锁的线程;  
Node nextWaiter	;  //  表示下一个等待 condition 的Node;  
### AbstractQueuedSynchronizer  
Thread exclusiveOwnerThread;  //  这个是AQS父类 AbstractOwnableSynchronizer 的属性, 表示独占模式同步器的当前拥有者;  
Node  //  FIFO队列的基本单位;  
Node head;  //  FIFO 队列中的头 Node;  
Node tail;  //  FIFO 队列中的尾 Node;  
int state;  //  同步状态, 0 表示未锁;  
getState()  //  获取同步状态;  
setState()  //  设置同步状态;  
compareAndSetState(expect, update)  //  利用 CAS 进行 State 的设置;  
long spinForTimeoutThreshold = 1000L;  //  线程自旋等待的时间;  
enq(node);  //  插入一个 Node 到队列中;  
addWaiter(mode);  //  为当前线程, 和指定模式, 创建并扩充一个等待队列;  
setHead(node);  //  设置队列的头 Node  
unparkSuccessor(node);  //  如果存在的话, 唤起 Node 持有的线程;  
doReleaseShared();  //  共享模式下做释放锁的动作;  
cancelAcquire(node);  //  取消正在进行的, Node 获取锁的尝试;  
shouldParkAfterFailedAcquire(pred, node)	;  //  在尝试获取锁失败后, 是否应该禁用当前线程并等待;  
selfInterrupt();  //  中断当前线程本身;  
parkAndCheckInterrupt()  //  禁用当前线程进入等待状态, 并中断线程本身;  
acquireQueued(Node node, arg)  //  队列中的线程获取锁;  
tryAcquire(arg)  //  尝试获得锁(由AQS的子类实现它)  
tryRelease(arg)	  //  尝试释放锁(由AQS的子类实现它)  
isHeldExclusively()  //  是否独自持有锁;  
acquire(arg)  //  获取锁;  
release(arg)  //  释放锁;  
compareAndSetHead(update)  //  利用 CAS 设置头 Node;  
compareAndSetTail(expect, update)  //  利用 CAS 设置尾 Node;  
compareAndSetWaitStatus(node, expect, update)  //  利用 CAS 设置某个 Node 中的等待状态;  

### 参考  
https://my.oschina.net/u/566591/blog/1557978  
https://www.cnblogs.com/xrq730/p/4979021.html  
https://juejin.im/entry/5c516476f265da61736aab3e  
