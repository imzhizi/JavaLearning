package com.imzhizi.javalearning.base;

/**
 * created by zhizi
 * on 3/7/20 12:39
 */
public class Student {
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
