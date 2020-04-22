package com.example.rabbitapi.returnlistener;

import com.example.rabbitapi.utils.Connect2Rabbitmq;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ReturnListener;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {

    public static void main(String[] args) throws IOException, TimeoutException {
        //1.创建一个connectionFactory
        Connection connection = Connect2Rabbitmq.getConnection();
        Channel channel = connection.createChannel();

        String exchange = "test_return_change";
        String routingKey = "return.save";
        String routingKeyError = "abc.save";

        String msg = "hello rabbitmq from return message";

        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("--------handle return---------------");
                System.out.println("replyCode: " + replyCode);
                System.out.println("replyText: " + replyText);
                System.out.println("exchange: " + exchange);
                System.out.println("routingKey: " + routingKey);
                System.out.println("properties: " + properties);
                System.out.println("body: " + new String(body));
            }

        });
        // mandatory设置为true的表示当exchange路由不到相应的队列的时候，broker不会删除该消息。会返回到return listener。设置为false会broker自动删除消息
        channel.basicPublish(exchange, routingKeyError, true, null, msg.getBytes());
    }

}
