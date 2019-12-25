package com.example.rabbitapi.ack;

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
        System.out.println("body:" + new String(bytes));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if ((Integer) basicProperties.getHeaders().get("num") == 0){
            channel.basicNack(envelope.getDeliveryTag(), false, true);
        } else{
            channel.basicAck(envelope.getDeliveryTag(), false);
        }
    }
}
