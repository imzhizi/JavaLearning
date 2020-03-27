package com.imzhizi.javalearning.spring;

import lombok.Data;
import org.junit.Test;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.Serializable;

/**
 * created by zhizi
 * on 3/16/20 09:09
 */
public class ConfigurationTest {
    /**
     * 使用 @Configuration 注解标记配置类
     * 之前介绍的 基于注解的自动装配 均可以使用
     * 可以想像 SpringBoot 就通过这样一个配置类，拥有大量的定义类，启动时将这些 Bean 加载进去形成 Bean 池
     * 然后根据他们的依赖关系，初始化时就完成强制依赖的装配，灵活依赖则通过setter完成
     * <p>
     * 通过 @Import 则可以把不同的配置和到一起使用
     * 还可以通过 @Scope 设置 bean 的 Scope
     */
    @Test
    public void test() {
        ConfigurableApplicationContext ctx =
                new AnnotationConfigApplicationContext(EGBeanConfigA.class);
        Student student = ctx.getBean(Student.class);
        System.out.println(student);
        ctx.registerShutdownHook();
    }


    @Configuration
    @Import(EGBeanConfigB.class)
    static class EGBeanConfigA {
        @Bean(initMethod = "go", destroyMethod = "back")
        public Student student() {
            return new Student();
        }

        @Bean
        public School schoolA() {
            School school = new School();
            school.setName("一中");
            school.setPrincipal("张校长");
            return school;
        }
    }

    @Data
    static class Student {
        private String name;
        @Autowired
        @Qualifier("schoolB")
        private School school;

        public void go() {
            System.out.println("上学去啦");
        }

        public void back() {
            System.out.println("放学啦");
        }
    }

    @Data
    static class School implements InitializingBean, DisposableBean {
        private String name;
        private String principal;

        @Override
        public void afterPropertiesSet() throws Exception {
            System.out.println("school start");
        }

        @Override
        public void destroy() throws Exception {
            System.out.println("school end");
        }

        @PostConstruct
        public void before() {
            System.out.println("school creating");
        }

        @PreDestroy
        public void after() {
            System.out.println("deleting school");
        }
    }

    @Configuration
    static class EGBeanConfigB {
        @Bean
        public School schoolB() {
            School school = new School();
            school.setName("二中");
            school.setPrincipal("李校长");
            return school;
        }
    }
}
