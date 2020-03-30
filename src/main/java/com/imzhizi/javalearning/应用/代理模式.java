package com.imzhizi.javalearning.应用;

import org.junit.Test;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * created by zhizi
 * on 3/28/20 20:06
 */
public class 代理模式 {
    /**
     * 什么是代理模式？
     * 指存在一个代理类，能够帮助委托类处理消息、过滤消息、把消息转发给委托类，执行委托方法并进行事后处理
     * 关键在于，代理类看起来应该是和被代理类一摸一样的，在实现上其实表现为代理类实现了委托类的接口
     * 但代理类并不真的实现那些方法，而是通过引入真正的委托对象，来间接执行方法，这个间接就是代理可以起作用的地方
     * 比如AOP就基于代理模式实现
     */
    interface Human {
        void eat();

        void sleep();
    }

    interface BeforeAfter {
        void before();

        void after();
    }

    static class Asian implements Human {
        @Override
        public void eat() {
            System.out.println("eat noodles");
        }

        @Override
        public void sleep() {
            System.out.println("睡就完了呗");
        }
    }

    static class EatBA implements BeforeAfter {
        @Override
        public void before() {
            System.out.println("make and boil noodles");
        }

        @Override
        public void after() {
            System.out.println("clean bows and table");
        }
    }

    static class StaticHumanProxy implements Human {
        private static StaticHumanProxy instance;
        private Human human;
        private BeforeAfter beforeAfter;

        public static StaticHumanProxy getInstance() {
            return instance;
        }

        public void init(Human human, BeforeAfter beforeAfter) {
            this.human = human;
            this.beforeAfter = beforeAfter;
        }

        @Override
        public void eat() {
            beforeAfter.before();
            human.eat();
            beforeAfter.after();
        }

        @Override
        public void sleep() {

        }
    }

    static class DynamicHumanProxy {
        private Human human;
        private BeforeAfter beforeAfter;

        public DynamicHumanProxy(Human human, BeforeAfter beforeAfter) {
            this.human = human;
            this.beforeAfter = beforeAfter;
        }

        public Human create() {
            final Class<?>[] interfaces = new Class[]{Human.class};
            final HumanInvocationHandler handler = new HumanInvocationHandler(human, beforeAfter);
            return (Human) Proxy.newProxyInstance(Human.class.getClassLoader(), interfaces, handler);
        }

        static class HumanInvocationHandler implements InvocationHandler {
            private final Human human;
            private final BeforeAfter beforeAfter;

            public HumanInvocationHandler(Human human, BeforeAfter beforeAfter) {
                this.human = human;
                this.beforeAfter = beforeAfter;
            }

            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                beforeAfter.before();
                Object ret = method.invoke(human, args);
                beforeAfter.after();
                return ret;
            }
        }
    }

    /**
     * ### 什么是静态代理
     * 就是手工实现的代理，静态代理几乎是没意义的，静态代理意味着要去手动适配每一个方法，一旦有改动都要做相应改动
     * <p>
     * ### 既然静态代理不可接受，所以研制了动态代理
     * 什么是动态代理，其实就是不手动去适配每一个类，但又能够实现每个方法的代理，节约代码量
     * 如何达到这样的目的呢？这就需要借助 Java API 的帮忙
     * 首先是方法的执行策略，也就是如何对方法进行调用，如果希望进行消息处理、消息过滤等，就应该在 InvocationHandler 的 invoke 中定义
     * 但更重要的，我们需要一个「看起来和委托类一样」的代理类，这个由 Proxy.newProxyInstance 通过反射生成
     * <p>
     * ### 看起来效果不错
     * 但有个比较明显的问题是Proxy只能代理接口，对于普通类则无能为力
     */
    @Test
    public void 动态静态代理() {
        StaticHumanProxy staticProxy = StaticHumanProxy.getInstance();
        staticProxy.init(new Asian(), new EatBA());
        staticProxy.sleep();

        System.out.println("*******");

        DynamicHumanProxy proxy = new DynamicHumanProxy(new Asian(), new EatBA());
        Human human = proxy.create();
        human.sleep();
    }

    /**
     * CGLib 是一个类库，它可以在运行期间动态的生成字节码，动态生成代理类
     * 它和 Proxy 用法相近，不同的是它进行更深度的封装，只需要一层，可以很方便的获取代理类
     */
    @Test
    public void CG代理() {
        CGlibProxy proxy = new CGlibProxy(new EatBA());
        Asian asianProxy = proxy.getProxy(Asian.class);
        asianProxy.eat();
        asianProxy.sleep();

        CGlibProxy proxy2 = new CGlibProxy(new BeforeAfter() {
            @Override
            public void before() {
                System.out.println("洗脸、刷牙、洗脚");
            }

            @Override
            public void after() {
                System.out.println("洗脸、刷牙、吃早饭");
            }
        });
        Asian asianProxy2 = proxy2.getProxy(Asian.class);
        asianProxy.eat();
        asianProxy2.sleep();
    }

    static class CGlibProxy implements MethodInterceptor {
        private BeforeAfter beforeAfter;

        public CGlibProxy(BeforeAfter beforeAfter) {
            this.beforeAfter = beforeAfter;
        }

        public <T> T getProxy(Class<T> cls) {
            return (T) Enhancer.create(cls, this);
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            if (beforeAfter == null) return methodProxy.invokeSuper(o, objects);
            beforeAfter.before();
            Object result = methodProxy.invokeSuper(o, objects);
            beforeAfter.after();
            return result;
        }
    }

    @Test
    public void CG() {
        CGlibProxy proxy = new CGlibProxy(new EatBA());
        Class clazz = proxy.getProxy(Asian.class).getClass();

        System.out.println(clazz.getSuperclass());
        System.out.println("***declaredField***");
        for (Field declaredField : clazz.getDeclaredFields()) {
            System.out.println(declaredField.toString());
        }

        System.out.println("***declaredMethod***");
        for (Method declaredMethod : clazz.getDeclaredMethods()) {
            System.out.println(declaredMethod.toString());
        }


    }
}
