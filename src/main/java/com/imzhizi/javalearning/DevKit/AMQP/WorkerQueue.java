package com.imzhizi.javalearning.DevKit.AMQP;

import com.rabbitmq.client.*;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * created by zhizi
 * on 5/12/20 17:37
 */
public class WorkerQueue {
    static class Producer {
        public static void main(String[] args) throws IOException, TimeoutException {
            Connection connection = MQUtil.getNewConnection();
            Channel channel = connection.createChannel();
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
            channel.close();
            connection.close();
        }
    }

    static class Consumer {
        public static void main(String[] args) throws IOException, TimeoutException {
            Connection connection = MQUtil.getNewConnection();
            new Thread(() -> {
                try {
                    Channel channel = connection.createChannel();
                    channel.basicQos(1);
                    channel.queueDeclare("queue", false, false, true, null);
                    quickConsuming(channel);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                try {
                    Channel channel = connection.createChannel();
                    channel.basicQos(1);
                    channel.queueDeclare("queue", false, false, true, null);
                    slowConsuming(channel);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        static void quickConsuming(Channel channel) throws IOException {
            channel.basicConsume("queue", false, new DefaultConsumer(channel) {
                @SneakyThrows
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String s = new String(body);
                    System.out.println("Quick " + s);
                    Thread.sleep(500);
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            });
        }

        static void slowConsuming(Channel channel) throws IOException {
            channel.basicConsume("queue", false, new DefaultConsumer(channel) {
                @SneakyThrows
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String s = new String(body);
                    System.out.println("Slow " + s);
                    Thread.sleep(1000);
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            });
        }
    }
}
