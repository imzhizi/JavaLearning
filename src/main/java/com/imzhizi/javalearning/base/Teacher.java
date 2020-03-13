package com.imzhizi.javalearning.base;

/**
 * created by zhizi
 * on 3/7/20 12:59
 */
public class Teacher implements WorkAbility {
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
