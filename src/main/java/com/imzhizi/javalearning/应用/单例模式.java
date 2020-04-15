package com.imzhizi.javalearning.应用;

import org.junit.Test;

/**
 * created by zhizi
 * on 4/1/20 18:13
 */
public class 单例模式 {
    /**
     * 单例模式实现思路
     * 静态化实例对象
     * 私有化构造方法，禁止通过构造方法创建实例
     * 提供一个公共的静态方法，用来返回唯一实例
     */
    @Test
    public void 单例() {
        SyncedSingleton.getInstance().doSomething();
        StaticSingleton.getInstance().doSomething();
        HungrySingleton.getInstance().doSomething();
        EnumSingleton.INSTANCE.doSomething();
    }

    /**
     * 双重检查+锁
     * 按需加载，通过为类对象加锁保证类变量的线程安全
     * 为什么需要双重检查
     * 检查1：如果instance已经被实例化，那么就不需要加锁，直接返回，如果未创建则创建
     * 检查2：synchronized 可能被调用多次，再次检查保证只被实例化一次
     * synchronized 和 volatile 的作用
     * synchronized 保证只有一个线程在实例化类变量，类变量被实例化只后才可以读取
     * 因为存在一个致命缺陷
     * 因指令重排而造成未被初始化成功的对象逸出
     * 具体来说，对象的创建：分配内存(地址已经存在了)、设置零值、设置对象头、构造函数，然后将地址返回给变量名
     * 但经过指令重排，线程A可能还没有完成对象的初始化就已经被线程B调用了
     * 所以需要 volatile 来避免指令重排，确保初始化未完成之前变量名都是null
     */
    static class SyncedSingleton {
        private volatile static SyncedSingleton instance;

        private SyncedSingleton() {
        }

        public static SyncedSingleton getInstance() {
            if (instance == null) {
                synchronized (SyncedSingleton.class) {
                    if (instance == null) {
                        instance = new SyncedSingleton();
                    }
                }
            }
            return instance;
        }

        public void doSomething() {
            System.out.println("doSomething");
        }
    }

    /**
     * 静态内部类
     * 同样通过类初始化来保证单例的线程安全
     * 由于是内部类，所以在类加载的时候不会初始化，在调用时才会初始化，满足了懒加载的要求
     */
    static class StaticSingleton {
        private StaticSingleton() {
        }

        // 内部类持有外部的单例
        private static class InstanceHolder {
            private static StaticSingleton instance = new StaticSingleton();
        }

        // 调用内部类属性
        public static StaticSingleton getInstance() {
            return InstanceHolder.instance;
        }

        public void doSomething() {
            System.out.println("doSomething");
        }
    }

    /**
     * 简单粗暴的饿汉模式
     * 在类加载的准备阶段，通过关键字 new 触发类初始化，private 的构造函数
     * 初始化结束之后再分配内存，然后直接类加载结束，因为已经完成了类初始化，相当于类加载过程直接完成类初始化
     * 由于JVM的规定，保证了类初始化时候的线程安全，进而保证了instance的线程安全
     * 不能实现懒加载，造成空间浪费，如果一个类比较大，类加载时就实例化了对象，但又长时间未用，这会导致内存空间的浪费
     */
    static class HungrySingleton {
        private static HungrySingleton instance = new HungrySingleton();

        private HungrySingleton() {
        }

        public static HungrySingleton getInstance() {
            return instance;
        }

        public void doSomething() {
            System.out.println("doSomething");
        }
    }

    /**
     * 枚举实现单例模式
     * JVM就可以帮助我们保证线程安全和单一实例的问题
     * 同时枚举对象序列化也比较容易
     */
    public enum EnumSingleton {
        INSTANCE;

        public void doSomething() {
            System.out.println("doSomething");
        }
    }
}