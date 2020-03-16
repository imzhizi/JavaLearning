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
     * 切面编程
     * 首先要知道面在哪里？面是 Aspect，面确实是一组横向API，具体多少个看怎么切
     * 实际上面是 Pointcut 切出来的，是以方法为维度的，可以从包的层次来规定
     * 如何围绕切面编程，在方法前、方法后，方法出现异常等不同情况下采取不同的措施
     * todo Introduction 添加新方法或属性到现有的类中
     * Logging 就算是一个代理对象，tarfet object，连接的过程就是 Weaving
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
