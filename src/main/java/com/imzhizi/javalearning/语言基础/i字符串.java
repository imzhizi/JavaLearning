package com.imzhizi.javalearning.语言基础;

import org.junit.Assert;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * created by zhizi
 * on 3/23/20 15:30
 */
public class i字符串 {
    /**
     * String 类定义被 final 修饰, 所以它不可被继承
     * ### 常量
     * 作为编译时能获得的常量, JVM 设计了字符串常量池在方法区, 现在放在了「元空间」
     * 最基本的一点, "J"+"ava" 和 "Ja" +"va" 在编译期能得到最终字符串
     * 所以它们互相之间`==`都是 `true`，因为编译期得到的最终字符串都成为 #字符串常量池 中的一员
     * <p>
     * ### 涉及变量时
     * 另外一方面, 构造函数得到的字符串和字面量的地址是不同的
     * 一个基本的解释是构造函数的执行流程：
     * 首先在「字符串常量池」中查找原始字符串, 若存在则拷贝一份到堆内存中, 然后将地址传给虚拟机栈
     * 如果「字符串常量池」中不存在， 则在常量池中创建一份，然后拷贝到堆内存中
     * <p>
     * ### 运算符处理
     * 那么常用的「+」如何处理呢？能够看到的是一旦有变量参与字符串的拼接, 地址就会不同,
     * 根据反编译得到的情况看, Java 在执行「+」操作时会创建一个 StringBuilder, 将「+」操作改成「append」来执行
     * 在这种情况下, 如果在循环中使用了 StringBuilder, 将会大量的 StringBuilder 用于字符串拼接
     * 但「"xx"+"yy"+"zz"+s1」 这种只会产生一个 StringBuilder, 然后逐个「append」完成操作
     */
    @Test
    public void StringTest() {
        String s = "Java";
        String s1 = "a" + "Java";
        String s2 = "aJ" + "ava";
        String s3 = "a" + s;
        String s4 = new String("aJava");
        System.out.println(s1 == s2);
        System.out.println(s1 == s3);
        System.out.println(s1 == s3);
        System.out.println(s3 == s4);
        System.out.println("aJava" == s1);
        System.out.println("aJava" == s3);
        System.out.println("aJava" == s4);
    }

    /**
     * ### 常量池的实现细节
     * 常量池是一个类似于Hashtable的结构，但不支持扩容，默认容量为1009，可通过「-XX:StringTableSize=」设置
     * 总之一旦超过size后，更多结点只能无限拉链，所以效率会越来越低
     * <p>
     * ### 哪里不太对？
     * 不觉得很奇怪吗？「new String()」的时候，直接创建字符串就行了，为什么还要先在常量池中创建呢？
     * 事实上也是这样，只有在直接出现的「"xxx"」的字面量才会被放到常量池中，然后执行「new String()」方法
     * 在下面的测试中，可以看到使用了字符串拼接后的变量「intern()」之后和字面量地址居然相同了
     * 这是因为使用字符串拼接，那么目标字符串从来没有显式地出现过，也就没有被保存在常量池中
     * 执行「intern()」时发现常量池中根本不存在该字面量，所以会创建该字面量并将引用调整到这里并返回
     * 但假如执行「intern()」时发现常量池中已经存在该变量，会直接返回该变量
     * <p>
     * ### 字符串对象到底存在哪里
     * 有说法会说其实字符串常量池中存放的也不是字符串本身，而是字符串的引用，真正的字符串同样存在于堆中
     * 像前文所说，「new String」时假如使用了非字面量，那么直接在堆内存中即可，而执行了「intern」相当于检查常量池表中是否有该字符串
     * 不存在则创建索引，而不是真的拷贝了一份，若存在则直接返回索引
     * <p>
     * ### 如何正确地使用「intern()」
     * 比如说 FastJson，理论上说 json-key 应该比较固定，如果使用「intern()」方法就可以大量减少对象创建，降低内存压力和GC压力
     * 但是如果 json-key 会大量变化的话，就会给常量池带来很大负担，一旦超过常量池的容量开始大量拉链，性能会急剧下降
     * <p>
     * ### valueOf 与 「+」
     * 经过上文不难发现 变量+"" 创建字符串并不是一种很好的用法，因为想要执行「+」本身就需要调用变量的 toString 方法
     * 之后还需要创建 StringBuilder 来进行字符串拼接，多次一举，空耗内存
     * <p>
     * 参考 [深入解析String#intern - 美团技术团队](https://tech.meituan.com/2014/03/06/in-depth-understanding-string-intern.html )
     */
    @Test
    public void InternTest1() {
        String s = new String("1");
        String s2 = "1";
        s.intern();
        System.out.println(s == s2); //false
    }

    @Test
    public void InternTest2() {
        String s3 = new String("1") + new String("1");
        String s4 = "11";
        String s5 = s3.intern();
        System.out.println(s3 == s4); //false
        System.out.println(s4 == s5); // true
    }

    @Test
    public void InternTest3() {
        String s3 = new String("1") + new String("1");
        s3.intern();
        String s4 = "11";
        System.out.println(s3 == s4); //true
    }

    @Test
    public void InternTest4() {
        String s3 = "1" + new String("1");
        s3.intern();
        String s4 = "11";
        System.out.println(s3 == s4); //true
    }

    @Test
    public void InternTest5() {
        String s = "1";
        String s3 = s + s;
        s3.intern();
        String s4 = "11";
        System.out.println(s3 == s4); //true
    }

    /**
     * ### 编码相关
     * 1. Unicode 规定码点的范围从 `U+0000` 到 `U+10FFFF`
     * 其中最常用的 `U+0000`到`U+FFFF`为的基本平面(code plane)
     * 而对于补充平面的`U+010000`到`U+10FFFF`分成16个补充平面
     * 对于最常用的基本平面, 用4个字节(代码单元 code unit)表示
     * 而补充平面则用一对代码单元来表示, 前者是基本平面中空闲的 2048 个区域, 后者则是真正的位置, 两者拼起来对应 Unicode 的码点
     * 2. 最基本的方法, `U+0000`共32位, 所以用 `UTF-32` 可以把码点一一对应的储存起来
     * 所以说 UTF-32 可以说是 Unicode 的完美实现
     * 3. 但是`UTF-32`太浪费, 所以有了 `UTF-16`和`UTF-8`, 这两个都是变长字符集, 区别在于长度方式不同
     * <p>
     * ### UTF-8 与 UTF-16
     * 根据维基, 对于一个码点, `UTF-16` 可以是4位, 6位
     * 而`UTF-8`则为1到6位均有可能, 具体表示范围见维基
     * [ UTF-8的编码方式 - 维基百科，自由的百科全书 ](https://zh.wikipedia.org/wiki/UTF-8#UTF-8%E7%9A%84%E7%B7%A8%E7%A2%BC%E6%96%B9%E5%BC%8F)
     * [ Comparison of Unicode encodings - Wikipedia ](https://en.wikipedia.org/wiki/Comparison_of_Unicode_encodings#Eight-bit_environments)
     */
    @Test
    public void encoding() {
        "你".codePoints().forEach((code) -> System.out.println(code + ": " + Integer.toHexString(code)));
        "\ud835\udd46".codePoints().forEach((code) -> System.out.println(code + ": " + Integer.toHexString(code)));
        "\ud835\udf46".codePoints().forEach((code) -> System.out.println(code + ": " + Integer.toHexString(code)));

        System.out.println("\u4f60");
        System.out.println("\ud835\udd46");
        System.out.println("\uD835\udf46");
    }

    /**
     * ### 默认编码
     * 通过`Charset.defaultCharset()`可以获得默认编码, 因此下文两个 bytes 是一样的
     * 可以看到 String 和 byte 之间存在着千思万缕的联系，String 就有这多个和 bytes[] 相关的构造方法
     * 例如 String(byte[] bytes, Charset charset)
     * 该构造方法是指通过 charset 来解码指定的 byte 数组，将其解码成 unicode 的 char[] 数组，来构造成 String
     * 如果没有指明解码使用的字符集的话，那么 StringCoding 的 decode 方法首先调用系统的默认编码格式
     * 我使用的是 macOS，可以看到默认编码是 UTF-8，所以 System.out.println(new String(codesUTF8)) 也能正确执行
     * todo 字节数组如何「decode」为字符数组看了一下感觉非常相当复杂，之后再看吧
     */
    @Test
    public void encoding1() {
        System.out.println(Charset.defaultCharset());
        byte[] codesUTF8 = "你".getBytes(StandardCharsets.UTF_8);
        byte[] codesDefault = "你".getBytes();
        Assert.assertArrayEquals(codesUTF8, codesDefault);
        System.out.println(new String(codesUTF8));
    }

    /**
     * ### 那么`UTF-16`和`UTF-8`哪个好呢?
     * 并没有绝对的好坏，对于西文字符, 每个字符在`UTF-8`中仅需要 1 字节, 而在 `UTF-16`中则需要 4 字节
     * 但对于中文而言, `UTF-8`比较浪费, 因为中文所处的区块在`UTF-8`中需要3个字节，而`UTF-16`2个字节直接就能表示,
     * 所以说，每存储一个中文字符，`UTF-8`都会浪费一个字节，执行以下代码可以看到
     * PS：还可以看到 UTF_16 都会用-2, -1做开始，应该是用来做编码识别
     */
    @Test
    public void encoding2() throws UnsupportedEncodingException {
        System.out.println(Arrays.toString("b".getBytes(StandardCharsets.UTF_8)));
        System.out.println(Arrays.toString("b".getBytes(StandardCharsets.UTF_16)));

        System.out.println(Arrays.toString("你是谁".getBytes(StandardCharsets.UTF_8)));
        System.out.println(Arrays.toString("你是谁".getBytes(StandardCharsets.UTF_16)));
        System.out.println(Arrays.toString("你是谁".getBytes("Unicode")));

        System.out.println(Arrays.toString("中文真的不一样呦".getBytes(StandardCharsets.UTF_8)));
        System.out.println(Arrays.toString("中文真的不一样呦".getBytes(StandardCharsets.UTF_16)));
        System.out.println(Arrays.toString("中文真的不一样呦".getBytes("Unicode")));
    }

    // 而对于辅助平面码点构成的字符串而言, `UTF-16` 的长度是2+3*(2+2), `UTF-8` 则是 3*[(1+1)+(1+1)]，相当于没有区别
    @Test
    public void encoding3() {
        System.out.println(Arrays.toString("\uD835\uDf46".getBytes(StandardCharsets.UTF_8)));
        System.out.println(Arrays.toString("\uD835\uDf46".getBytes(StandardCharsets.UTF_16)));
        System.out.println("\uD801\uDf46\uD801\uDf46\uD801\uDf46".getBytes(StandardCharsets.UTF_8).length);
        System.out.println(Arrays.toString("\uD801\uDf46\uD801\uDf46\uD801\uDf46".getBytes(StandardCharsets.UTF_8)));
        System.out.println("\uD801\uDf46\uD801\uDf46\uD801\uDf46".getBytes(StandardCharsets.UTF_16).length);
        System.out.println(Arrays.toString("\uD801\uDf46\uD801\uDf46\uD801\uDf46".getBytes(StandardCharsets.UTF_16)));
    }

    // 可以看到，一个辅助平面的字符对于 char 而言是两个字符，所以使用 char 其实是一种危险的东西，尽量不要直接使用 char
    @Test
    public void encoding4() {
        System.out.println("\uD835\uDf46");
        System.out.println("\uD835\uDf46".length());
        System.out.println("\uD835\uDf46\uD835\uDf46".toCharArray().length);
    }
}