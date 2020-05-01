package com.imzhizi.javalearning.语言基础;

import org.junit.Test;

/**
 * created by zhizi
 * on 3/31/20 15:56
 */
public class d类加载 {
    static class Parent {
        static int pp = 123;

        static {
            System.out.println("i'm ur fa");
        }

        static void out() {
            System.out.println("static out");
        }
    }

    static class Son extends Parent {
        static int ss = 234;
        static final int css = 234;
        static Init init;

        static {
            System.out.println("i'm dd");
        }
    }


    @Test
    public void test() {
        // 两者都触发
        Son son = new Son();
        System.out.println(Son.ss);

        // 仅触发父类
        // System.out.println(Son.pp);
        // Son.out();

        // 都不触发
        // System.out.println(Son.sss);
        // Son[] sons = new Son[10];
    }

    /**
     * 一般的变量都是先声明再使用
     * 静态块虽然不能在静态变量声明之前就使用，但能够在静态变量声明前就对其进行赋值
     * 如果静态变量的初始化值和静态块的赋值谁在后谁有效
     */
    @Test
    public void 静态块() {
        System.out.println(Init.iii);
    }

    static class Init implements IDemo {
        static {
            iss = 111;
//            以下这个输出语句会提示错误 - 非法向前引用
//            System.out.println(i);
            System.out.println("静态块");
        }

        static int iss = 3;
    }

    interface IDemo {
        int iii = 2;
    }
}