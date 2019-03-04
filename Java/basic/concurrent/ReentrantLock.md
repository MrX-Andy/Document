ReentrantLock, Condition, AbstractQueuedSynchronizer;  
ReentrantLock#FairSync 继承与 Sync;  
ReentrantLock#NonfairSync 继承与 Sync;  
ReentrantLock#Sync 继承与 AbstractQueuedSynchronizer;  
AbstractQueuedSynchronizer 继承与 AbstractOwnableSynchronizer;  


支持重入性, 表示能够对共享资源能, 重复加锁, 即当前线程获, 再次获取该锁时, 不会被阻塞;
由于锁会被获取n次, 那么只有, 锁在被释放同样的n次之后, 该锁才算是完全释放成功;    
synchronized 关键字, 隐式支持重入性;  
ReentrantLock 类似于 synchronized 的增强版, synchronized 的特点是使用简单, 一切交给JVM去处理, 但是功能上是比较薄弱的;  
ReentrantLock 功能更加丰富, 它具有可重入, 可中断, 可限时, 公平锁等特点;  
两者性能是不相上下的, 如果是简单的实现, 不要刻意去使用 ReentrantLock;  

### 锁的分类  
公平锁指的是线程获取锁的顺序, 是按照申请锁的顺序;  
非公平锁指的是抢锁机制, 先申请锁的线程, 不一定先获得锁;  

公平锁, 每次获取到的锁, 为同步队列中的第一个节点, 保证请求资源时间上的绝对顺序;  
而非公平锁, 有可能出现, 刚释放锁的线程, 下次还会继续获取该锁, 可能导致其他线程, 永远无法获取到锁, 造成"饥饿"现象;  

公平锁为了保证, 时间上的绝对顺序, 需要频繁的上下文切换, 而非公平锁会降低一定的上下文切换, 降低性能开销;  
ReentrantLock 默认选择的是非公平锁, 则是为了减少一部分上下文切换, 保证了系统更大的吞吐量;  

❀ 公平锁, 为什么会比较慢?  
公平锁性能比较低, 不是因为检查队列会变慢, 而是因为要让活跃线程无法得到锁, 进入等待状态, 并唤醒等待的线程, 引起了频繁的上下文切换, 从而降低了整体的效率;  


### ReentrantLock#synchronized  
1.. Lock 是一个接口, 而 synchronized 是 Java 中的关键字, synchronized 是内置的语言实现;  

2.. synchronized 在发生异常时, 会自动释放线程占有的锁,  因此不会导致死锁现象发生;  
而 Lock 在发生异常时, 如果没有主动通过 unLock()去释放锁, 则很可能造成死锁现象, 因此使用 Lock 时需要在 finally 块中释放锁;  

3.. Lock 可以让等待锁的线程响应中断, 而 synchronized 却不行, 使用 synchronized 时, 等待的线程会一直等待下去, 不能够响应中断;  

4.. 通过 Lock 可以知道有没有成功获取锁, 而 synchronized 却无法办到;  

5.. Lock 可以提高多个线程进行读操作的效率;  

lock 适合于, 条件变量, 时间轮询, 中断等待, 无限等待;  
synchronized 适合于简单的线程同步;  

### 示例代码  
#### 简单的锁  
```
public class Test01 {
    public static void main(String[] args) {

        UserEntity userEntity = new UserEntity();
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    userEntity.print("hello" + finalI);
                }
            }.start();
        }

    }

    private static final class UserEntity {
        private ReentrantLock lock = new ReentrantLock();

        void print(String message) {
            lock.lock();
            LogTrack.w(message + ", thread = " + Thread.currentThread().getName());
            ThreadUtil.sleep(1000);
            lock.unlock();
        }
    }

}
```

### ReentrantLock  
ReentrantLock#tryLock  
立即返回结果, 尝试获得锁, 如果获得锁立即返回 true, 失败立即返回 false;  

ReentrantLock#tryLock(timeout,TimeUnit)  
规定时间内, 能拿到锁, 返回 true;  
规定时间内, 拿不到锁, 返回 false;
等待过程中, 可以被中断;  

ReentrantLock#lock  
lock 的次数要等于unlock 的次数相等,  
如果 unlock > lock 出现 IllegalMonitorStateException;  
如果 unlock < lock 该线程依旧持有锁, 导致其他线程无法获取锁;  
lock 能获得锁就返回 true, 获取不到锁会一直等待, 不可被打断, 即使当前线程被中断, 线程也一直阻塞, 直到拿到锁;  
如果锁空闲, 直接占用锁;  
如果锁被当前线程占用, 直接占用锁;  
如果锁被其他线程占用, 线程入队;  
前驱节点是 head, 并且成功获取锁, 设置当前节点为 head, 原节点出队, 返回中断状态;  

ReentrantLock#lockInterruptibly   
获取锁, 可中断, 如果获取锁之前当前线程被中断了, 获取锁之后会抛出 InterruptedException, 并且停止当前线程, 优先响应中断;  

unlock  
释放当前线程将释放持有的锁, 如果线程并不持有锁, 执行该方法, 会发生异常;  

Condition newCondition();  
返回一个绑定该 lock 的 Condition 对象;  
在 Condition#await 之前, 锁会被该线程持有;  
Condition#await 会自动释放锁, 在 wait 返回之后, 会自动获取锁;  

```
getHoldCount() 查询当前线程保持此锁的次数, 也就是执行此线程执行lock方法的次数
getQueueLength（）返回正等待获取此锁的线程估计数, 比如启动10个线程, 1个线程获得锁, 此时返回的是9
getWaitQueueLength（Condition condition）返回等待与此锁相关的给定条件的线程估计数。比如10个线程, 用同一个condition对象, 并且此时这10个线程都执行了condition对象的await方法, 那么此时执行此方法返回10
hasWaiters(Condition condition)查询是否有线程等待与此锁有关的给定条件(condition), 对于指定contidion对象, 有多少线程执行了condition.await方法
hasQueuedThread(Thread thread)查询给定线程是否等待获取此锁
hasQueuedThreads()是否有线程等待此锁
isFair()该锁是否公平锁
isHeldByCurrentThread() 当前线程是否保持锁锁定, 线程的执行lock方法的前后分别是false和true
isLock()此锁是否有任意线程占用
lockInterruptibly（）如果当前线程未被中断, 获取锁
tryLock（）尝试获得锁, 仅在调用时锁未被线程占用, 获得锁
tryLock(long timeout TimeUnit unit)如果锁在给定等待时间内没有被另一个线程保持, 则获取该锁
```  

### Condition  
Condition 一般是与 Lock 配合使用, 应用在多线程协同工作的场景中;  

// 使当前线程处于等待状态, 释放与 Condition 绑定的 lock 锁  
// 直到 singal()方法被调用后, 被唤醒(若中断, 就game over了)  
// 唤醒后, 该线程会再次获取与条件绑定的 lock锁  
void await() throws InterruptedException;

// 相比较await()而言, 不响应中断
void awaitUninterruptibly();

// 在wait()的返回条件基础上增加了超时响应, 返回值表示当前剩余的时间
// < 0 , 则表示超时
long awaitNanos(long nanosTimeout) throws InterruptedException;

// 同上, 只是时间参数不同而已
boolean await(long time, TimeUnit unit) throws InterruptedException;

// 同上, 只是时间参数不同而已
boolean awaitUntil(Date deadline) throws InterruptedException;

// 表示条件达成, 唤醒一个被条件阻塞的线程
void signal();

// 唤醒所有被条件阻塞的线程。
void signalAll();

### AbstractQueuedSynchronizer#Node  
Node SHARED = new Node();  
表示 Node 处于共享模式  

Node EXCLUSIVE = null;  
表示 Node 处于独占模式  

int CANCELLED = 1;  
因为超时或者中断, Node 被设置为取消状态;  
被取消的 Node, 不应该去竞争锁, 只能保持取消状态不变, 不能转换为其他状态;  
处于这种状态的 Node, 会被踢出队列, 被GC回收;  

int SIGNAL = -1;  
线程的后继线程正在/已经被阻塞, 当该线程 release 或 cancel 时要重新这个后继线程(unpark);  

int CONDITION = -2;   
表明该线程被处于条件队列, 就是因为调用了 Condition.await 而被阻塞;  

int PROPAGATE = -3;   
使用在共享模式头 Node, 有可能处于这种状态, 表示锁的下一次获取, 可以无条件传播;  

int waitStatus;   
0, 新 Node 会处于这种状态;  

Node prev;   
队列中某个 Node 的前驱 Node;  

Node next;    
队列中某个 Node 的后继 Node;  

Thread thread;     
这个 Node 持有的线程, 表示等待锁的线程;  

Node nextWaiter	;     
表示下一个等待 condition 的 Node;  

### AbstractQueuedSynchronizer  
Thread exclusiveOwnerThread;   
这个是 AQS 父类 AbstractOwnableSynchronizer 的属性, 表示独占模式同步器的当前拥有者;  

Node  
FIFO 队列的基本单位;  

Node head;  
FIFO 队列中的头 Node;  

Node tail;  
FIFO 队列中的尾 Node;  
int state;  
同步状态, 0 表示未锁;  

getState()  
获取同步状态;  

setState()   
设置同步状态;  

compareAndSetState(expect, update)   
利用 CAS 进行 State 的设置;  

long spinForTimeoutThreshold = 1000L;  //  线程自旋等待的时间;  

enq(node);   
插入一个 Node 到队列中;  

addWaiter(mode);   
为当前线程, 和指定模式, 创建并扩充一个等待队列;  

setHead(node);   
设置队列的头 Node  

unparkSuccessor(node);   
如果存在的话, 唤起 Node 持有的线程;  

doReleaseShared();   
共享模式下做释放锁的动作;  

cancelAcquire(node);   
取消正在进行的, Node 获取锁的尝试;  

shouldParkAfterFailedAcquire(pred, node)	;   
在尝试获取锁失败后, 是否应该禁用当前线程并等待;  

selfInterrupt();   
中断当前线程本身;  

parkAndCheckInterrupt()   
禁用当前线程进入等待状态, 并中断线程本身;  

acquireQueued(Node node, arg)   
队列中的线程获取锁;  

tryAcquire(arg)   
尝试获得锁(由AQS的子类实现它)  

tryRelease(arg)	   
尝试释放锁(由AQS的子类实现它)  

isHeldExclusively()   
是否独自持有锁;  

acquire(arg)   
获取锁;  

release(arg)   
释放锁;  

compareAndSetHead(update)   
利用 CAS 设置头 Node;  

compareAndSetTail(expect, update)   
利用 CAS 设置尾 Node;  

compareAndSetWaitStatus(node, expect, update)   
利用 CAS 设置某个 Node 中的等待状态;  

### 原理   

#### 非公平锁#加锁过程   
默认是非公平锁, NonfairSync;  
加锁, 等待锁的过程;  
1.0.. 加锁时, 根据 lock.state 进行判断, 如果 lock.state == 0, 代表当前没有任何线程持有该锁;  
1.1.. 调用 compareAndSetState 抢占锁, 抢占成功更新 lock.state = 1, 并设置 exclusiveOwnerThread = 当前线程;  
2.0.. 加锁时, 根据 lock.state 进行判断, 如果 lock.state != 0, 代表有线程持有该锁, 调用 acquire 方法, 申请锁;  
2.1.. acquire-tryAcquire-nonfairTryAcquire, 再判断, 如果 lock.state ==0, 则和 1.1 步骤一致, 并返回 true;  
2.2.. acquire-tryAcquire-nonfairTryAcquire, 再判断, 如果 lock.state !=0, 同时 exclusiveOwnerThread == current.Thread, 那么 lock.state 会执行 +1, 并返回 true;  
2.3.. 没有进入 2.1 和 2.2, 代表申请锁, 失败了, 也就是在 acquire 方法中, 开始执行 addWaiter-acquireQueued;  
3.0.. 在 addWaiter 方法中, 如果 tail 节点非空, 并且把当前线程当做尾节点, 并更新 tail 的指针, 如果操作成功, 返回;  
3.1.. 在 addWaiter  方法中, 如果 tail 节点为空, 或者没有成功的把, 当前线程设置成尾节点, 执行 enq 方法;  
3.2.. 在 enq 方法中, 进入死循环, 如果当前链表为空, 则执行初始化, 继续死循环;  
3.3.. 在 enq 方法中, 进入死循环, 如果当前链表非空, 则把当前线程添加到尾节点, 添加失败则继续死循环, 设置成功则返回, 执行 acquireQueued 方法;  
4.0.. 在 acquireQueued 方法中, 入口参数就是尾节点, 如果新节点的前驱是 head 节点, 再调用 tryAcquire 获取锁, 如果获取成功则更新 head 节点, 并返回;  
4.1.. 在 acquireQueued 方法中, 入口参数就是尾节点, 如果新节点的前驱不是 head 节点, 则调用 shouldParkAfterFailedAcquire 方法, 
ReentrantLock#lock  
ReentrantLock.NonfairSync#lock  
```
final void lock() {
    //  1.. 如果当前 lock.state = 0, 代表等待队列是空的, 当前也没有任何线程持有锁;  
    //  2.1.. 当前线程开始抢占锁, 如果抢占成功, 执行 setExclusiveOwnerThread 方法;  
    //  2.1.. 如果抢占失败, 执行 acquire;  
    if (compareAndSetState(0, 1))
        //  抢占成功, 添加标记, 设置当前线程持有锁;  
        setExclusiveOwnerThread(Thread.currentThread());
    else  
        //  将线程置于队列尾部排队;  
        acquire(1);  
}
```
lock()内部调用 acquire(1), 为何是"1"呢?  
首先我们知道 ReentrantLock 是独占锁, 1 表示的是锁的状态 state, 对于独占锁而言, 如果所处于可获取状态, 其状态为 0, 当锁初次被线程获取时状态变成 1;  

AbstractQueuedSynchronizer#acquire  
```
public final void acquire(int arg) {
    // 如果 tryAcquire 返回 true, 就不会执行 addWaiter-acquireQueued;  
    if (!tryAcquire(arg) &&  
        acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
        selfInterrupt();
}
```
tryAcquire 为 true, 表示成功获取锁,  
tryAcquire 为 false, 表示没有成功获取锁, 会把当前线程添加到阻塞队列, 排队获取锁;  

ReentrantLock.NonfairSync#tryAcquire  
ReentrantLock.Sync#nonfairTryAcquire  
```
final boolean nonfairTryAcquire(int acquires) {
    final Thread current = Thread.currentThread();
    //  获取同步状态  
    int c = getState();
    //  如果同步状态 c = 0, 说明没有线程占用当前锁, 此时可加锁  
    if (c == 0) {
        //  调用 CAS 加锁, 
        //  如果同步状态更新成功, 加锁成功;  
        // 如果失败, 则说明有其他线程在竞争获取锁;  
        if (compareAndSetState(0, acquires)) {
            //  设置当前线程为锁的持有线程;  
            setExclusiveOwnerThread(current);
            return true;
        }
    }
    //  如果当前线程已经持有锁, 此处条件为 true, 表明线程需再次获取锁, 也就是重入  
    else if (current == getExclusiveOwnerThread()) {
        //  计算重入后的同步状态值, acquires 一般为 1
        int nextc = c + acquires;
        if (nextc < 0) // overflow
            throw new Error("Maximum lock count exceeded");
        //  设置新的同步状态值, 也就是当前线程第几次获得了锁;    
        setState(nextc);
        return true;
    }
    return false;
}
```
如果当前状态为初始状态, 那么尝试设置状态;  
如果状态设置成功后就返回;  
如果状态被设置, 且获取锁的线程又是当前线程的时候, 进行状态的自增;  
如果未设置成功状态且当前线程不是获取锁的线程, 那么返回失败;  

AbstractQueuedSynchronizer#addWaiter  
```
private Node addWaiter(Node mode) {
    Node node = new Node(Thread.currentThread(), mode);
    // Try the fast path of enq; backup to full enq on failure
    Node pred = tail;
    // 如果队列不为空, 队尾指针就不为空;  
    //  更新尾节点为插入的数据, 并更新 prev 指向原尾节点;  
    if (pred != null) {
        node.prev = pred;
        //  在队列的尾部插入线程  
        if (compareAndSetTail(pred, node)) {
            pred.next = node;
            return node;
        }
    }
    //  队列为空, 初始化队列;  
    //  如果上一步CAS更新失败, 则执行插入当前节点;  
    enq(node);
    return node;
}
```
AbstractQueuedSynchronizer#enq  
```
private Node enq(final Node node) {
    for (;;) {
        Node t = tail;
        //  首次进入时, 队列为空, 先初始化;  
        if (t == null) { // Must initialize
            if (compareAndSetHead(new Node()))
                tail = head;
        } else {
            node.prev = t;
            if (compareAndSetTail(t, node)) {
                t.next = node;
                return t;
            }
        }
    }
}
```
AbstractQueuedSynchronizer#acquireQueued  
```
final boolean acquireQueued(final Node node, int arg) {
    //  设置入队是否获取锁成功标志;  
    boolean failed = true;
    try {
        boolean interrupted = false;
        for (;;) {
            //  获取节点的前驱;  
            final Node p = node.predecessor();
            //  如果 node 前驱节点恰好是 head;  
            //  那么就可以在次尝试获取一次锁;  
            //  队首节点很乐观, 因为确实很可能马上轮到他来获取锁;  
            if (p == head && tryAcquire(arg)) {
                //  很幸运的是, 它居然再一次的尝试成功获取了锁;  
                //  那 head 自然就要指到它的头上;  
                setHead(node);
                //  那个刚刚那个线程, 我帮你继续使用锁;  
                p.next = null; // help GC
                //  肯定是获取成功了
                failed = false;
                return interrupted;
            }
            //  不是 head 的后一个, 或者是 head 的后一个, 但是没有获取到锁;  
            //  获取锁失败了, 是否要阻塞这个线程;  
            //  是否挂起 ?  
            if (shouldParkAfterFailedAcquire(p, node) &&
                //  在这阻塞, 等待唤醒
                parkAndCheckInterrupt())
                interrupted = true;
        }
    } finally {
        //  发现之前哪里出现了异常, 就执行  
        if (failed)
            cancelAcquire(node);
    }
}
```
AbstractQueuedSynchronizer#shouldParkAfterFailedAcquire  
```
//  是否要阻塞该线程  
private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
    //  获取前驱的等待状态;  
    int ws = pred.waitStatus;
    //  如果线程前驱的等待状态为 -1, 即在执行状态, 那么就阻塞 node  
    if (ws == Node.SIGNAL)
        /*
         * This node has already set status asking a release
         * to signal it, so it can safely park.
         */
        return true;
    //  如果线程前驱的等待状态大于 0, 即是 1 CANCELLED 就把前面的等待状态为 1 的删了, 删到直到不为 1;  
    if (ws > 0) {
        /*
         * Predecessor was cancelled. Skip over predecessors and
         * indicate retry.
         */
        do {
            node.prev = pred = pred.prev;
        } while (pred.waitStatus > 0);
        pred.next = node;
    } else {
         // ws 只有 1, 0, -1, -2, -3
        // 1, -1处理了, 前面没有对 waitStatus 做操作
        // 那么只剩 0 了, 前驱的节点的 ws 设置为 -1  
        // 在之前图表示的时候, 为 -1 是执行状态, 但这种状态是阻塞状态;  
        // 直到前一个线程释放了锁, 才能使执行状态
        /*
         * waitStatus must be 0 or PROPAGATE.  Indicate that we
         * need a signal, but don't park yet.  Caller will need to
         * retry to make sure it cannot acquire before parking.
         */
        compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
    }
    return false;
}
```
AbstractQueuedSynchronizer#parkAndCheckInterrupt  
```
private final boolean parkAndCheckInterrupt() {
    LockSupport.park(this);
    return Thread.interrupted();
}
```
LockSupport.park(this) 会挂起当前线程, 但是 LockSupport.park 还有一个隐藏功能. 就是, 如果先对一个线程 unpark, 再对这个线程 park,  
那么这次的 park 是失效的, 下一次 park 才会挂起;  
原因就是, 对一个没有被 park 的线程进行 unpark 的时候, 会把标志位 perm 置为 1;  
而每次 park 的操作, 都是先去检查 perm 是否为 1, 
如果是 1, 那么置为 0, 并且这次不挂起;  
如果是 0, 那么就直接挂起这个线程;  
如果线程被阻塞过, 返回 true;  

#### 非公平锁#释放锁过程   
非公平锁, 释放锁的过程;  
ReentrantLock#unlock  
AbstractQueuedSynchronizer#release  
```
public final boolean release(int arg) {
    //  尝试释放锁;  
    if (tryRelease(arg)) {
        //  只有一层 lock unlock结构, 释放锁成功后, 或者独占锁完全释放, 其实就是这个线程把所有的 lock 都 unlock了; 
        Node h = head;
        //  唤醒阻塞队列中的下一个等待的线程;  
        //  头结点不为空, 且不为等待状态;  
        if (h != null && h.waitStatus != 0)
            //  用 Unsafe.unpack 方法, 使当前线程可用, 此线程在 lock 时调用过 parkAndCheckInterrupt()方法进行 pack 处理;  
            unparkSuccessor(h);
        return true;
    }
    return false;
}
```
ReentrantLock.Sync#tryRelease  
```
//  公平锁和非公平锁尝试释放锁;  
protected final boolean tryRelease(int releases) {
    int c = getState() - releases;
    //   如果当前要释放锁的线程, 不是上次加锁的线程抛出异常;  
    if (Thread.currentThread() != getExclusiveOwnerThread())
        throw new IllegalMonitorStateException();
    // 用于标记是否可以完全释放锁;  
    boolean free = false;
    //  如果同步状态为 0, 说明锁完全释放;  
    //  是重入锁, 这也是释放的最后的一把锁了;  
    //  不是重入锁, 就直接释放了;  
    if (c == 0) {
        //  表示完全释放;  
        free = true;
        //  置空持有当前锁的, 当前线程;  
        setExclusiveOwnerThread(null);
    }
    //  减少同步状态, 减到 0 释放锁;  
    //  不是最后一层锁, 就释放一层;  
    setState(c);
    return free;
}
```
如果该锁被获取了 n 次, 那么前(n-1)次 tryRelease(int releases)方 法必须返回 false, 而只有同步状态完全释放了, 才能返回 true;  
可以看到, 该方法将同步状态是否为 0 作为最终释放的条件, 当同步状态为 0 时, 将占有线程设置为 null, 并返回true, 表示释放成功;  

AbstractQueuedSynchronizer#unparkSuccessor  
```
//  唤醒 node
private void unparkSuccessor(Node node) {
    //  获取等待状态
    int ws = node.waitStatus;
    //  如果 ws < 0, 就将其变为 0
    if (ws < 0)
        compareAndSetWaitStatus(node, ws, 0);
    /  获取 head 的后继节点
    Node s = node.next;
    //  如果 head 的后继节点不存在, 或者 ws 为 1;  
    if (s == null || s.waitStatus > 0) {
        s = null;
        for (Node t = tail; t != null && t != node; t = t.prev)
            //  就从队列的后面往前面找, 找到最前面一个 ws < 0 的, 但是又不是 head 的节点;  
            if (t.waitStatus <= 0)
                s = t;
    }
    //  就把该节点唤醒;  
    if (s != null)
        LockSupport.unpark(s.thread);
}
```
#### 公平锁#加锁过程  
ReentrantLock#lock  
ReentrantLock.FairSync#lock  
```
final void lock() {
    acquire(1);
}
```

AbstractQueuedSynchronizer#acquire  
ReentrantLock.FairSync#tryAcquire  
```
protected final boolean tryAcquire(int acquires) {
    final Thread current = Thread.currentThread();
    int c = getState();
    if (c == 0) {
        //  同步状态更新成功;  
        //  判断同步队列中当前节点是否有前驱节点, 
        //  如果有说明有线程更早的请求锁, 因此需要等待前驱节点线程获取锁, 并释放锁之后, 当前线程才能继续获取锁;  
        if (!hasQueuedPredecessors() &&
            compareAndSetState(0, acquires)) {
            //  添加标记, 设置当前线程持有锁;  
            setExclusiveOwnerThread(current);
            return true;
        }
    }
    //  这个锁被人占了, 确认一下是不是自己现在占着这个锁  
    //  是自己占着这个锁, 且 c!=0了, 也就是重入了;  
    else if (current == getExclusiveOwnerThread()) {
        int nextc = c + acquires;
        //  这个线程占有这个锁的次数太多了, 使 int 溢出了, 抛出异常
        if (nextc < 0)
            throw new Error("Maximum lock count exceeded");
        //  设置新的同步状态值, 也就是当前线程第几次获得了锁;    
        setState(nextc);
        return true;
    }
    return false;
}
```
公平锁获取过程, 仅加入了当前线程(Node)之前是否有前置节点在等待的判断, 也就是说当前面没有人排在该节点(Node)前面时候队且能够设置成功状态, 才能够获取锁;  
如果锁未被任何线程持有, 则立即返回且设置持有数=1;  
如果锁被当前线程已持, 则立即返回, 且持有数+1;  
如果锁被其他线程持有, 则当前线程进入等待队列;  
 
#### 公平锁#释放锁过程  
ReentrantLock#unlock  
AbstractQueuedSynchronizer#release  
```
public final boolean release(int arg) {
    if (tryRelease(arg)) {
        Node h = head;
        if (h != null && h.waitStatus != 0)
            unparkSuccessor(h);
        return true;
    }
    return false;
}
```

### 参考  
https://my.oschina.net/u/566591/blog/1557978  
https://www.cnblogs.com/xrq730/p/4979021.html  
https://juejin.im/entry/5c516476f265da61736aab3e  
https://www.cnblogs.com/-new/p/7256297.html  
性能比较  
https://blog.csdn.net/fofabu2/article/details/78983767  

原理  
https://blog.csdn.net/fuyuwei2015/article/details/72583010  
https://blog.csdn.net/WeiJiFeng_/article/details/81390935  
https://www.cnblogs.com/xrq730/p/4979021.html  
https://blog.csdn.net/ios99999/article/details/76977666  
https://blog.csdn.net/hotpots/article/details/78148592  
https://blog.csdn.net/kangxiongwei/article/details/72469434  
http://www.cnblogs.com/skywang12345/p/3496147.html  
http://www.cnblogs.com/maypattis/p/6403682.html  
https://blog.csdn.net/yanyan19880509/article/details/52345422  
https://blog.csdn.net/Luxia_24/article/details/52403033  
https://blog.csdn.net/gao__xue/article/details/79950448  
https://blog.csdn.net/javazejian/article/details/75043422  
https://blog.csdn.net/lsgqjh/article/details/63685058  
https://blog.csdn.net/u010942020/article/details/73310898  
https://javadoop.com/2017/06/16/AbstractQueuedSynchronizer/  
https://blog.csdn.net/javazejian/article/details/72828483  
http://www.liuhaihua.cn/archives/518637.html  
https://blog.csdn.net/liyantianmin/article/details/54673109  

锁的分类  
https://blog.csdn.net/qq_41931837/article/details/82314478  
