package com.imzhizi.javalearning.语言基础;

import lombok.Data;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * created by zhizi
 * on 3/29/20 11:29
 */
public class nSerializable {
    /**
     * 类的可序列化性通过实现java.io.Serializable接口开启, Serializable接口没有方法和属性，只是一个识别类可被序列化的标志
     * 序列化的好处
     * 一是实现了数据的持久化，通过序列化可以把数据永久地保存到硬盘上
     * 二是利用序列化实现**远程通信**, 即在网络上传送对象的字节序列
     * <p>
     * 开头的魔数0x73表示普通对象，72表示类的描述符号，71表示类描述符为引用类型
     */
    @Data
    static class Demo implements Serializable {
        private static final long serialVersionUID = 63316110461919923L;
        private String name;
        private String pwd;
    }

    /**
     * aced 0005 7372 0038 636f 6d2e 696d 7a68
     * 697a 692e 6a61 7661 6c65 6172 6e69 6e67
     * 2ee8 afad e8a8 80e5 9fba e7a1 802e 6e53
     * 6572 6961 6c69 7a61 626c 6524 4465 6d6f
     * 9f26 9257 bacd 7158 0200 024c 0004 6e61
     * 6d65 7400 124c 6a61 7661 2f6c 616e 672f
     * 5374 7269 6e67 3b4c 0003 7077 6471 007e
     * 0001 7870 7400 0434 3332 3574 0004 3132
     * 3334
     */
    @Test
    public void writeObject() throws IOException {
        Demo demo = new Demo();
        demo.setName("4325");
        demo.setPwd("1234");

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("object"));
        oos.writeObject(demo);
        oos.close();
    }

    @Test
    public void readObject() throws IOException, ClassNotFoundException {
        File file = new File("object");
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        Demo demo = (Demo) ois.readObject();
        System.out.println(demo.toString());
        ois.close();
    }


    /**
     * aced 0005 7372 0013 6a61 7661 2e75 7469
     * 6c2e 4172 7261 794c 6973 7478 81d2 1d99
     * c761 9d03 0001 4900 0473 697a 6578 7000
     * 0000 0477 0400 0000 0473 7200 116a 6176
     * 612e 6c61 6e67 2e49 6e74 6567 6572 12e2
     * a0a4 f781 8738 0200 0149 0005 7661 6c75
     * 6578 7200 106a 6176 612e 6c61 6e67 2e4e
     * 756d 6265 7286 ac95 1d0b 94e0 8b02 0000
     * 7870 0000 0005 7371 007e 0002 0000 0006
     * 7371 007e 0002 0000 0007 7371 007e 0002
     * 0000 0008 78
     * <p>
     * 仔细看，能看到应该就是
     * 8b02 0000 7870 0000 0005
     * 7371 007e 0002 0000 0006
     * 7371 007e 0002 0000 0007
     * 7371 007e 0002 0000 0008
     */
    @Test
    public void writeList() throws IOException {
        List<Integer> list = new ArrayList<>();
        list.add(4);
        list.add(6);
        list.add(7);
        list.add(8);

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("list"));
        System.out.println(list);
        oos.writeObject(list);
        oos.close();
    }

    @Test
    public void readList() throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("list"));
        ArrayList read = (ArrayList) ois.readObject();
        System.out.println(read);
        ois.close();
    }

    static class Demo2 implements Serializable {
        static int name;
        static int pwd;

        public Demo2(int n, int p) {
            name = n;
            pwd = p;
        }

        private void writeObject(java.io.ObjectOutputStream s) throws IOException, IllegalAccessException {
            Field[] fields = getClass().getDeclaredFields();
            ArrayList<String> list = new ArrayList<>();
            for (Field f : fields) {
                list.add(f.getGenericType().toString() + "$" + f.getName() + "#" + f.get(this));
            }
            String str = String.join(";", list);

            s.writeUTF(getClass().getName());
            s.writeUTF("@@@");
            s.writeUTF(str);
            System.out.println(getClass().getName());
            System.out.println(str);
        }

        // 有点麻烦，不想写了
        private void readObject(java.io.ObjectInputStream s) {
        }
    }

    @Test
    public void 自定义写对象() throws IOException {
        Demo2 demo = new Demo2(1234, 5678);

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("object"));
        oos.writeObject(demo);
        oos.close();
    }
}