package com.example.rabbitapi.confirm;

import com.example.rabbitapi.utils.Connect2Rabbitmq;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {
    public static void main(String[] args) throws IOException, TimeoutException {
        //1.创建一个connectionFactory
        Connection connection = Connect2Rabbitmq.getConnection();
        Channel channel = connection.createChannel();

        //2.指定消息投递模式：消息的确认模式
        channel.confirmSelect();

        String exchangeName = "test_confirm_exchange";
        String routingKey = "confirm.save";

        // 3. 发送一条消息
        String msg = "hello rabbitmq send confirm message";
        channel.basicPublish(exchangeName, routingKey, null, msg.getBytes());

        //4. 创建一个消息监听
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long l, boolean b) throws IOException {
                System.out.println("---------no com.example.rabbitapi.ack!-------");
            }

            @Override
            public void handleNack(long l, boolean b) throws IOException {
                System.out.println("---------com.example.rabbitapi.ack!-------");
            }
        });
    }
}
