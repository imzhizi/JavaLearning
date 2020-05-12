package com.imzhizi.javalearning.语言基础;

import lombok.Data;
import org.junit.Test;

/**
 * created by zhizi
 * on 3/25/20 09:12
 */
public class a接口 {
    @Test
    public void 接口测试() {
        Student student = new Student();
        student.SayMyName(() -> System.out.println("方法执行成功"));
    }

    interface Say {
        String words="ss";
        void func();
    }

    class SSS implements Say{

        @Override
        public void func() {

        }
    }

    @Data
    static class Student {

        private String name;

        public void SayMyName(Say say) {
            System.out.println("my name is " + name);
            say.func();
        }

    }
}
