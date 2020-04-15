package com.imzhizi.javalearning.语言基础;

import org.junit.Test;

import java.util.concurrent.*;
import java.util.concurrent.locks.*;

/**
 * created by zhizi
 * on 4/17/20 09:05
 */
public class r并发组件 {

    /**
     * Java 中定义了一个名为 AbstractQueuedSynchronizer 的抽象类
     * 它本身不可以直接被使用，但却是 ReentrantLock、Semaphore 等并发组件的核心依赖
     * AbstractQueuedSynchronizer 被称为队列同步器，是构建锁和其他同步组件的基础框架
     * 同步器自身没有实现任何同步接口，它仅仅是定义了若干同步状态的获取和释放方法来供自定义同步组件的使用
     * 如果说锁是面向使用者，它定义了使用者与锁交互的接口，隐藏了实现细节；
     * 那么同步器是面向锁的实现者，它简化了锁的实现方式，屏蔽了同步状态的管理，线程的排队，等待和唤醒等底层操作
     * 同步器既支持独占式获取同步状态，也可以支持共享式获取同步状态，便于实现不同类型的锁
     * <p>
     * 首先，作为一个队列，它的数据结构必然是数组或者是链表，究竟是什么可以通过源码确认
     * 可以看到 AQS 结点的数据结构为 Node
     * 它包含 Node 类型的 mode-marker SHARED和EXCLUSIVE、前后向指针prev和next(双向链表)
     * 以上的相对比较容易理解，以下的就需要再看看
     * 包含整型变量 cancelled、signal、condition、propagate，值分别是1～-3，这四个值都用来改变 waitStatus
     * - 其中cancelled 表示该线程已取消
     * - signal 表示后继线程需要 unparking(取消park状态，也就是需要唤醒)
     * - condition 表示该线程正在等待 condition，相当于陷入 condition 的 await状态？
     * - propagate(传播) 注释说下一个「共享获取」应该无条件传播，不太能理解
     * 还有 nextWaiter 和 thread, 下一个等待者和当前结点所对应的线程
     * <p>
     * 再看AQS自身的数据结构
     * Node 类型的 head、tail，形成双向链表、双头队列
     * 表示锁状态的 state 和持有当前锁的线程 ownThread
     * 因此大概可以想像一个链表的结构，通过 head 和 tail 始终指向链表的头部和尾部
     * 每个Node都和一个线程绑定，如果获取锁失败就实例化一个Node放在队列的尾部
     * 每个Node都拥有一个waitStatus， 用来表示Node的状态
     */
    @Test
    public void NodeTest() {
        Lock lock = new ReentrantLock();
        for (int i = 0; i < 8; i++) {
            Thread t = new Thread(() -> {
                lock.lock();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("睡醒了");
                    lock.unlock();
                }
            });
            t.start();
        }
    }


    /**
     * 如果多个线程都存在于同步队列中，即便尝试 unpark 后面的线程
     * 也只是会再次判断自己能否获取锁，然后再次park
     */
    static class 公平独占重入锁 {
        private static final Lock lock = new ReentrantLock(true);

        public static void main(String[] args) {
            Thread t1 = new Thread(() -> {
                System.out.println(Thread.currentThread().getName());
                lock.lock();
                try {
                    Thread.sleep(3000);
                    System.out.println(Thread.currentThread().getName() + " unlock");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            });
            Thread t2 = new Thread(() -> {
                System.out.println(Thread.currentThread().getName());
                lock.lock();
                try {
                    Thread.sleep(3000);
                    System.out.println(Thread.currentThread().getName() + " unlock");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            });
            Thread t3 = new Thread(() -> {
                System.out.println(Thread.currentThread().getName());
                lock.lock();
                try {
                    Thread.sleep(2000);
                    System.out.println(Thread.currentThread().getName() + " unlock");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            });
            Thread t4 = new Thread(() -> {
                System.out.println(Thread.currentThread().getName());
                lock.lock();
                try {
                    Thread.sleep(2000);
                    System.out.println(Thread.currentThread().getName() + " unlock");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            });

            t1.start();
            t2.start();
            t4.start();
            t3.start();

            LockSupport.unpark(t3);
            System.out.println("unpark t3");
        }
    }

    /**
     * 在AQS 中，ConditionObject implements Condition
     */
    @Test
    public void 条件队列() throws InterruptedException {
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        condition.await();
        condition.signal();
    }

    /**
     * 基于重入锁的读写锁 - 共享锁
     */
    static class 重入读写锁 {
        private static final ReadWriteLock lock = new ReentrantReadWriteLock();
        private static volatile String msg = "null";

        public static void main(String[] args) {
            for (int i = 1; i < 9; i++) {
                if (i % 4 == 0) {
                    new Thread(() -> {
                        lock.writeLock().lock();
                        try {
                            msg = Thread.currentThread().getName();
                            System.out.println("write msg:" + msg);
                        } finally {
                            lock.writeLock().unlock();
                        }
                    }, "writer-" + i).start();
                } else {
                    new Thread(() -> {
                        lock.readLock().lock();
                        try {
                            System.out.println(msg);
                        } finally {
                            lock.readLock().unlock();
                        }

                    }, "reader-" + i).start();
                }
            }
        }

    }


    /**
     * CountDownLatch 的思路是 await() 时如果同步变量不为0就阻塞自身，而同步变量减到0会唤醒所有被阻塞的线程
     * 简单的设想是在 countDown() 将同步变量缩减为0时唤醒阻塞队列中的所有结点
     * 实际上并没有那么简单，首先latch基于AQS的共享锁实现，所以线程的唤醒也基于共享锁的唤醒
     * AQS如何唤醒共享锁呢——使用了一种名为「调用风暴」的机制
     */
    static class 倒数锁 {
        private static final CountDownLatch latch = new CountDownLatch(3);

        public static void main(String[] args) {
            new Thread(() -> {
                try {
                    latch.await();
                    System.out.println("time to me?");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

            for (int i = 0; i < 6; i++) {
                int finalI = i;
                new Thread(() -> {
                    latch.countDown();
                    System.out.println(finalI + " is reading, " + latch.getCount() + " is waiting");
                }).start();
            }
        }
    }

    @Test
    public void 循环栅栏() throws BrokenBarrierException, InterruptedException {
        CyclicBarrier barrier = new CyclicBarrier(6);
        barrier.await();
    }

    @Test
    public void 信号量() throws InterruptedException {
        Semaphore semaphore = new Semaphore(2);
        semaphore.acquire();
        semaphore.release();
    }

    /**
     * 数据结构
     * table[] 装载Node数组
     * nextTable[] 要启用的下一个table，只有在扩容的时候 non-null
     * baseCount：base的计数器，主要在没有竞争的时候使用
     * sizeCtl - 用于table的初始化和扩容
     * - 未初始化时,sizeCtl表示初始容量, 初始化后表示扩容的阈值,为当前数组长度length*0.75
     * - -1：table正在初始化_(initTable()中CAS更改)_
     * - 小于-1代表 -(扩容线程数+1)
     * transferIndex：还不理解
     * cellsBusy：扩容和创建 counterCells 时的自旋锁
     * counterCells： counterCell 数组，长度为2的幂
     * <p>
     * Node 的数据结构 hash、key、val、next
     * ForwardingNode 继承自Node，是执行扩容操作时被插在桶的头部的结点
     * 增加了一个成员变量 nextTable(Node[])
     * <p>
     * resizeStamp
     * <p>
     * index 的计算方法为 hash&(n-1)，hash = h^(h>>>16) & 0x7fffffff
     * 这个 0x7fffffff 有什么用之后才能看到
     * <p>
     * 扩容方法是 transfer
     */
    @Test
    public void 并发哈希表() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
    }
}
