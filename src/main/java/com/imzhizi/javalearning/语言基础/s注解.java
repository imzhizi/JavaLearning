package com.imzhizi.javalearning.语言基础;

import lombok.Data;
import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * created by zhizi
 * on 3/18/20 16:11
 */
public class s注解 {
    /**
     * - 基本概念
     * DK 5 版本引入的一个新特性
     * 与类、接口、枚举是同一层次的
     * 可以用在包、类、字段、方法、局部变量、方法参数之前
     * - 注解有什么用呢？
     * 编写文档
     * 类似于源代码中的`@param`、`@return` javadoc 的时候可以直接产生文档
     * 功能开关/快速传值
     * 编译检查
     * 使用 @Override 可以在编译时检测该方法是否来自于父类/父接口
     * 使用 @Deprecated 指明方法已经有更好地实现，该方法已不推荐使用
     * 使用 @SuppressWarning 可以压制警告，避免IDE对某一个类的warning
     * - 元注解
     * 以下是一个注解的定义， 可以看到注解的定义中也使用到了注解
     * 这些注解就是 meta-annotation，包括@Target、@Retention、@Documented、@Inherited
     * 功能分别是作用位置（类、方法、属性……）作用阶段（源代码阶段、字节码阶段、运行时）
     * 在 Javadoc 生成文档时是否加入文档中，类被继承时注解是否在子类中生效
     * - 注解的本质
     * 注解使用的关键字是 @interface，可见和接口有很大关系
     * 经过class文件反编译，不难看到注解本质上是一个接口
     * 这个接口默认继承 extends java.lang.annotation.Annotation
     * 所以注解的定义和接口的定义基本相同
     * - 注解定义
     * 注解和接口一样，只能够包含虚方法，这些成员方法被称为注解的属性，方法的返回值只能为基本数据类型、枚举和字符串数组
     * 配合元注解就形成了一个基本的注解，JDK 1.8 之后接口支持使用 default 关键字
     * - 注解是如何工作的？
     * 形式上，注解是加在类名称、属性、方法……的前面，有些仅有注解，有些会包含参数
     * 包含的参数就是在为注解的属性(成员方法)赋值，若属性存在默认值，也可以不赋值
     * 换言之，自行定义的注解相当于一个接口，而 JVM 会为其生成一个 impl，每个属性的赋值就相当于该实现对每个成员方法的实现
     * 使用时，通过类 Class 对象的 getAnnotation 方法，即可获得注解对象，进而获得每个属性的值
     * <p>
     * 有些时候，注解仅仅作为开关而存在，如 @Bean 就意味着把一个对象加入容器中，无需多余的赋值 isAnnotationPresent()
     * 特别的一点，如果注解仅有一个属性需要赋值且属性名为value，那么可以省略value直接赋值 @GetMapping("/hello")
     */

    /**
     * 测试一
     */
    @Test
    public void 从注解获取配置() throws Exception {
        Claim claim = s注解.class.getMethod("执行对象方法").getAnnotation(Claim.class);
        String className = claim.className();
        String methodName = claim.methodName();

        执行对象方法(className, methodName);
    }

    @Claim(className = "com.imzhizi.javalearning.语言基础.注解.Teacher", methodName = "work")
    public void 执行对象方法(String className, String methodName) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, NoSuchFieldException, InvocationTargetException {
        Class cls = Class.forName(className);
        Method method = cls.getMethod(methodName);
        Object object = cls.newInstance();

        Field field = cls.getDeclaredField("name");
        field.setAccessible(true);
        field.set(object, "刘老师");

        method.invoke(object);
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Claim {
        String className();

        String methodName();
    }


    @Data
    static class Teacher {
        private String name;

        public void work() {
            System.out.println(name + " 工作去了");
        }
    }

    /**
     * 测试二
     */
    @Test
    public void 模仿Bean() throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        Method[] methods = s注解.class.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Melon.class)) {
                Object object = method.invoke(this);
                System.out.println(object);
            }
        }
    }

    @Melon
    public Teacher teacher() {
        Teacher teacher = new Teacher();
        teacher.setName("teacher");
        return teacher;
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Melon {
    }
}
