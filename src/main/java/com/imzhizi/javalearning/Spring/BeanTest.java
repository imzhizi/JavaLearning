package com.imzhizi.javalearning.Spring;

import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

import java.util.Date;

/**
 * created by zhizi
 * on 3/13/20 21:41
 */
public class BeanTest {
    @Test
    public void BeanFactoryTest() {
        // 可以看到
        // 读取xml中的配置后能够直接初始化对象
        XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource("BeanTest.xml"));

        System.out.println("student");
        System.out.println(factory.getBean("student"));
        System.out.println(StuDetail.count);
        System.out.println(factory.getBean("student"));
        System.out.println(StuDetail.count);
        System.out.println("teacher");
        System.out.println(factory.getBean("teacher"));
        System.out.println(Teacher.count);
        System.out.println(factory.getBean("teacher"));
        System.out.println(Teacher.count);

        System.out.println(factory.getBeanDefinition("teacher"));
    }

    @Test
    public void FXAppContextTest() {
        ApplicationContext context = new ClassPathXmlApplicationContext("BeanTest.xml");
        WorkAbility work = (WorkAbility) context.getBean("teacher");
        System.out.println(work.getWorkPlace());
    }

    @Test
    public void Bean的初始化和销毁() {
        AbstractApplicationContext context = new ClassPathXmlApplicationContext("BeanTest.xml");
        InitDisBean initDisBean = (InitDisBean) context.getBean("initDisBean");
        System.out.println(initDisBean);
        context.registerShutdownHook();
    }

    static class StuDetail extends BeanTest.Student {
        private Integer age;
        private Date date;
        static int count = 0;

        public StuDetail() {
            super();
            count++;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        @Override
        public String toString() {
            return "StuDetail{" +
                    "age=" + age +
                    ", date=" + date +
                    '}';
        }
    }

    static class Teacher implements WorkAbility {
        private String username;
        private Integer gender;
        public static int count = 0;

        public Teacher() {
            count++;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Integer getGender() {
            return gender;
        }

        public void setGender(Integer gender) {
            this.gender = gender;
        }

        @Override
        public String toString() {
            return "Teacher{" +
                    "username='" + username + '\'' +
                    ", gender=" + gender +
                    '}';
        }

        @Override
        public String getWorkPlace() {
            return "school";
        }
    }

    static class Student {
        private String username;
        private String pwd;
        private String gender;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPwd() {
            return pwd;
        }

        public void setPwd(String pwd) {
            this.pwd = pwd;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public Student(String username, String pwd, String gender) {
            this.username = username;
            this.pwd = pwd;
            this.gender = gender;
        }

        public Student() {
        }

        @Override
        public String toString() {
            return "Student{" +
                    "username='" + username + '\'' +
                    ", pwd='" + pwd + '\'' +
                    ", gender='" + gender + '\'' +
                    '}';
        }
    }

    interface WorkAbility {
        String getWorkPlace();
    }

    static class InitDisBean implements InitializingBean, DisposableBean {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "InitDisBean{" +
                    "name='" + name + '\'' +
                    '}';
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            System.out.println("start");
        }

        @Override
        public void destroy() throws Exception {
            System.out.println("end");
        }
    }

    static class Processor implements BeanPostProcessor {

        public void init() {
            System.out.println("我来了");
        }

        public void explode() {
            System.out.println("我裂开了");
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            System.out.println("postProcessBeforeInitialization " + beanName);
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            System.out.println("postProcessAfterInitialization " + beanName);
            return bean;
        }
    }
}
