package com.imzhizi.javalearning.spring;

import lombok.Data;
import org.aspectj.lang.annotation.*;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * created by zhizi
 * on 3/16/20 16:56
 */
public class AspectTest {
    @Test
    public void POJO切面测试() {
        ApplicationContext context =
                new ClassPathXmlApplicationContext("AspectTest.xml");
        ExampleBean egBean = (ExampleBean) context.getBean("egBean");
        System.out.println(egBean.getName());
        System.out.println(egBean.getCount());
        egBean.printThrowException();
    }

    @Aspect
    static class AspectModule {
        @Pointcut("execution(* com.imzhizi.javalearning.spring.AspectTest.ExampleBean.*(..))") // expression
        private void dailyService() {
        }

        @Before("dailyService()")
        public void doBeforeTask() {
            System.out.println("before invoke");
        }

        @After("dailyService()")
        public void doAfterTask() {
            System.out.println("after invoke");
        }

        @AfterReturning(pointcut = "dailyService()", returning = "retVal")
        public void doAfterReturningTask(Object retVal) {
            System.out.println("return: " + retVal);
        }

        @AfterThrowing(pointcut = "dailyService()", throwing = "ex")
        public void doAfterThrowingTask(Exception ex) {
            System.out.println("ex throw");
        }
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
}
