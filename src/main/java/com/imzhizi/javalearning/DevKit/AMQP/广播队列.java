package com.imzhizi.javalearning.DevKit.AMQP;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;

/**
 * created by zhizi
 * on 5/13/20 10:08
 */
public class 广播队列 {
    static class Provider {
        public static void main(String[] args) throws IOException {
            Connection connection = MQUtil.getNewConnection();
            Channel channel = MQUtil.getNewChannel(connection);

            //exchange的名称和类型
            // 可以看到exchange发送的所有消息都被分发给了各个消费者
            // 设置 routingKey 似乎没啥用
            channel.exchangeDeclare("account", BuiltinExchangeType.FANOUT);
            channel.basicPublish("account", "", null, "verify your ".getBytes());
            channel.basicPublish("account", "", null, "verify your phone".getBytes());

            MQUtil.close(connection, channel);
        }
    }

    static class Consumer {
        public static void main(String[] args) {
            new Thread(() -> {
                Connection connection = MQUtil.getNewConnection();
                Channel channel = MQUtil.getNewChannel(connection);
                try {
                    channel.exchangeDeclare("account", BuiltinExchangeType.FANOUT);
                    // 创建并绑定临时队列
                    String queueName = channel.queueDeclare().getQueue();
                    channel.queueBind(queueName, "account", "");
                    MQUtil.sleepConsuming(channel, queueName,"A");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                Connection connection = MQUtil.getNewConnection();
                Channel channel = MQUtil.getNewChannel(connection);
                try {
                    channel.exchangeDeclare("account", BuiltinExchangeType.FANOUT);
                    // 创建并绑定临时队列
                    String queueName = channel.queueDeclare().getQueue();
                    channel.queueBind(queueName, "account", "");
                    MQUtil.sleepConsuming(channel, queueName,"B");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                Connection connection = MQUtil.getNewConnection();
                Channel channel = MQUtil.getNewChannel(connection);
                try {
                    channel.exchangeDeclare("account", BuiltinExchangeType.FANOUT);
                    // 创建并绑定临时队列
                    String queueName = channel.queueDeclare().getQueue();
                    channel.queueBind(queueName, "account", "");
                    MQUtil.sleepConsuming(channel, queueName,"C");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }


    }
}
