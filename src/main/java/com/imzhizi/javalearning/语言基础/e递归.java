package com.imzhizi.javalearning.语言基础;

/**
 * created by zhizi
 * on 5/1/20 10:16
 */
public class e递归 {
    static long val;
    public static void main(String[] args) {
        test(32);
        System.out.println(val);
    }

    public static void test(int i) {
        if (i == 0) return;
        val++;
        test(i - 1);
        test(i - 1);
    }
}
