package com.imzhizi.javalearning.应用.多线程;

import org.junit.Test;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * created by zhizi
 * on 4/19/20 10:38
 */
public class 线程池 {
    /**
     * 可以很直观地看到，
     * 有5个线程(CORE_POOL_SIZE)在执行任务，执行之后接下来的5个任务依然是这5个线程，因为线程ID没有变化
     * 尝试调整QUEUE_CAPACITY为4，可以发现多了一个 thread-6，说明阻塞队列满了之后，会根据MAX_POOL_SIZE创建新线程执行任务
     * <p>
     * 如何做到的呢？可以看一下 ThreadPoolExecutor 的数据结构
     * 首先拥有状态数 ctl，通过位切分同时表示两个值，线程池状态和线程池中的数量，其中状态位在高位，这样可以正常地进行线程数量的增减操作
     * The main pool control state, ctl, is an atomic integer packing two conceptual fields
     * - workerCount, indicating the effective number of threads
     * - runState,    indicating whether running, shutting down etc
     * - largestPoolSize
     * HashSet:workers 是线程池中所拥有的线程集合、mainLock&termination 非公平锁
     * <p>
     * execute 方法 - 不能保证顺序执行
     * - 比较线程池中线程的数量和CORE_POOL_SIZE的大小关系，假如小于核心线程池大小，则调用 addWorker()，创建一个线程执行任务
     * addWorker 会实例化一个 worker 对象加入到 workers 中，还要修改相应的count等值（包含加锁操作），最终启动worker执行任务
     * worker 是一个线程和任务的封装，通过 getThreadFactory().newThread(this) 初始化一个线程，并绑定task
     * - 无法直接创建就要进入阻塞队列，即便入队成功，我们也要二次检查能否创建一个线程，因为这个过程中可能有线程死亡，还要再检查一次线程池是否要关闭
     * 如果线程池将要关闭，就要remove(command)&reject(command)，如果所有的线程都执行任务完毕(workingCount==0)，还要创建新线程
     * - 如果入队也失败，尝试MAX_POOL_SIZE模式创建线程也失败，就就会 reject(command);
     */
    @Test
    public void 简单线程池() {
        int CORE_POOL_SIZE = 5;
        int MAX_POOL_SIZE = 10;
        int QUEUE_CAPACITY = 100;
        long KEEP_ALIVE_TIME = 1L;

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                new ThreadPoolExecutor.CallerRunsPolicy());


        for (int i = 0; i < 10; i++) {
            Runnable worker = new MyRunnable(i);
            executor.execute(worker);
        }

//        executor.shutdownNow();
        while (executor.getActiveCount() > 0) ;

        System.out.println("Finished all threads");
    }

    static class MyRunnable extends Thread {

        public MyRunnable(int i) {
            this.setName(String.valueOf(i));
        }

        private void processCommand() {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " Start. Time = " + new Date());
            processCommand();
            System.out.println(Thread.currentThread().getName() + " End. Time = " + new Date());
        }
    }
}
