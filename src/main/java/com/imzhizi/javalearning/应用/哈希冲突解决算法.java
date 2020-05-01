package com.imzhizi.javalearning.应用;

import org.junit.Test;

/**
 * created by zhizi
 * on 4/16/20 14:03
 */
public class 哈希冲突解决算法 {
    static int count = 1;
    static int length = 256;
    static int[] table;

    @Test
    public void rehash() {
        String obj = null;
        table = new int[length];
        for (double i = 1.0; i < length * 0.75; i++) {
            obj = "str" + i;
            int index = index(obj.hashCode());
            if (table[index] != 0) {
                index = squareExp(obj.hashCode());
            }
            table[index]++;

            System.out.println(obj.hashCode() + " " + index);
        }
        System.out.println(count + " " + count * 1.0 / length);
    }

    static int index(int hashcode) {
        int h;
        return ((h = hashcode) ^ (h >> 16)) & (length - 1);
    }

    // 线性探测法再散列
    static int linearExp(int hashcode) {
        int col = 1;
        int index = index(hashcode + col++);
        while (table[index] != 0) {
            count++;
            index = index(hashcode + col++);
        }
        return index;
    }

    // 二次探测再散列
    static int squareExp(int hashcode) {
        count++;
        int col = 1;
        int index = index(hashcode + col);
        while (table[index] != 0) {
            count++;
            col = (col < 0 ? col - 1 : col + 1) * -1;
            index = index(hashcode + col * col);
        }
        return index;
    }

}