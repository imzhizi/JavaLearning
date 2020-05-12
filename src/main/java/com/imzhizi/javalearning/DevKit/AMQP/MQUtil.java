package com.imzhizi.javalearning.DevKit.AMQP;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * created by zhizi
 * on 5/12/20 21:38
 */
public class MQUtil {
    private static ConnectionFactory connectionFactory;

    public static Connection getNewConnection() throws IOException, TimeoutException {
        if (connectionFactory == null) {
            connectionFactory = new ConnectionFactory();
            connectionFactory.setHost("localhost");
            connectionFactory.setPort(5672);
            connectionFactory.setVirtualHost("/just4fun");
            connectionFactory.setUsername("just4fun");
            connectionFactory.setPassword("just4fun");
        }

        return connectionFactory.newConnection();
    }
}
