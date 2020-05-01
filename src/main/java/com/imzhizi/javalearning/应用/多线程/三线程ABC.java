package com.imzhizi.javalearning.应用.多线程;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * created by zhizi
 * on 4/2/20 07:57
 * [华为和阿里都考过的多线程编程题，你会吗？多线程交替打印 ABC的多种实现方法 - 个人文章 - SegmentFault 思否](https://segmentfault.com/a/1190000021433079 )
 */
public class 三线程ABC {
    /**
     * 基于轮询，个人测试没发现问题，不知道是否符合要求
     */
    static class Volatile轮询 {
        private volatile static int last = 3;

        public static void main(String[] args) {
            new Thread(() -> {
                int i = 0;
                while (i < 100) {
                    if (last == 3) {
                        System.out.print('A');
                        last = 1;
                        i++;
                    }
                }
            }).start();

            new Thread(() -> {
                int i = 0;
                while (i < 100) {
                    if (last == 1) {
                        System.out.print('B');
                        last = 2;
                        i++;
                    }
                }
            }).start();

            new Thread(() -> {
                int i = 0;
                while (i < 100) {
                    if (last == 2) {
                        System.out.print('C');
                        last = 3;
                        i++;
                    }
                }
            }).start();
        }
    }

    static class 原子类 {
        private static final AtomicInteger val = new AtomicInteger(1);

        public static void main(String[] args) {
            new Thread(() -> {
                int i = 0;
                while (i < 100) {
                    while (val.get() != 1) ;
                    System.out.print('A');
                    val.set(2);
                    i++;

                }
            }).start();

            new Thread(() -> {
                int i = 0;
                while (i < 100) {
                    while (val.get() != 2) ;
                    System.out.print('B');
                    val.set(3);
                    i++;
                }
            }).start();

            new Thread(() -> {
                int i = 0;
                while (i < 100) {
                    while (val.get() != 3) ;
                    System.out.print('C');
                    val.set(1);
                    i++;
                }
            }).start();
        }
    }

    /**
     * ReentrantLock 可以替代synchronized进行同步
     * ReentrantLock 获取锁更安全
     * 必须先获取到锁，再进入try {...}代码块，最后使用finally保证释放锁
     * 可以使用tryLock()尝试获取锁
     * <p>
     * synchronized可以配合对象的监视器，实现 wait 和 notify，让线程在条件不满足时等待
     * <p>
     * 在 ReentrantLock 中，使用Condition代替对象监视器，完成类似的操作
     * 用await()替换wait()，用signal()替换notify()，用signalAll()替换notifyAll()
     * Condition 是被绑定到 Lock 上的，要创建一个 Lock 的 Condition 必须用 newCondition() 方法
     * Condition 的强大之处在于它可以为多个线程建立不同的 Condition
     * <p>
     * 但是await之后不唤醒，会陷入永久等待
     */
    static class 重入锁 {
        private static final Lock lock = new ReentrantLock();
        private static final Condition isA = lock.newCondition();
        private static final Condition isB = lock.newCondition();
        private static final Condition isC = lock.newCondition();

        public static void main(String[] args) {
            new Thread(() -> {
                lock.lock();
                for (int i = 0; i < 10; i++) {
                    System.out.print('A');
                    isB.signal();
                    try {
                        isA.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                isB.signal();
                lock.unlock();
            }).start();

            new Thread(() -> {
                lock.lock();
                for (int i = 0; i < 10; i++) {
                    System.out.print('B');
                    isC.signal();
                    try {
                        isB.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                isC.signal();
                lock.unlock();
            }).start();

            new Thread(() -> {
                lock.lock();
                for (int i = 0; i < 10; i++) {
                    System.out.print('C');
                    isA.signal();
                    try {
                        isC.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                isA.signal();
                lock.unlock();
            }).start();
        }
    }

    /**
     * 最大的问题在于，每次都需要 notifyAll，然后其中的非目标线程都会被唤醒然后迅速进入阻塞
     * 因为 synchronized 三个线程想要形成阻塞，必须要共同争夺一个资源，如果因为一个资源而阻塞，则又无法唤醒特定某一个
     */
    static class 同步锁 {
        private static volatile int state = 0;
        private static final Object lock = new Object();

        public static void main(String[] args) {
            new Thread(() -> {
                int i = 0;
                while (i < 10) {
                    synchronized (lock) {
                        while (state % 3 != 0) {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        i++;
                        state++;
                        System.out.print("A");
                        lock.notifyAll();
                    }
                }
            }).start();
            new Thread(() -> {
                int i = 0;
                while (i < 10) {
                    synchronized (lock) {
                        while (state % 3 != 1) {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        i++;
                        state++;
                        System.out.print("B");
                        lock.notifyAll();
                    }
                }
            }).start();
            new Thread(() -> {
                int i = 0;
                while (i < 10) {
                    synchronized (lock) {
                        while (state % 3 != 2) {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        i++;
                        state++;
                        System.out.print("C");
                        lock.notifyAll();
                    }
                }
            }).start();
        }
    }

    /**
     * 线程控制实现
     * 通过park、unPark来阻塞、唤醒线程
     */
    static class LockSupport实现 {
        static Thread threadA, threadB, threadC;

        public static void main(String[] args) {
            threadA = new Thread(() -> {
                for (int i = 0; i < 10; i++) {
                    // 打印当前线程名称
                    System.out.print("A");
                    // 唤醒下一个线程
                    LockSupport.unpark(threadB);
                    // 当前线程阻塞
                    LockSupport.park();
                }
            }, "A");
            threadB = new Thread(() -> {
                for (int i = 0; i < 10; i++) {
                    // 先阻塞等待被唤醒
                    LockSupport.park();
                    System.out.print(Thread.currentThread().getName());
                    // 唤醒下一个线程
                    LockSupport.unpark(threadC);
                }
            }, "B");
            threadC = new Thread(() -> {
                for (int i = 0; i < 10; i++) {
                    // 先阻塞等待被唤醒
                    LockSupport.park();
                    System.out.print(Thread.currentThread().getName());
                    // 唤醒下一个线程
                    LockSupport.unpark(threadA);
                }
            }, "C");
            threadA.start();
            threadB.start();
            threadC.start();
        }
    }

    /**
     * 使用信号量实现
     * semaphore 中文意思是信号量，原本是操作系统中的概念，JUC下也有个 Semaphore 的类，可用于控制并发线程的数量。
     * Semaphore 的构造方法有个 int 类型的 permits 参数，指的是该 Semaphore 对象可分配的许可数
     * 一个线程中的 Semaphore 对象调用 acquire() 方法可以让线程获取许可继续运行，同时该对象的许可数减一，如果当前没有可用许可，线程会阻塞
     * 该 Semaphore 对象调用 release() 方法可以释放许可，同时其许可数加一，似乎会直接唤醒
     */
    static class 信号量 {
        public static void main(String[] args) throws InterruptedException {
            // 初始化许可数为1，A线程可以先执行
            Semaphore semaphoreA = new Semaphore(1);
            // 初始化许可数为0，B线程阻塞
            Semaphore semaphoreB = new Semaphore(0);
            // 初始化许可数为0，C线程阻塞
            Semaphore semaphoreC = new Semaphore(0);


            new Thread(() -> {
                for (int i = 0; i < 10; i++) {
                    try {
                        // A线程获得许可，同时semaphoreA的许可数减为0,进入下一次循环时
                        // A线程会阻塞，知道其他线程执行semaphoreA.release();
                        semaphoreA.acquire();
                        // 打印当前线程名称
                        System.out.print(Thread.currentThread().getName());
                        // semaphoreB许可数加1
                        semaphoreB.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "A").start();
            new Thread(() -> {
                for (int i = 0; i < 10; i++) {
                    try {
                        semaphoreB.acquire();
                        System.out.print(Thread.currentThread().getName());
                        semaphoreC.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "B").start();
            new Thread(() -> {
                for (int i = 0; i < 10; i++) {
                    try {
                        semaphoreC.acquire();
                        System.out.print(Thread.currentThread().getName());
                        semaphoreA.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "C").start();
        }
    }

    /**
     * 错误示范
     */

    /**
     * 最大的问题在于如果值没有及时写回主存会导致死循环
     * 不使用赋值形式会让问题暴露的更明显
     * 内存的写入读取都非常随机
     */
    static class 无操作 {
        private static int val = 3;

        public static void main(String[] args) {
            new Thread(() -> {
                int i = 0;
                while (i < 100) {
                    while (val % 3 == 1) ;
                    System.out.print('A');
                    val++;
                    i++;

                }
            }).start();

            new Thread(() -> {
                int i = 0;
                while (i < 100) {
                    while (val % 3 == 2) ;
                    System.out.print('B');
                    val++;
                    i++;
                }
            }).start();

            new Thread(() -> {
                int i = 0;
                while (i < 100) {
                    while (val % 3 == 0) ;
                    System.out.print('C');
                    val++;
                    i++;
                }
            }).start();
        }
    }

    /**
     * volatile 错误示范，使用了 ++ 操作，因为 ++ 操作不是原子操作
     * 这也展现了volatile不能保证原子性的特点
     */
    static class Volatile {
        private volatile static int val = 1;

        public static void main(String[] args) {
            new Thread(() -> {
                int i = 0;
                while (i < 100) {
                    while (val % 3 == 1) ;
                    System.out.print('A');
                    val++;
                    i++;

                }
            }).start();

            new Thread(() -> {
                int i = 0;
                while (i < 100) {
                    while (val % 3 == 2) ;
                    System.out.print('B');
                    val++;
                    i++;
                }
            }).start();

            new Thread(() -> {
                int i = 0;
                while (i < 100) {
                    while (val % 3 == 0) ;
                    System.out.print('C');
                    val++;
                    i++;
                }
            }).start();
        }
    }
}
