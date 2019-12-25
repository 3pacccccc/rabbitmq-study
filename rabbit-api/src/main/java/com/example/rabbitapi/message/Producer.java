package com.example.rabbitapi.message;

import com.example.rabbitapi.utils.Connect2Rabbitmq;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class Producer {

    public static void main(String[] args) throws IOException, TimeoutException {
        //1.创建一个connectionFactory
        Connection connection = Connect2Rabbitmq.getConnection();
        Channel channel = connection.createChannel();

        Map<String, Object> headers = new HashMap<>();
        headers.put("my1", "111");
        headers.put("my2", "222");

        // 设置
        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                .deliveryMode(2)// deliveryMode(2)表示持久化投递，即消费重启还会投递。deliveryMode(2)如果消费重启就会消失
                .contentEncoding("UTF-8")
                .headers(headers) // 自定义属性
                .expiration("10000")  //10s内被消费完，超出时间会被清除
                .build();

        // 5.发送
        for (int i = 0; i < 5; i++) {
            String msg = "test";
            channel.basicPublish("", "test001", properties, msg.getBytes());
        }
        channel.close();
        connection.close();


    }

}

