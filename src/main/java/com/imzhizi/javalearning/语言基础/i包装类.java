package com.imzhizi.javalearning.语言基础;

import org.junit.Test;

/**
 * created by zhizi
 * on 3/24/20 14:23
 * 参考 [java基础：Integer — 源码分析 - 掘金](https://juejin.im/post/5c16041f518825566d236529 )
 */
public class i包装类 {
    /**
     * Integer
     * Integer 同样被 final 关键字修饰，所以不能被继承
     * ### 关于 Number 接口
     * Integer 实现了 Number 接口提供了 longValue、floatValue 在内的多个方法
     * 通过这些方法能够直接进行类型转换
     * <p>
     * ### 构成成分
     * Integer 的值由字段 final value
     * MAX_VALUE、MIN_VALUE、 Class<Integer>  TYPE
     * SIZE-32、BYTES - (SIZE / Byte.SIZE 表示 int 的字节数)
     * <p>
     * ### 关于 final 的 value
     * 所以 value 的地址是不可修改的，这很反直觉，常识中如果我们重新对变量赋值，那么变量的值一定会改变
     * 那 final 如何解释，对赋值代码反编译后可以看到，Integer 直接向变量提供了一个新对象
     * Integer i = new Integer(112); i = Integer.valueOf(660);
     * 这时 value 的地址没有变化，而是 Integer 自身地址变了，和 String 应该是类似的
     * <p>
     * ### 「valueOf」从哪里提供了对象
     * 从代码可以看到，JVM 同样为 Integer 设计了缓存，如果在缓存范围内，那么可以直接提供
     * if (i >= IntegerCache.low && i <= IntegerCache.high)
     * return IntegerCache.cache[i + (-IntegerCache.low)];
     * return new Integer(i);
     * 如果不在缓存中，那么同样需要重新创建
     * <p>
     * ### Integer 的缓存机制
     * Integer 内部设计了一个「IntegerCache」做缓存，共定义了 low、high、cache[] 三个字段
     * 在初始化阶段，Integer.IntegerCache 直接将 -128～127 之间的 Integer 对象全部创建好放进缓存数组中
     * 赋值操作则全部转换为 valueOf，一旦存在则直接提供
     * 而构造方法则无视缓存，所以产生的对象与直接复制的对象地址不同
     * <p>
     * ### 其他方法
     * 其他方法主要是 String 和 int 之间的互相转换，并牵涉不同进制
     * 那么int是如何输出的呢？可以想像的需要调用「toString」方法，源码如下
     * if (i == Integer.MIN_VALUE) return "-2147483648";
     * int size = (i < 0) ? stringSize(-i) + 1 : stringSize(i);
     * char[] buf = new char[size];
     * getChars(i, size, buf);
     * return new String(buf, true);
     * stringSize 是什么，stringSize 通过一个数组 stringSize，里面放着 9、99、999……，然后比较来得到数字的长度
     * 现在的关键就是「getChars」方法了，「toString」作为一个无数次调用的看不见的方法，一定要优化的过关
     * <p>
     * ### 极尽优化的「getChars」
     * 可以先说一些基本的原则：乘法比除法高效，位运算比乘法高效，但位运算有弊端，位运算可能会越界
     * 所以算法以 65536 为界分为了两种处理方法
     * 65536以下，每次 i 除以10得到 q，然后用 i-q*10，就能得到个位数
     * ((i * 52429) >>> (16+3))，r = i - ((q << 3) + (q << 1));
     * 而对于 65536 以上的数字，再使用位运算可能会有越界问题，使用了另一种方式
     * q = i / 100;
     * r = i - ((q << 6) + (q << 5) + (q << 2)); // really: r = i - (q * 100);
     * 每个 r 都是一个两位数，通过两个长度为一百的数组，一个得到十位，一个得到个位，来获得最终字符
     */
    @Test
    public void 整型() {
        Integer i1 = 112;
        Integer i2 = 110;
        System.out.println(i2);
        i2 = 112;
        System.out.println(i1 == i2);   //true
        Integer i21 = new Integer(112);
        Integer i22 = Integer.valueOf(112);
        System.out.println(i1 == i21);  //false
        System.out.println(i1 == i22);  //true

        Integer i3 = 146;
        Integer i4 = 146;
        Integer i5 = Integer.valueOf(146);
        System.out.println(i3 == i4);   //false
        System.out.println(i3 == i5);   //false
    }

    /**
     * Double 这些类型都使用了类似的设计
     * final 使其不能继承，实现 Number 接口用于类型转换
     * 可以看到，Double 没有使用缓存机制，全部都靠即时创建
     * ……
     */
    @Test
    public void 双精度() {
        Double d1 = 1.0;
        Double d2 = new Double(1.0);
        System.out.println(d1 == d2);
        Double d3 = Double.valueOf(1.0);
        System.out.println(d1 == d3);
    }
}
