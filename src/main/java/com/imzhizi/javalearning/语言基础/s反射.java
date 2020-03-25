package com.imzhizi.javalearning.语言基础;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class s反射 {
    @Test
    public void testReflect() throws NoSuchFieldException, IllegalAccessException {
        Student srcObject = new Student("zhizi", "qwer1234", "male");
        Teacher desObject = new Teacher();
        异类反射浅拷贝(srcObject, desObject);
        System.out.println(srcObject);
        System.out.println(desObject);
    }

    public void 异类反射浅拷贝(Object srcObject, Object desObject) throws IllegalAccessException, NoSuchFieldException {
        Assert.assertNotNull("Source must not be null", srcObject);
        Assert.assertNotNull("Destination must not be null", desObject);

        Class srcClazz = srcObject.getClass();
        Set<String> srcFields = Arrays.stream(srcClazz.getDeclaredFields()).map(Field::getName).collect(Collectors.toSet());

        Class desClazz = desObject.getClass();
        Field[] desFields = desClazz.getDeclaredFields();

        for (Field df : desFields) {
            if (srcFields.contains(df.getName())) {
                Field sf = srcClazz.getDeclaredField(df.getName());
                if (sf.getType() == df.getType()) {
                    sf.setAccessible(true);
                    df.setAccessible(true);
                    df.set(desObject, sf.get(srcObject));
                }
            }
        }
    }

    static class Teacher {
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

}
