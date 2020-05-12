package com.imzhizi.javalearning.spring;

import lombok.Data;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.Set;

/**
 * created by zhizi
 * on 3/14/20 21:28
 */
public class DITest {
    /**
     * 主要还是讨论怎么用起来爽的问题，Bean一个个都通过xml初始化、硬编码既不灵活也不方便
     * 需要一些简单易维护的方式用于创恢复bean直接点饿依赖
     */
    @Test
    public void IOCTest() {
        GenericApplicationContext context = new GenericXmlApplicationContext("DITest.xml");
        Teacher teacher = (Teacher) context.getBean("teacher");
        System.out.println(teacher);
        Student student = (Student) context.getBean("student");
        System.out.println(student);
        Directive direct = (Directive) context.getBean("direct");
        System.out.println(direct);
        context.registerShutdownHook();
    }

    @Data
    static class Teacher {
        private Integer code;
        private Set<AClass> classes;
        private School school;
        private Course course;
        private Location location;

        public Teacher(Location location, Course course) {
            System.out.println("creating teacher");
            this.location = location;
            this.course = course;
        }

        public void setSchool(School school) {
            System.out.println("setting school");
            this.school = school;
        }

        @PreDestroy
        public void done() {
            System.out.println("下班了……");
        }
    }

    @Data
    static class Student {
        private Integer code;
        @Resource(name = "school") //无效
        private School location;
        @Autowired  //无效
        @Qualifier("ac2")
        private AClass aClass;
    }

    @Data
    static class Directive {
        private Integer code;
        private Title title;
    }

    @Data
    static class Location {
        private String city;
        private String zipCode;

        public Location() {
            System.out.println("creating location");
        }
    }

    @Data
    static class Course {
        private String name;

        public Course() {
            System.out.println("creating course");
        }
    }

    @Data
    static class School {
        private String name;
        private String principal;

        public School() {
            System.out.println("creating  school");
        }
    }

    @Data
    static class AClass {
        private Integer code;
        private Integer count;

        public AClass() {
            System.out.println("creating clazz");
        }
    }

    @Data
    static class Title {
        private String name;
        private Integer level;
    }
}
