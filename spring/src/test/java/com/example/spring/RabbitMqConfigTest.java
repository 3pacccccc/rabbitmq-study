package com.example.spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitMqConfigTest {

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void connectionFactory() {
    }

    @Test
    public void rabbitAdmin() {
        rabbitAdmin.declareExchange(new DirectExchange("test.direct", false, false));
        rabbitAdmin.declareExchange(new TopicExchange("test.topic", false, false));
        rabbitAdmin.declareExchange(new FanoutExchange("test.fanout", false, false));

        rabbitAdmin.declareQueue(new Queue("test.direct.queue", false));
        rabbitAdmin.declareQueue(new Queue("test.topic.queue", false));
        rabbitAdmin.declareQueue(new Queue("test.fanout.queue", false));

        rabbitAdmin.declareBinding(new Binding("test.direct.queue", Binding.DestinationType.QUEUE, "test.direct",
                "direct", new HashMap<>()));

        /*
        在声明绑定关系的时候现创建队列
         */
        rabbitAdmin.declareBinding(
                BindingBuilder.bind(new Queue("test.topic.queue", false))
                        .to(new TopicExchange("test.topic", false, false))
                        .with("user.#")
        );

        rabbitAdmin.declareBinding(
                BindingBuilder.bind(new Queue("test.fanout.queue", false))
                        .to(new FanoutExchange("test.fanout", false, false)));

        rabbitAdmin.purgeQueue("test.topic.queue", false); //清空指定队列的消息

    }

    @Test
    public void testSendMessage() {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.getHeaders().put("desc", "信息描述...");
        messageProperties.getHeaders().put("type", "自定义消息类型...");

        Message message = new Message("hello rabbitmq".getBytes(), messageProperties);

        rabbitTemplate.convertAndSend("topic001", "spring.amqp", message, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                System.out.println("-------添加额外的设置----------");
                message.getMessageProperties().getHeaders().put("desc", "额外修改的信息描述");
                message.getMessageProperties().getHeaders().put("attr", "额外新加的属性");
                return message;
            }
        });
    }


    @Test
    public void testSendMessage2() {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("text/plain");
        Message message = new Message("mq 消息1234".getBytes(), messageProperties);

//        Map<String, Object> rabbitmq_content = new HashMap<>();
//        rabbitmq_content.put("ent_id", "456781");
//        rabbitmq_content.put("ent_name", "小米科技有限公司");
//        rabbitmq_content.put("user_id", "111");

//        rabbitTemplate.convertAndSend("topic001", "spring.abc", rabbitmq_content);
        rabbitTemplate.send("topic001", "spring.abc", message);
        rabbitTemplate.convertAndSend("topic001", "spring.amqp", "hello object message send");
        rabbitTemplate.convertAndSend("topic002", "rabbit.abc", "hello object message send!");
    }
}