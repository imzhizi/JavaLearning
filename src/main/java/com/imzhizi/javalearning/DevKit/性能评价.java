package com.imzhizi.javalearning.DevKit;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * created by zhizi
 * on 4/23/20 09:46
 * [JAVA 拾遗 — JMH 与 8 个测试陷阱 | 徐靖峰|个人博客](https://www.cnkirito.moe/java-jmh/ )
 */
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Measurement(iterations = 4) // 每个用例几次
@Fork(2) // 多线程，相当于将测试执行两遍
@State(Scope.Benchmark)
public class 性能评价 {

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }


    @Setup // 仅在每次迭代前执行一次
    public void init() {
        System.out.print("\ninit");
    }

    @Param({"11", "22", "33", "44"})  // 不同参数下的表现，相当于一共执行 iterations*i*fork 次
            int i;


    @Benchmark
    public void b1() {
        System.out.print("\nb" + i);
    }

//    @Benchmark
//    public String loop() {
//        String str = "";
//        for (int i = 0; i < 20; i++) {
//            str += i;
//        }
//        return str;
//    }
//
//    @Benchmark
//    public String loop2() {
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < 20; i++) {
//            sb.append(i);
//        }
//        return sb.toString();
//    }
}
