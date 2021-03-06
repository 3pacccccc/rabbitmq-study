package com.example.spring;

import com.example.spring.entity.Order;
import com.example.spring.entity.Packaged;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

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

    @Test
    public void testSendMessage4Text() {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("text/plain");
        Message message = new Message("mq 消息1234".getBytes(), messageProperties);

        rabbitTemplate.send("topic001", "spring.abc", message);
        rabbitTemplate.send("topic002", "rabbit.abc", message);
    }

    @Test
    public void testSendJsonMessage() throws Exception {
        Order order = new Order();
        order.setId("001");
        order.setName("消息订单");
        order.setContent("描述信息");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(order);
        System.err.println("order 4 json: " + json);

        MessageProperties messageProperties = new MessageProperties();
        // 这里注意一定要修改contentType为application/json
        messageProperties.setContentType("application/json");
        Message message = new Message(json.getBytes(), messageProperties);
        rabbitTemplate.send("topic001", "spring.order", message);
    }

    @Test
    public void testSendJavaMessage() throws Exception {

        Order order = new Order();
        order.setId("001");
        order.setName("订单消息");
        order.setContent("订单描述信息");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(order);
        System.err.println("order 4 json: " + json);

        MessageProperties messageProperties = new MessageProperties();
        //这里注意一定要修改contentType为 application/json
        messageProperties.setContentType("application/json");
        messageProperties.getHeaders().put("__TypeId__", "com.example.spring.entity.Order"); // 可以直接转化为order对象
        Message message = new Message(json.getBytes(), messageProperties);

        rabbitTemplate.send("topic001", "spring.order", message);
    }

	@Test
	public void testSendMappingMessage() throws Exception {

		ObjectMapper mapper = new ObjectMapper();

		Order order = new Order();
		order.setId("001");
		order.setName("订单消息");
		order.setContent("订单描述信息");

		String json1 = mapper.writeValueAsString(order);
		System.err.println("order 4 json: " + json1);

		MessageProperties messageProperties1 = new MessageProperties();
		//这里注意一定要修改contentType为 application/json
		messageProperties1.setContentType("application/json");
		messageProperties1.getHeaders().put("__TypeId__", "order");
		Message message1 = new Message(json1.getBytes(), messageProperties1);
		rabbitTemplate.send("topic001", "spring.order", message1);

		Packaged pack = new Packaged();
		pack.setId("002");
		pack.setName("包裹消息");
		pack.setDescription("包裹描述信息");

		String json2 = mapper.writeValueAsString(pack);
		System.err.println("pack 4 json: " + json2);

		MessageProperties messageProperties2 = new MessageProperties();
		//这里注意一定要修改contentType为 application/json
		messageProperties2.setContentType("application/json");
		messageProperties2.getHeaders().put("__TypeId__", "packaged");
		Message message2 = new Message(json2.getBytes(), messageProperties2);
		rabbitTemplate.send("topic001", "spring.pack", message2);
	}

	@Test
	public void testSendExtConverterMessage() throws Exception {
        byte[] body = Files.readAllBytes(Paths.get("d:/002_books", "picture.png"));
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("image/png");
        messageProperties.getHeaders().put("extName", "png");
        Message message = new Message(body, messageProperties);
        rabbitTemplate.send("", "image_queue", message);

//			byte[] body = Files.readAllBytes(Paths.get("d:/002_books", "mysql.pdf"));
//			MessageProperties messageProperties = new MessageProperties();
//			messageProperties.setContentType("application/pdf");
//			Message message = new Message(body, messageProperties);
//			rabbitTemplate.send("", "pdf_queue", message);
	}
}
