package com.imzhizi.javalearning.语言基础;

import org.junit.Test;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * created by zhizi
 * on 4/18/20 15:43
 */
public class gGC {
    static class NR {
        @Override
        protected void finalize() throws Throwable {
            System.out.println("finalize");
        }
    }

    /**
     * 普通引用
     */
    @Test
    public void testNR() {
        NR nr = new NR();
        nr = null;
        System.gc();
    }

    /**
     * 软引用
     * 不够用才会被GC掉
     * 通过虚拟机参数 Xmx20M，将虚拟机堆大小设置为20M，但只能创建10M长度的byte，难道一个byte长度是2字节？
     */
    @Test
    public void testSR() {
        SoftReference<byte[]> sr1 = new SoftReference(new byte[1024 * 1024 * 8]);
        SoftReference<byte[]> sr2 = new SoftReference(new byte[1024 * 1024 * 1]);
        System.out.println("sr1 address: " + sr1.get());
        System.out.println("sr2 address: " + sr2.get());
        byte[] bs = new byte[1024 * 1024 * 10];
        System.out.println("sr1 address: " + sr1.get());
        System.out.println("sr2 address: " + sr2.get());
        System.out.println("new bytes address: " + bs);
    }

    /**
     * 弱引用
     */
    @Test
    public void testWR() {
        WeakReference<byte[]> wr = new WeakReference<>(new byte[1024 * 1024 * 10]);
        System.out.println(wr.get());
        System.gc();
        System.out.println(wr.get());
    }

    /**
     * 虚引用
     * 众所周知，虚引用直接就会被GC，主要是为了临走前发送一条消息
     * 有什么用呢？可以用于堆外内存的GC
     */
    @Test
    public void testPR() throws InterruptedException {
        ReferenceQueue<Object> queue = new ReferenceQueue<>();
        PhantomReference<Object> pr = new PhantomReference<>(new Object(), queue);

        new Thread(() -> {
            for (int i = 1; i < 20; i++) {
                System.out.println(pr.get());
                if (i % 5 == 0) System.gc();
            }
        }).start();

        new Thread(() -> {
            while (true) {
                Object obj = queue.poll();
                if (obj != null) {
                    System.out.println("=================" + obj);
                    return;
                }
            }
        }).start();


        Thread.sleep(3000);
    }
}
