package com.imzhizi.javalearning.base;

import java.util.Date;

/**
 * created by zhizi
 * on 3/7/20 12:50
 */
public class StuDetail extends Student {
    private Integer age;
    private Date date;
    public static int count = 0;

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
