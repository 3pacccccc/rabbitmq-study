package com.example.rabbitapi.strict_stream;

import com.example.rabbitapi.utils.Connect2Rabbitmq;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        //1.创建一个connectionFactory
        Connection connection = Connect2Rabbitmq.getConnection();
        Channel channel = connection.createChannel();

        String exchangeName = "test_qos_change";
        String routingKey = "qos.#";
        String queueName = "test_qos_queue";

        channel.exchangeDeclare(exchangeName, "topic", true, false, null);
        channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName, exchangeName, routingKey);

        // 设置限流方式
        channel.basicQos(0, 1, false);

        channel.basicConsume(queueName, false, new MyConsumer(channel));


    }
}
