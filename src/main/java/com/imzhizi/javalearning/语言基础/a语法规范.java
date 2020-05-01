package com.imzhizi.javalearning.语言基础;

/**
 * created by zhizi
 * on 4/29/20 09:45
 */
public class a语法规范 {
    static {
        System.out.println("static outer");
    }

    {
        System.out.println("outer");
    }

    static int me;
    private int i;

    public a语法规范(String s) {
        Inner.name = s;
    }

    static class Inner {
        static String name = "xx";
    }

    public static void main(String[] args) {
        a语法规范 aa = new a语法规范("a");
        a语法规范 bb = new a语法规范("b");
        System.out.println(a语法规范.Inner.name);
        System.out.println(a语法规范.Inner.name);
    }

    public final void test() {

    }
}
