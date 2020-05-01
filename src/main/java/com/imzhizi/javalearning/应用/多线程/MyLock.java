package com.imzhizi.javalearning.应用.多线程;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

import static java.lang.Thread.yield;

/**
 * created by zhizi
 * on 4/15/20 11:16
 */
public class MyLock {
    static class MyCASLock {
        volatile static AtomicInteger state = new AtomicInteger(0);

        public void lock() {
            while (!state.compareAndSet(0, 1)) ;
        }

        public void unlock() {
            state.incrementAndGet();
        }
    }

    static class MyYieldLock {
        volatile static AtomicInteger state = new AtomicInteger(0);

        public void lock() {
            while (!state.compareAndSet(0, 1)) {
                yield();
            }
        }

        public void unlock() {
            state.incrementAndGet();
        }
    }

    static class MySleepLock {
        volatile static AtomicInteger state = new AtomicInteger(0);

        public void lock() {
            while (!state.compareAndSet(0, 1)) {
                yield();
            }
        }

        public void unlock() {
            state.incrementAndGet();
        }
    }

    /**
     * park, unpark
     * 其实来源于unsafe类
     */
    static class MyParkLock {
        volatile static AtomicInteger state = new AtomicInteger(0);
        final static ArrayDeque<Thread> queue = new ArrayDeque();

        public void lock() {
            if (!state.compareAndSet(0, 1)) {
                queue.addLast(Thread.currentThread());
                LockSupport.park();
            }
        }

        public void unlock() {
            Thread.currentThread().interrupt();
            Thread t = queue.pollFirst();
            state.incrementAndGet();
            LockSupport.unpark(t);
        }
    }

    @Test
    public void testMyLock() {
    }
}
