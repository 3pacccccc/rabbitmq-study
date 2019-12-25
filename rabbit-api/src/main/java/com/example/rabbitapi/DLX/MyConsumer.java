package com.example.rabbitapi.DLX;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

public class MyConsumer extends DefaultConsumer {

    public MyConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String s, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException {
        System.out.println("-------------consume message----------------");
        System.out.println("consumerTag:" + s);
        System.out.println("envelope:" + envelope);
        System.out.println("basicProperties:" + basicProperties);
        System.out.println("body:" + new String(bytes));
    }
}
