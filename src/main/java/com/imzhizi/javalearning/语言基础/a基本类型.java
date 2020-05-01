package com.imzhizi.javalearning.语言基础;

import org.junit.Test;

/**
 * created by zhizi
 * on 4/20/20 15:28
 */
public class a基本类型 {
    /**
     * 以 float 为例，正数20.3的二进制为 1000001101000100110011001100110，共31位，因为会忽略符号位
     * 这31位是如何构成的呢？
     * 作为一个小数，最核心的就是小数点在哪里，IEEE规定32位浮点数，第一位为符号位，最后23位作数据位，中间8位是指数位
     * 而在double中，第一位同样为符号位，接下来11位作指数位，剩下的52位作数据位
     * 指数位是什么概念呢，可以把数据位中的数字想象为科学计数法表示后的数字，然后指数位就用来对小数点左右移动，既然是左右移动，就需要有正负号
     */
    @Test
    public void 浮点数() {
        float f_v1 = 20;
        double d_v1 = 20;

        float f_v2 = 20.3f;
        double d_v2 = 20.3;

        float f_v3 = 20.5f;
        double d_v3 = 20.5;

        System.out.println((f_v1 == d_v1) ? "true" : "false");
        System.out.println(f_v2 == d_v2 ? "true" : "false");
        System.out.println(f_v2 < d_v2 ? "true" : "false");
        System.out.println(f_v3 == d_v3 ? "true" : "false");

        System.out.println(Long.toBinaryString(Double.doubleToLongBits(20.3)));
        System.out.println(0B10100);
        System.out.println(0B0100_1100_1100_1100_1100_1100_1100_1100_1100_1100_1100_1101L);

        System.out.println(Integer.toBinaryString(Float.floatToIntBits(-20.3f)));
        System.out.println(0B10100);
        System.out.println(0B0100_1100_1100_1100_110);
    }

    /**
     * 首先指数位定义负数表示左移，毕竟小数点左移会变小，正数表示右移
     * 整数一般采用补码来做，但指数位不是这样的，它使用了移位存储，8位数的数字范围是 0～255
     * 如果要表示正数、负数，肯定是分为两半，0～127、128～255，具体到移位存储中
     * 将 10000000 设为分界线，用 01111111～11111111 表示 0～127，用 00000000～01111111 表示 -0～-127
     * 计算方法就是 01111111+目标值，可以形成对称的数轴，也很容易实现对数字的比较，指数位大就不用比较具体数字
     */
    @Test
    public void 移位机制() {
        System.out.println(Integer.toBinaryString(Float.floatToIntBits(-20.3f)));
        System.out.println(0B10000011);
        System.out.println(0B10000011 - 0B1111111); // 4

        System.out.println(Integer.toBinaryString(Float.floatToRawIntBits(-0.003f)));
        System.out.println(0B01110110);
        System.out.println(0B01110110 - 0B1111111); // -9
        System.out.println(Integer.toBinaryString(Float.floatToRawIntBits(-0.005f)));
        System.out.println(0B01110111);
        System.out.println(0B01110111 - 0B1111111); // -8
    }
}
