package com.imzhizi.javalearning.Spring;

import com.imzhizi.javalearning.base.StuDetail;
import com.imzhizi.javalearning.base.Teacher;
import com.imzhizi.javalearning.base.WorkAbility;
import org.junit.Test;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

/**
 * created by zhizi
 * on 3/13/20 21:41
 */
public class IOCTest {
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

}
