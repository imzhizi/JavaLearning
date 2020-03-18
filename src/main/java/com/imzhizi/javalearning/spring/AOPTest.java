package com.imzhizi.javalearning.spring;

import lombok.Data;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * created by zhizi
 * on 3/16/20 14:53
 */
public class AOPTest {
    /**
     * - 从切面编程说开去
     * 切面编程，显然是横向的，是定义一个逻辑上的面，然后在面前后有一些相关操作，还要保持不侵入性，降低耦合
     * - 切面是什么？
     * 面是Aspect，是一组方法、一组API的集合，每一个单位都是一个 Join point
     * - 切面怎么来定义的
     * 逻辑上说面由 Pointcut 所定义，那具体如何实现呢？是通过方法路径来定义，可以使用通配符
     * 类似于 "execution(* com.imzhizi.javalearning.spring.AspectTest.ExampleBean.*(..))"
     * - 在切面前后执行什么方法？如何执行？
     * 要执行的方法被称为 advice
     * 落到代码层面，不难想象，没有侵入性的(不直接调用)执行目标方法，必然是存在代理对象
     * 代理对象(advisor?)包裹了将要执行的方法和各种advice包裹在一起，然后执行
     * - 都有哪些advice？
     * 如何围绕切面编程，在方法前、方法后，方法出现异常等不同情况下采取不同的措施
     * - Target Object
     * 应该是 advisor，也就是代理对象，它应该是在Weaving之后产生
     * - Introduction
     * 代理对象在Weaving之后完成，但并不是Weaving的时候才存在
     * 代理对象更早就存在，但需要添加新方法或属性到其类中，才能完成切面的代理
     * - Weaving
     * Spring AOP 是在运行时生成代理对象来织入的, 但这不是唯一的方式
     * 但其实通过AspectJ还可以在编译期、类加载期织入
     */
    @Test
    public void 切面测试() {
        ApplicationContext context =
                new ClassPathXmlApplicationContext("AOPTest.xml");
        ExampleBean egBean = (ExampleBean) context.getBean("egBean");
        System.out.println(egBean.getName());
        System.out.println(egBean.getCount());
        egBean.printThrowException();
    }

    @Data
    static class ExampleBean {
        private String name;
        private Integer count;

        public void demo() {
            System.out.println("demo");
        }

        public void printThrowException() {
            System.out.println("Exception raised");
            throw new IllegalArgumentException();
        }
    }

    static class Logging {
        /**
         * This is the method which I would like to execute
         * before a selected method execution.
         */
        public void beforeAdvice() {
            System.out.println("Going to setup EGBean profile.");
        }

        /**
         * This is the method which I would like to execute
         * after a selected method execution.
         */
        public void afterAdvice() {
            System.out.println("EGBean profile has been setup.");
        }

        /**
         * This is the method which I would like to execute
         * when any method returns.
         */
        public void afterReturningAdvice(Object retVal) {
            System.out.println("Returning:" + retVal.toString());
        }

        /**
         * This is the method which I would like to execute
         * if there is an exception raised.
         */
        public void afterThrowingAdvice(IllegalArgumentException ex) {
            System.out.println("There has been an exception: " + ex.toString());
        }
    }
}
