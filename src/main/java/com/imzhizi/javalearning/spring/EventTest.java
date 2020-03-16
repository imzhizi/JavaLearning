package com.imzhizi.javalearning.spring;

import lombok.Data;
import org.junit.Test;
import org.springframework.context.*;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * created by zhizi
 * on 3/16/20 11:19
 */
public class EventTest {
    /**
     * 通过 ApplicationEvent 类和 ApplicationListener 接口来处理 ApplicationContext 中的事件
     * 如果一个 Bean 实现了 ApplicationListener 接口, 那么ApplicationEvent 被发布时, bean 会被通知
     */
    @Test
    public void 事件处理器测试() {
        ConfigurableApplicationContext context =
                new ClassPathXmlApplicationContext("EventTest.xml");
        context.start();
        EGBean egBean = (EGBean) context.getBean("egBean");
        System.out.println(egBean);
        context.stop();
        context.close();
    }

    @Data
    static class EGBean {
        private String name;
    }


    static class CStartEventHandler implements ApplicationListener<ContextStartedEvent> {
        @Override
        public void onApplicationEvent(ContextStartedEvent event) {
            System.out.println("ContextStartedEvent Received");
        }
    }

    static class CStopEventHandler implements ApplicationListener<ContextStoppedEvent> {
        @Override
        public void onApplicationEvent(ContextStoppedEvent event) {
            System.out.println("ContextStoppedEvent Received");
        }
    }

    static class CCloseEventHandler implements ApplicationListener<ContextClosedEvent> {

        @Override
        public void onApplicationEvent(ContextClosedEvent event) {
            System.out.println("ContextClosedEvent Received");
        }
    }

    /**
     * 继承 ApplicationEvent 可以创建一个自定义事件
     * 如何触发自定义事件呢? 需要自定义一个 Publisher, 实现ApplicationEventPublisherAware 接口
     * 将这个 Publisher 同样作为Bean加入容器中, 使用该Bean publish() 可以触发该事件
     * 接着, 该事件的 Handler (实现了ApplicationListener<ApplicationEvent>) 会执行相应的操作
     */
    @Test
    public void 自定义事件测试() {
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("EventTest.xml");
        CustomEventPublisher cvp = (CustomEventPublisher) context.getBean("customEventPublisher");
        cvp.publish();
        cvp.publish();
    }

    static class CustomEvent extends ApplicationEvent {

        /**
         * Create a new ApplicationEvent.
         *
         * @param source the component that published the event (never {@code null})
         */
        public CustomEvent(Object source) {
            super(source);
        }

        public void invoke() {
            System.out.println("myEvent was invoked");
        }
    }

    static class CustomEventPublisher implements ApplicationEventPublisherAware {
        private ApplicationEventPublisher publisher;

        public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
            this.publisher = publisher;
        }

        public void publish() {
            publisher.publishEvent(new CustomEvent(this));
        }
    }

    static class CCustomEventHandler implements ApplicationListener<CustomEvent> {
        @Override
        public void onApplicationEvent(CustomEvent event) {
            event.invoke();
        }
    }
}
