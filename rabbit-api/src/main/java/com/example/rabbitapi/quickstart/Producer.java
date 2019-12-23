package com.example.rabbitapi.quickstart;

import com.example.rabbitapi.utils.Connect2Rabbitmq;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {

    public static void main(String[] args) throws IOException, TimeoutException {
        //1.创建一个connectionFactory
        Connection connection = Connect2Rabbitmq.getConnection();
        Channel channel = connection.createChannel();

        // 4.通过channel发送数据
        for (int i = 0; i < 5; i++) {
            String msg = "hello rabbitmq";
            channel.basicPublish("", "test001", null, msg.getBytes());
        }

        //5.关闭相关连接
        channel.close();
        connection.close();
    }
}
