package com.example.rabbitapi.DLX;

import com.example.rabbitapi.utils.Connect2Rabbitmq;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class Consumer {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        //1.创建一个connectionFactory
        Connection connection = Connect2Rabbitmq.getConnection();
        Channel channel = connection.createChannel();

        String exchangeName = "test_dlx_change";
        String routingKey = "dlx.#";
        String queueName = "test_dlx_queue";

        channel.exchangeDeclare(exchangeName, "topic", true, false, null); // 普通的声明exchange交换机
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "dlx.exchange");

        channel.queueDeclare(queueName, true, false, false, arguments);
        channel.queueBind(queueName, exchangeName, routingKey);

        // 声明死信队列
        channel.exchangeDeclare("dlx.exchange", "topic", true, false, null);
        channel.queueDeclare("dlx.queue", true, false,false, null);
        channel.queueBind("dlx.queue", "dlx.exchange", "#");

        channel.basicConsume(queueName, true, new MyConsumer(channel));


    }
}
