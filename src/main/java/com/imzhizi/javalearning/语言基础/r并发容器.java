package com.imzhizi.javalearning.语言基础;

import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * created by zhizi
 * on 5/10/20 14:51
 */
public class r并发容器 {
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

    @Test
    public void 阻塞队列() {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);
        queue.element();
        queue.peek();
    }
}
