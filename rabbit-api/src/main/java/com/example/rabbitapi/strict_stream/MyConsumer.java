package com.example.rabbitapi.strict_stream;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

public class MyConsumer extends DefaultConsumer {

    private Channel channel;

    public MyConsumer(Channel channel) {
        super(channel);
        this.channel = channel;
    }

    @Override
    public void handleDelivery(String s, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException {
        System.out.println("-------------consume message----------------");
        System.out.println("consumerTag:" + s);
        System.out.println("envelope:" + envelope);
        System.out.println("basicProperties:" + basicProperties);
        System.out.println("body:" + new String(bytes));

        channel.basicAck(envelope.getDeliveryTag(), false); // false表示关闭批量签收
    }
}
