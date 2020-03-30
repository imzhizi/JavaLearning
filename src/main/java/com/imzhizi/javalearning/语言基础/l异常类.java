package com.imzhizi.javalearning.语言基础;

import java.io.FileNotFoundException;
import java.nio.channels.FileLockInterruptionException;
import java.nio.file.FileAlreadyExistsException;

/**
 * created by zhizi
 * on 3/30/20 20:51
 */
public class l异常类 {
    /**
     * 可以看到对于不可预料的运行时异常，会导致线程的终止
     * 对于IO异常，则必须要处理
     */
    static class 异常 {

        public static void main(String[] args) {
            for (int i = 0; i < 10; i++) {
                if (i % 3 == 0) {
                    new Thread(() -> {
                        System.out.println(2 / 0);
                        System.out.println("能行吗");
                    }).start();
                } else if (i % 2 == 0) {
                    new Thread(() -> {
                        // throw 后面的语句都是 unreachable，所以不能加更多语句
                        throw new MyException("我异常，我骄傲");
                    }).start();
                } else if (i % 5 == 0) {
                    new Thread(() -> {
                        try {
                            throw new FileNotFoundException("IO异常怎么说");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }).start();
                } else {
                    new Thread(() -> {
                        System.out.println("咱没事");
                    }).start();
                }
            }
        }
    }

    static class MyException extends RuntimeException {
        public MyException(String message) {
            super(message);
        }
    }

    /**
     * 无法为重写的方法抛出更多类型的异常
     */
    static class Parent {
        public void test(int i) throws FileNotFoundException, FileAlreadyExistsException {
            if (i == 0) throw new FileNotFoundException("exp 1");
            if (i == 1) throw new FileAlreadyExistsException("exp 2");
        }
    }

    interface Mom {
        void test2() throws FileLockInterruptionException;
    }

    static class Son extends Parent implements Mom {
        @Override
        public void test(int i) throws FileNotFoundException, FileAlreadyExistsException {
//            if(i==3) throw new FileLockInterruptionException();
            super.test(i);
        }

        @Override
        public void test2() throws FileLockInterruptionException {
//            throw new FileNotFoundException();
        }
    }
}
