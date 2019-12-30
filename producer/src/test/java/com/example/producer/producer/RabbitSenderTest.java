package com.example.producer.producer;


import com.example.producer.entity.Order;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitSenderTest {

    @Autowired
    private RabbitSender rabbitSender;

    @Autowired
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:ss.SSS"); //SimpleDateFormat有线程安全问题

    @Test
    public void testSender1() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("number", "12345");
        properties.put("send_time", simpleDateFormat.format(new Date()));
        for (int i = 0; i < 3; i++) {
            rabbitSender.send("hello rabbitmq for spring boot!", properties);
        }
    }

    @Test
    public void testSender2() throws Exception{
        Order order = new Order("001", "第一个订单");
        rabbitSender.sendOrder(order);
    }

}
