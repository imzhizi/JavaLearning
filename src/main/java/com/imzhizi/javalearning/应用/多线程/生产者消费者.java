package com.imzhizi.javalearning.应用.多线程;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * created by zhizi
 * on 4/16/20 11:41
 */
public class 生产者消费者 {
    /**
     * 基于synchronized的生产者消费者
     * 如果满了，生产者就死循环+休眠自身，消费者消费之后，会唤醒wait状态的线程
     * <p>
     * wait 会释放锁，如果下一个线程是生产者继续休眠，而不会再参与到锁的竞争中
     * 这个死循环的思想非常重要
     */
    static class 同步锁 {
        public static void main(String[] args) throws InterruptedException {
            for (int i = 1; i < 9; i++) {
                if (i % 4 == 0) {
                    new Thread(() -> {
                        try {
                            for (int j = 0; j < 300; j++) {
                                consume();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }, "consumer" + i).start();
                } else {
                    new Thread(() -> {
                        try {
                            for (int j = 0; j < 100; j++) {
                                produce();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }, "producer" + i).start();
                }
            }

            Thread.sleep(1000);
            System.out.println("生产失败：" + proFailure.get());
            System.out.println("消费失败：" + conFailure.get());
//            System.out.println("消费成功：" + conSuccess.get());
        }

        private static final Container container = new Container();

        static final AtomicInteger proFailure = new AtomicInteger(0);
        static final AtomicInteger conFailure = new AtomicInteger(0);
        static final AtomicInteger conSuccess = new AtomicInteger(0);

        public static void produce() throws InterruptedException {
            synchronized (container) {
                while (container.isFull()) {
                    proFailure.incrementAndGet();
                    container.wait();
                }

                System.out.println(Thread.currentThread().getName() + " " + container.push());
                container.notifyAll();
            }
        }

        public static void consume() throws InterruptedException {
            synchronized (container) {
                while (container.isEmpty()) {
                    conFailure.incrementAndGet();
                    container.wait();
                }
                System.out.println(Thread.currentThread().getName() + " " + container.pop());
                conSuccess.incrementAndGet();
                container.notifyAll();
            }
        }
    }

    /**
     * 相比之下这个失败率就比较高，同样基于synchronized的生产者消费者
     * 但是由于消费者较少，因此虽然生产者都在竞争锁，但他们获得锁之后无法生产，因此只能白白加锁解锁
     */
    static class 同步锁2 {
        public static void main(String[] args) throws InterruptedException {
            for (int i = 1; i < 9; i++) {
                if (i % 4 == 0) {
                    new Thread(() -> {
                        try {
                            for (int j = 0; j < 300; j++) {
                                consume();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }, "consumer" + i).start();
                } else {
                    new Thread(() -> {
                        try {
                            for (int j = 0; j < 100; j++) {
                                produce();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }, "producer" + i).start();
                }
            }

            Thread.sleep(1000);
            System.out.println("生产失败：" + proFailure.get());
            System.out.println("消费失败：" + conFailure.get());
            System.out.println("消费成功：" + conSuccess.get());
        }

        private static final Container container = new Container();
        static final AtomicInteger proFailure = new AtomicInteger(0);
        static final AtomicInteger conFailure = new AtomicInteger(0);
        static final AtomicInteger conSuccess = new AtomicInteger(0);

        public static void produce() throws InterruptedException {
            synchronized (container) {
                if (container.isFull()) {
                    proFailure.incrementAndGet();
                } else {
                    System.out.println(Thread.currentThread().getName() + " " + container.push());
                }
            }
        }

        public static void consume() throws InterruptedException {
            synchronized (container) {
                if (container.isEmpty()) {
                    conFailure.incrementAndGet();
                } else {
                    System.out.println(Thread.currentThread().getName() + " " + container.pop());
                    conSuccess.incrementAndGet();
                }
            }
        }
    }

    /**
     * 基于 ReentrantLock 的实现
     * ReentrantLock 的好处在于可以多个Condition，能够对读进程、写进程单独控制
     * 在引入 condition 之前， 必须明确如果不使用 condition 那么代码和 同步锁 几乎没区别
     * 而引入了 condition，目的是对两种线程有更精确的控制，像下面这两种实现
     * - 不包含else，container 会直接被爆破，因为很多生产者休眠，但唤醒他们的消费者必须等容器为空时，生产者被唤醒后不再检查容器容量，所以爆破
     * - 包含了else，只是避免了容器被爆破，但是不代表问题的解决，最后会存在一个线程无法被notify，因为现在存在两个condition了
     */
    static class 可重入锁 {
        private static final Container container = new Container();
        private static final AtomicInteger proFailure = new AtomicInteger(0);
        private static final AtomicInteger conFailure = new AtomicInteger(0);
        private static final Lock lock = new ReentrantLock();
        private static final Condition empty = lock.newCondition();
        private static final Condition full = lock.newCondition();

        public static void main(String[] args) throws InterruptedException {
            for (int i = 1; i < 9; i++) {
                if (i % 4 == 0) {
                    new Thread(() -> {
                        try {
                            for (int j = 0; j < 300; j++) {
                                consume();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }, "consumer" + i).start();
                } else {
                    new Thread(() -> {
                        try {
                            for (int j = 0; j < 100; j++) {
                                produce();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }, "producer" + i).start();
                }
            }

            Thread.sleep(1000);
            System.out.println("生产失败：" + proFailure.get());
            System.out.println("消费失败：" + conFailure.get());
        }

        public static void produce() throws InterruptedException {
            lock.lock();
            if (container.isFull()) {
                proFailure.incrementAndGet();
                full.await();
            } else {
                System.out.println(Thread.currentThread().getName() + " " + container.push());
            }
            empty.signalAll();
//            if (container.isFull()) {
//                proFailure.incrementAndGet();
//                empty.signal();
//                full.await();
//            }
            lock.unlock();
        }

        public static void consume() throws InterruptedException {
            lock.lock();
            if (container.isEmpty()) {
                conFailure.incrementAndGet();
                empty.await();
            } else {
                System.out.println(Thread.currentThread().getName() + " " + container.pop());
            }
            full.signalAll();
            lock.unlock();
        }
    }

    /**
     * 这个实现就很完美、优雅
     * 通过 condition 可以定点控制
     */
    static class 可重入锁2 {
        private static final Container container = new Container();
        private static final AtomicInteger proFailure = new AtomicInteger(0);
        private static final AtomicInteger conFailure = new AtomicInteger(0);
        private static final Lock lock = new ReentrantLock();
        private static final Condition empty = lock.newCondition();
        private static final Condition full = lock.newCondition();

        public static void main(String[] args) throws InterruptedException {
            for (int i = 1; i < 9; i++) {
                if (i % 4 == 0) {
                    new Thread(() -> {
                        try {
                            for (int j = 0; j < 300; j++) {
                                consume();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }, "consumer" + i).start();
                } else {
                    new Thread(() -> {
                        try {
                            for (int j = 0; j < 100; j++) {
                                produce();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }, "producer" + i).start();
                }
            }

            Thread.sleep(1000);
            System.out.println("生产失败：" + proFailure.get());
            System.out.println("消费失败：" + conFailure.get());
        }

        public static void produce() throws InterruptedException {
            lock.lock();
            while (container.isFull()) {
                proFailure.incrementAndGet();
                full.await();
            }
            System.out.println(Thread.currentThread().getName() + " " + container.push());
            empty.signal();
            lock.unlock();
        }

        public static void consume() throws InterruptedException {
            lock.lock();
            while (container.isEmpty()) {
                conFailure.incrementAndGet();
                empty.await();
            }
            System.out.println(Thread.currentThread().getName() + " " + container.pop());
            full.signal();
            lock.unlock();
        }
    }

    /**
     * 除非是真的信号量，它的自减和阻塞是原语操作，但在Java里就很难做到
     * 因此也无法使用三个变量来实现信号量版的生产者、消费者机制
     * 可以使用 concurrent 包提供的 Semaphore
     */
    static class PV {
        private static final Container container = new Container();
        private static final int cap = 8;
        private static AtomicInteger mutex = new AtomicInteger(1);
        private static AtomicInteger ava = new AtomicInteger(0);
        private static AtomicInteger free = new AtomicInteger(cap);

        private static final AtomicInteger proFailure = new AtomicInteger(0);
        private static final AtomicInteger conFailure = new AtomicInteger(0);

        public static void main(String[] args) throws InterruptedException {
            for (int i = 1; i < 9; i++) {
                if (i % 4 == 0) {
                    new Thread(() -> {
                        try {
                            for (int j = 0; j < 300; j++) {
                                consume();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }, "consumer" + i).start();
                } else {
                    new Thread(() -> {
                        try {
                            for (int j = 0; j < 100; j++) {
                                produce();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }, "producer" + i).start();
                }
            }

            Thread.sleep(1000);
            System.out.println("生产失败：" + proFailure.get());
            System.out.println("消费失败：" + conFailure.get());
        }

        public static void produce() throws InterruptedException {
            int cur = free.get();
            if (free.compareAndSet(cur - 1, cur)) {
                while (mutex.compareAndSet(1, 0)) {
                    System.out.println(Thread.currentThread().getName() + " " + container.push());
                    mutex.incrementAndGet();
                    ava.incrementAndGet();
                }
            }
        }

        public static void consume() throws InterruptedException {
            int cur = ava.get();
            while (ava.compareAndSet(cur - 1, cur)) {
                while (mutex.compareAndSet(1, 0)) {
                    System.out.println(Thread.currentThread().getName() + " " + container.pop());
                    mutex.incrementAndGet();
                    free.incrementAndGet();
                }
            }
        }
    }


    /**
     * 可以看到，信号量是原语操作，会在内部阻塞，而不是对代码块的加锁
     * 所以当信号量释放的时候，能够继续执行，并且通知另外一个线程
     */
    static class 信号量 {
        private static final Container container = new Container();
        private static final int cap = 8;
        private static final Semaphore mutex = new Semaphore(1);
        private static final Semaphore ava = new Semaphore(0);
        private static final Semaphore free = new Semaphore(cap);

        private static final AtomicInteger proFailure = new AtomicInteger(0);
        private static final AtomicInteger conFailure = new AtomicInteger(0);

        public static void main(String[] args) throws InterruptedException {
            for (int i = 1; i < 9; i++) {
                if (i % 4 == 0) {
                    new Thread(() -> {
                        try {
                            for (int j = 0; j < 300; j++) {
                                consume();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }, "consumer" + i).start();
                } else {
                    new Thread(() -> {
                        try {
                            for (int j = 0; j < 100; j++) {
                                produce();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }, "producer" + i).start();
                }
            }

            Thread.sleep(1000);
            System.out.println("生产失败：" + proFailure.get());
            System.out.println("消费失败：" + conFailure.get());
        }

        public static void produce() throws InterruptedException {
            free.acquire();
            mutex.acquire();
            System.out.println(Thread.currentThread().getName() + " " + container.push());
            mutex.release();
            ava.release();
        }

        public static void consume() throws InterruptedException {
            ava.acquire();
            mutex.acquire();
            System.out.println(Thread.currentThread().getName() + " " + container.pop());
            mutex.release();
            free.release();
        }
    }

    /**
     *
     */
    static class 阻塞队列 {
        private static final BlockingQueue<Object> container = new ArrayBlockingQueue<>(8);

        public static void main(String[] args) throws InterruptedException {
            for (int i = 1; i < 9; i++) {
                if (i % 4 == 0) {
                    new Thread(() -> {
                        try {
                            for (int j = 0; j < 300; j++) {
                                consume();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }, "consumer" + i).start();
                } else {
                    new Thread(() -> {
                        try {
                            for (int j = 0; j < 100; j++) {
                                produce();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }, "producer" + i).start();
                }
            }
        }

        public static void produce() throws InterruptedException {
            container.put(1);
            System.out.println(Thread.currentThread().getName() + " " + container.size());

        }

        public static void consume() throws InterruptedException {
            container.take();
            System.out.println(Thread.currentThread().getName() + " " + container.size());

        }
    }

    static class Container {
        static int cap = 8;
        static volatile AtomicInteger cur = new AtomicInteger(0);


        public boolean isFull() {
            return cur.get() == cap;
        }

        public boolean isEmpty() {
            return cur.get() == 0;
        }

        public int pop() {
            if (isEmpty()) throw new ArrayIndexOutOfBoundsException();
            return cur.getAndDecrement();
        }

        public int push() {
            if (isFull()) throw new ArrayIndexOutOfBoundsException();
            return cur.incrementAndGet();
        }
    }
}