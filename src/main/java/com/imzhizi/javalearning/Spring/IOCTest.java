package com.imzhizi.javalearning.Spring;

import lombok.Data;
import lombok.Getter;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Set;

/**
 * created by zhizi
 * on 3/14/20 21:28
 */
public class IOCTest {
    @Test
    public void DITest() {
        ApplicationContext context = new ClassPathXmlApplicationContext("IOCTest.xml");
        Teacher teacher = (Teacher) context.getBean("teacher");
        System.out.println(teacher);
    }

    @Data
    static class Teacher {
        private Integer code;
        private Set<Clazz> clazz;
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
    static class Clazz {
        private Integer code;
        private Integer count;

        public Clazz() {
            System.out.println("creating clazz");
        }
    }
}
