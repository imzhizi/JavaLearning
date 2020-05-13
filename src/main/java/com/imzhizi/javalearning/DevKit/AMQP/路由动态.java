package com.imzhizi.javalearning.DevKit.AMQP;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;

/**
 * created by zhizi
 * on 5/13/20 15:12
 */
public class 路由动态 {
    static String exchange = "logs";
    static String levelDefault = "level.info";
    static String level0 = "level.info.*";
    static String levelInfo = "level.info.#";
    static String levelWarn = "level.info.warn.#";
    static String levelError = "level.info.warn.error.#";

    static class Producer {
        public static void main(String[] args) throws IOException {
            Connection connection = MQUtil.getNewConnection();
            Channel channel = MQUtil.getNewChannel(connection);

            // exchange的名称和类型, 路由模式
            // 这里使用了TOPIC类型的exchange
            // 重点就在于 levelKey 的设计，可以看到使用了通配符
            // 经过测试，default 消息能被 info 级别收到，也就是 # 能匹配0～n个word
            // 而 level0 能够接收 info 级别的消息，却接收不到 default 级别的消息，说明 * 能且仅必须匹配一个 word

            channel.exchangeDeclare(exchange, BuiltinExchangeType.TOPIC);
            channel.basicPublish(exchange, levelDefault, null, "started".getBytes());
            channel.basicPublish(exchange, levelInfo, null, "ok!".getBytes());
            channel.basicPublish(exchange, levelWarn, null, "warn warn".getBytes());
            channel.basicPublish(exchange, levelError, null, "error error error".getBytes());

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
                    channel.exchangeDeclare(exchange, BuiltinExchangeType.TOPIC);
                    // 创建临时队列
                    String queueName = channel.queueDeclare().getQueue();
                    // 创建并绑定临时队列
                    channel.queueBind(queueName, exchange, levelInfo);
                    MQUtil.consuming(channel, queueName, "A");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                Connection connection = MQUtil.getNewConnection();
                Channel channel = MQUtil.getNewChannel(connection);
                try {
                    channel.exchangeDeclare(exchange, BuiltinExchangeType.TOPIC);
                    String queueName = channel.queueDeclare().getQueue();
                    channel.queueBind(queueName, exchange, levelWarn);
                    MQUtil.consuming(channel, queueName, "B");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                Connection connection = MQUtil.getNewConnection();
                Channel channel = MQUtil.getNewChannel(connection);
                try {
                    channel.exchangeDeclare(exchange, BuiltinExchangeType.TOPIC);
                    String queueName = channel.queueDeclare().getQueue();
                    channel.queueBind(queueName, exchange, levelError);
                    MQUtil.consuming(channel, queueName, "C");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                Connection connection = MQUtil.getNewConnection();
                Channel channel = MQUtil.getNewChannel(connection);
                try {
                    channel.exchangeDeclare(exchange, BuiltinExchangeType.TOPIC);
                    String queueName = channel.queueDeclare().getQueue();
                    channel.queueBind(queueName, exchange, level0);
                    MQUtil.consuming(channel, queueName, "D");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
