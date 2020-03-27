package com.imzhizi.javalearning.语言基础;

import org.junit.Test;

import java.util.concurrent.*;

/**
 * created by zhizi
 * on 3/24/20 20:37
 */
public class r并发基本概念 {
    /**
     * ### 新建线程的三种方式
     * 通过继承Thread类，重写run方法
     * 通过实现 runnable 接口, 事实上Thread 也是实现了 runnable 接口, thread 的 run 方法也来自于 runnable
     * 通过实现callable接口, callable 强制要求返回值, 需要借助 ExecutorService 和 Future 包裹的异步结果
     * ExecutorService service = Executors.newSingleThreadExecutor();
     * service.submit(callable), 将callable 转换成 FutureTask 在未来进行异步执行
     */
    @Test
    public void 新建线程() throws ExecutionException, InterruptedException {
        // 方法一
        Thread thread1 = new Thread() {
            @Override
            public void run() {
                System.out.println("事实上是继承 Thread 来创建线程");
                super.run();
            }
        };
        thread1.start();

        // 方法二
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("事实上是通过实现 runnable 接口来创建线程");
            }
        });
        thread2.start();

        new Thread(() -> {
            System.out.println("借助lambda表达式，对于只有一个方法的接口，接口的实现可以变得非常简略");
        }).start();

        new Thread(() -> System.out.println("若仅执行一行代码，还可以更加简略")).start();

        // 方法三
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<String> future = service.submit(new Callable() {
            @Override
            public String call() throws Exception {
                return "通过实现 Callable 接口";
            }
        });
        System.out.println(future.get());
        Future<String> future2 = service.submit(() -> {
            String str = "future";
            return str + "3";
        });
        System.out.println(future2.get());

        Future<String> future3 = service.submit(() -> "future3 with lambda");
        System.out.println(future3.get());
    }

    static class 死锁展示 {
        public static void main(String[] args) {
            Integer resource1 = 1;
            Integer resource2 = 2;

            new Thread(() -> {
                // 在thread1中
                synchronized (resource1) {
                    try {
                        System.out.println("start r1");
                        Thread.sleep(3000);
                        synchronized (resource2) {
                            System.out.println(Thread.currentThread() + "get r2");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            new Thread(() -> {
                // 在thread2中
                synchronized (resource2) {
                    try {
                        System.out.println("start r2");
                        synchronized (resource1) {
                            System.out.println(Thread.currentThread() + "get r2");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }

    /**
     * interrupted
     * 中断可以理解为线程的一个标志位，它表示了一个运行中的线程是否被其他线程进行了中断操作
     * 其他线程可以调用该线程的interrupt()方法对其进行中断操作, 但这个中断似乎并不会带来真正的中断
     * 所以仅仅是改变了该线程中的一个值
     * 该线程可以调用 isInterrupted()来感知其他线程对其自身的中断操作, 感知到之后实际上还是自行了解
     * 相对于武断的直接只结束线程, 这样才有机会去清理资源, 安排后事
     * <p>
     * ### sleep/wait 遭遇 interrupt
     * 仅仅会抛出 InterruptedException 异常，但仍然会继续执行
     */
    static class 中断 {
        public static void main(String[] args) {
            Thread thread1 = new Thread(() -> {
                while (!Thread.interrupted()) {
                    System.out.print("*");
                }
                System.out.println("\ni was killed");
            });

            Thread thread2 = new Thread(() -> {
                System.out.println("\nready to go");
                thread1.interrupt();
                System.out.println("\nis thread1 dead?");
            });

            thread1.start();
            thread2.start();
        }
    }

    static class 中断2 {
        public static void main(String[] args) {
            Thread thread1 = new Thread(() -> {
                while (true) {
                    System.out.print("*");
                }
            });

            Thread thread2 = new Thread(() -> {
                System.out.println("\nready to go");
                thread1.interrupt();
                System.out.println("\nis thread1 dead?");
            });

            thread1.start();
            thread2.start();
        }
    }

    /**
     * join
     * 从效果上来说，在线程B中执行线程 A.join()，线程 B 会陷入阻塞，一直到线程A终结才会开始执行
     * 事实上它的原理就是监控其状态，如果存活则一直阻塞（为什么说是阻塞，因为可以自行恢复）
     * while (isAlive()) {wait(0);}
     * join还支持参数，表示最多等多久，如果时间到了之后线程A仍未终结也会恢复
     * <p>
     * ### note
     * join 内部使用的 wait，意味着会释放所有锁？锁是在何处获得的呢？
     * 仔细看发现 join() 方法实际上是被 synchronized 关键字修饰的
     */
    static class Join {
        public static void main(String[] args) {
            Thread previousThread = new Thread(() -> {
                System.out.println("first to go");
            });

            for (int i = 0; i < 10; i++) {
                previousThread.start();
                Thread finalPreviousThread = previousThread;
                previousThread = new Thread(() -> {
                    try {
                        finalPreviousThread.join();
                        System.out.println(finalPreviousThread.getName() + " is Terminated");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }

            previousThread.start();
        }
    }


    /**
     * sleep 是 Thread 的静态方法
     * 作用是让当前线程按照休眠指定的时间，是一个本地方法，所以具体精度取决于处理器
     * 如果当前线程获得了锁，使用sleep是不会失去锁的
     * sleep 意味着让出当前 CPU 资源
     */
    @Test
    public void 休眠() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("first to go");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * wait 是 Object 的实例方法
     * 可以说任何对象都拥有 wait 方法，wait 方法可以传参，就意味着进入 timed_waiting 两种状态，此状况和 sleep 类似
     * wait()方法必须要在同步方法或者同步块中调用，也就是必须已经获得对象锁
     * 一旦调用 wait 方法，当前对象的同步锁就会被释放，并使线程进入 waiting 状态，搁置在线程池中
     * <p>
     * ### 只有三种方法可以打破 waiting 状态
     * 1. 其他线程调用了 notify 方法，而当前线程被随机？任意地选中？后可以唤醒
     * 2. 其他线程调用了 notifyAll 方法
     * 3. 其他线程调用了该线程的 interrupt 方法（为什么呢？感觉 interrupt 没什么实际作用）
     * <p>
     * ### note
     * 一旦线程被唤醒，会重新竞争对象的同步锁，一旦竞争到了锁，然后就会恢复到之前的状态
     * 恢复到之前的状态，指的就是，竞争时线程仍处于到 waiting 状态，知道竞争到锁之后线程才会从 wait 方法中返回，线程才算恢复
     * 实际上并不是释放了所有的锁，而是释放了当前锁的对象，这个线程中其他加锁的对象不会被释放
     * 同时只有拥有该对象的 monitor 的线程才能调用对象的 wait 方法，notify 中也是先拥有了对象的 monitor，然后再唤醒它
     * <p>
     * ### sleep 和 wait 的区别
     * 两者都会暂停线程的执行，出让CPU资源
     * 最主要的区别是, sleep不会释放锁, wait会释放锁，wait往往用于线程间交互/通信, 而sleep用于暂停执行
     * wait使用后(waiting), 线程不会自行苏醒, 需要调用同一个对象上的notify/notifyAll方法唤醒
     * sleep使用后(timed-waiting), 线程会自动苏醒
     */
    @Test
    public void 等待() {

    }

    /**
     * yield 是一个静态方法，一旦执行，它会是当前线程让出CPU
     * 让出的CPU并不是代表当前线程不再运行，而是会进入下一轮竞争，让出的时间片只会分配给当前线程相同优先级的线程
     * 所以 yield 可以想象成一个瞬间 sleep，sleep 之后迅速恢复，不同点在于 yield 出让的资源是有要求的，也就是优先级要求
     * <p>
     * ### 关于优先级
     * Java程序中，通过一个整型成员变量Priority来控制优先级，优先级的范围从1~10
     * 构建线程的时候可以通过**setPriority(int)**方法进行设置，默认优先级为5
     * 优先级高的线程相较于优先级低的线程优先获得处理器时间片
     * 在不同JVM以及操作系统上，线程规划策略是不同的，不能一概而论
     * <p>
     * ### note
     * 代码的注释中提到，很少会有什么场合适合使用 yield，可能是 debug 时为了重现 bug
     * 还可以用来编写并发控制相关的功能，所以在 java.util.concurrent.locks 中被使用
     */
    static class Yield {
        public static void main(String[] args) {
            Thread thread1 = new Thread(() -> {
                System.out.println("释放");
                Thread.yield();
                System.out.println("我还能回来");
            });

            Thread thread2 = new Thread(() -> {
                System.out.println("我能抢到吗");
            });

            Thread thread3 = new Thread(() -> {
                System.out.println("我优先级高呀");
            });
            thread1.setPriority(8);
            thread2.setPriority(1);
            thread3.setPriority(8);
            thread2.start();
            thread3.start();
            thread1.start();
        }
    }

    /**
     * 守护线程是一种特殊的线程，就和它的名字一样，它是系统的守护者，在后台默默地守护一些系统服务
     * 比如垃圾回收线程，JIT线程就可以理解为守护线程
     * 用户线程完全结束后就意味着整个系统的业务任务全部结束，系统就没有对象需要守护，当一个Java应用只有守护线程的时候，虚拟机就会自然退出
     * 所以把一个线程标记为守护线程意味着主线程结束时会随之结束，而不像用户线程继续执行
     */
    static class 用户线程 {
        public static void main(String[] args) {
            new Thread(() -> {
                try {
                    for (int i = 0; i < 5; i++) {
                        Thread.sleep(500);
                        System.out.println("我优先级高呀");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

            System.out.println("我好了");
        }
    }

    static class 守护线程 {
        public static void main(String[] args) throws InterruptedException {
            Thread thread = new Thread(() -> {
                try {
                    for (int i = 0; i < 5; i++) {
                        Thread.sleep(500);
                        System.out.println("我优先级高呀");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            thread.setDaemon(true);
            thread.start();
            Thread.sleep(1000);
            System.out.println("我好了");
        }
    }

}
