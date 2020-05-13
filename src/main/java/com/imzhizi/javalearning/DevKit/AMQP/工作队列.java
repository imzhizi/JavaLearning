package com.imzhizi.javalearning.DevKit.AMQP;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;

/**
 * created by zhizi
 * on 5/12/20 17:37
 */
public class 工作队列 {
    static class Producer {
        public static void main(String[] args) throws IOException {
            Connection connection = MQUtil.getNewConnection();
            Channel channel = MQUtil.getNewChannel(connection);
            // 是否开启持久化
            // 是否独占模式
            // 消费后是否自动删除
            channel.queueDeclare("queue", false, false, true, null);
            for (int i = 0; i < 10; i++) {
                // 交换机名称，队列名称，消息额外设置，消息内容
                channel.basicPublish("", "queue", null, new String(i + "th greeting").getBytes());
                // Thread.sleep(1000);
            }
            channel.basicPublish("", "queue", null, new String("bye bye").getBytes());
            channel.basicPublish("", "queue", null, new String("bye bye").getBytes());
            MQUtil.close(connection, channel);
        }
    }

    static class Consumer {
        public static void main(String[] args) {
            Connection connection = MQUtil.getNewConnection();
            new Thread(() -> {
                try {
                    Channel channel = connection.createChannel();
                    channel.basicQos(1);
                    channel.queueDeclare("queue", false, false, true, null);
                    MQUtil.consuming(channel, "queue", "quick");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                try {
                    Channel channel = connection.createChannel();
                    channel.basicQos(1);
                    channel.queueDeclare("queue", false, false, true, null);
                    MQUtil.sleepConsuming(channel, "queue", "slow");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }

    }
}
