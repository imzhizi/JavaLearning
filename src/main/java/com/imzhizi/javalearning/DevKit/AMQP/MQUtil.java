package com.imzhizi.javalearning.DevKit.AMQP;

import com.rabbitmq.client.*;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

/**
 * created by zhizi
 * on 5/12/20 21:38
 */
public class MQUtil {
    private static ConnectionFactory connectionFactory;

    public static Connection getNewConnection() {
        if (connectionFactory == null) {
            connectionFactory = new ConnectionFactory();
            connectionFactory.setHost("localhost");
            connectionFactory.setPort(5672);
            connectionFactory.setVirtualHost("/just4fun");
            connectionFactory.setUsername("just4fun");
            connectionFactory.setPassword("just4fun");
        }

        Connection connection = null;
        try {
            connection = connectionFactory.newConnection();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        } finally {
            return connection;
        }
    }

    public static Channel getNewChannel(Connection connection) {
        Channel channel = null;
        try {
            channel = connection.createChannel();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return channel;
        }
    }

    public static void close(Connection connection, Channel channel) {
        try {
            channel.close();
            connection.close();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }


    public static void sleepConsuming(Channel channel, String queueName, String tag) throws IOException {
        channel.basicConsume(queueName, false, new DefaultConsumer(channel) {
            @SneakyThrows
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println(tag + ":{" + new String(body)+"}");
                Thread.sleep(500);
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        });
    }

    public static void consuming(Channel channel, String queueName, String tag) throws IOException {
        channel.basicConsume(queueName, false, new DefaultConsumer(channel) {
            @SneakyThrows
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println(tag + ":{" + new String(body)+"}");
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        });
    }
}
