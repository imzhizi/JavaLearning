package com.imzhizi.javalearning.语言基础;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * created by zhizi
 * on 3/26/20 10:10
 */
public class r并发编程 {
    /**
     * 不稳定的结果
     * 为什么会出现这样的结果，也就是虽然JMM允诺了 happens-before 的原则，但实际执行上还是有问题
     * 具体来说就是线程都从主内存中，或者说从堆内存中读取了 count 的值拿回去开始算，大家算的过程中相继写回，互相覆盖导致结果无法预计
     * <p>
     * ### 如何解决这个问题？
     * 比如说使用 synchronized，线程逐个调用变量
     */
    @Test
    public void 不稳定的Count() {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (int j = 0; j < 100000; j++) {
                    Count.count++;
                }
            }).start();
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("count: " + Count.count);
    }

    @Test
    public void 同步Count() {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                synchronized (Count.class) {
                    for (int j = 0; j < 100000; j++) {
                        Count.count++;
                    }
                }
            }).start();
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("count: " + Count.count);
    }

    @Test
    public void 同步Count2() {
        Count2 count2 = new Count2();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                synchronized (count2) {
                    for (int j = 0; j < 100000; j++) {
                        count2.count++;
                    }
                }
            }).start();
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("count: " + count2.count);
    }

    /**
     * 使用 JDK 提供的原子类 AtomicInteger，
     */
    @Test
    public void CASCount() {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (int j = 0; j < 100000; j++) {
                    AtomicCount.count.incrementAndGet();
                }
            }).start();
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("count: " + AtomicCount.count);
    }

    static class Count {
        static int count = 0;
    }

    static class AtomicCount {
        static AtomicInteger count = new AtomicInteger();
    }

    static class Count2 {
        Integer count = 0;
    }

    /**
     * 默认情况，线程真的会傻傻觉得自己所持有的变量一直都是最新版
     * 根本搞不清楚外面发生了什么
     * 加了 volatile 之后，变量的变化成功刷新了工作内存中的值
     * <p>
     * ### special case
     * 如果在循环中加入了输出语句，这时发现线程居然懂得刷新内存了，为什么呢？
     * 这和输出输出方法的实现有关，为了保证输出结果不变，都会为输出对象加锁，这本身没什么
     * 但是在一个循环中执行输出语句，意味着要获取n次锁，因此JVM会尝试让整个代码块都拥有锁，节约性能
     * 这时由于锁的关系，数据就得以刷新
     */
    static class NoVolatile {
        static boolean isOver = false;

        public static void main(String[] args) {
            new Thread(() -> {
                while (!isOver) ;
                System.out.println("over " + isOver);
            }).start();

            try {
                Thread.sleep(100);
                isOver = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("怎么说");
        }
    }

    static class Volatile {
        static volatile boolean isOver = false;

        public static void main(String[] args) {
            new Thread(() -> {
                while (!isOver) ;
                System.out.println("\nover " + isOver);
            }).start();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isOver = true;
        }
    }

    /**
     * 可见 volatile 做的是完全不一样的事情，对于大宗操作，一样没用
     * 但对于标志位，或许够用吧
     */
    @Test
    public void 不稳定的Count2() {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (int j = 0; j < 100000; j++) {
                    Count3.count++;
                }
            }).start();
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("count: " + Count3.count);
    }

    static class Count3 {
        static volatile int count = 0;
    }
}
