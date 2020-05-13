package com.imzhizi.javalearning.DevKit.AMQP;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;

/**
 * created by zhizi
 * on 5/13/20 10:53
 */
public class 路由静态 {
    static String exchange = "logs";
    static String routingInfo = "info";
    static String routingWarn = "warn";

    static class Producer {
        public static void main(String[] args) throws IOException {
            Connection connection = MQUtil.getNewConnection();
            Channel channel = MQUtil.getNewChannel(connection);

            // exchange的名称和类型, 路由模式
            // 可以看到exchange发送的所有消息都被分发给了各个消费者
            // 设置 routingKey 似乎没啥用

            channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT);
            channel.basicPublish(exchange, routingInfo, null, "thread pool is ready".getBytes());
            channel.basicPublish(exchange, routingWarn, null, "xxx not found, use default".getBytes());
            channel.basicPublish(exchange, "", null, "xxx not found, use default".getBytes());

            MQUtil.close(connection, channel);
        }
    }

    static class Consumer {
        public static void main(String[] args) {
            new Thread(() -> {
                Connection connection = MQUtil.getNewConnection();
                Channel channel = MQUtil.getNewChannel(connection);
                try {
                    // 创建exchange
                    channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT);
                    // 创建临时队列
                    String queueName = channel.queueDeclare().getQueue();
                    // 创建并绑定临时队列
                    channel.queueBind(queueName, exchange, routingInfo);
                    MQUtil.consuming(channel, queueName, "A");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                Connection connection = MQUtil.getNewConnection();
                Channel channel = MQUtil.getNewChannel(connection);
                try {
                    channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT);
                    String queueName = channel.queueDeclare().getQueue();
                    channel.queueBind(queueName, exchange, routingWarn);
                    MQUtil.consuming(channel, queueName, "B");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                Connection connection = MQUtil.getNewConnection();
                Channel channel = MQUtil.getNewChannel(connection);
                try {
                    channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT);
                    String queueName = channel.queueDeclare().getQueue();
                    channel.queueBind(queueName, exchange, routingInfo);
                    channel.queueBind(queueName, exchange, routingWarn);
                    MQUtil.consuming(channel, queueName, "C");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                Connection connection = MQUtil.getNewConnection();
                Channel channel = MQUtil.getNewChannel(connection);
                try {
                    channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT);
                    String queueName = channel.queueDeclare().getQueue();
                    channel.queueBind(queueName, exchange, "");
                    MQUtil.consuming(channel, queueName, "D");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
