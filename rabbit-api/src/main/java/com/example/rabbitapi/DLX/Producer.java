package com.example.rabbitapi.DLX;

import com.example.rabbitapi.utils.Connect2Rabbitmq;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {

    public static void main(String[] args) throws IOException, TimeoutException {
        //1.创建一个connectionFactory
        Connection connection = Connect2Rabbitmq.getConnection();
        Channel channel = connection.createChannel();

        String exchange = "test_dlx_exchange";
        String routingKey = "dlx.save";

        String msg = "hello rabbitmq from dlx message";

        // mandatory设置为true的表示当exchange路由不到相应的队列的时候，broker不会删除该消息。会返回到return listener。设置为false会broker自动删除消息
        for (int i = 0; i < 5; i++) {
            channel.basicPublish(exchange, routingKey, true, null, msg.getBytes());
        }
    }
}
