package com.keshihua.ksh.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory.ConfirmType;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class RabbitMQConfig {
    public static final String QINIU_EXCHANGE = "qiniu.upload.exchange"; // 定义交换机
    public static final String QINIU_UPLOAD_QUEUE = "qiniu_upload_queue"; // 定义队列
    public static final String ROUTING_KEY = "qiniu.upload"; // 新增路由键

    public static final String DLX_EXCHANGE = "qiniu.dlx.exchange"; // 死信交换机
    public static final String DLX_QUEUE = "qiniu_dead_letter_queue"; // 死信队列
    public static final String DLX_ROUTING_KEY = "qiniu.upload.dead"; // 死信路由键


    @Bean
    public CachingConnectionFactory rabbitConnectionFactory(
            @Value("${spring.rabbitmq.host}") String host,
            @Value("${spring.rabbitmq.port}") int port,
            @Value("${spring.rabbitmq.username}") String username,
            @Value("${spring.rabbitmq.password}") String password) {

        CachingConnectionFactory cf = new CachingConnectionFactory(host, port);
        cf.setUsername(username);
        cf.setPassword(password);

        // 户端开启“关联式确认”模式，发送时会给每条消息绑定一个 CorrelationData，Broker 在消息持久化后回 ACK，会带回同样的 CorrelationData
        cf.setPublisherConfirmType(ConfirmType.CORRELATED);

        return cf;
    }

    // 设置为 MANUAL Ack （手动返回 ack ）
    @Bean
    public SimpleRabbitListenerContainerFactory manualAckContainerFactory(
            CachingConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        // 1. 设置手动 ACK
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setAutoStartup("true".equalsIgnoreCase(System.getenv("CLOUD_UPLOAD_ENABLED")));
        // 可选：并发度配置
        factory.setConcurrentConsumers(5);
        factory.setMaxConcurrentConsumers(10);
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory cf,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(cf);
        template.setMessageConverter(converter);
        // 当开启 publisherReturns=true 时，如果消息路由失败会进入 returnCallback
        template.setMandatory(true);
        return template;
    }

    // 声明一个持久化的 DirectExchange
    @Bean
    public DirectExchange qiniuExchange() {
        // 第二个参数 durable=true，表示重启后 Exchange 仍然存在
        return new DirectExchange(QINIU_EXCHANGE, true, false);
    }

    // 声明一个持久化的 Queue，并绑定死信交换机
    @Bean
    public Queue qiniuUploadQueue() {
        Map<String, Object> args = new HashMap<>(2);
        args.put("x-dead-letter-exchange", DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", DLX_ROUTING_KEY);
        // 第二个参数 durable=true，表示重启后队列仍然存在
        return new Queue(QINIU_UPLOAD_QUEUE, true, false, false, args);
    }


    // 绑定时不需要额外配置，Exchange 与 Queue 均已 durable
    @Bean
    public Binding binding(Queue qiniuUploadQueue, DirectExchange qiniuExchange) {
        return BindingBuilder
                .bind(qiniuUploadQueue)
                .to(qiniuExchange)
                .with(ROUTING_KEY);
    }

    // 死信交换机、队列与绑定
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue(DLX_QUEUE, true);
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder
                .bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(DLX_ROUTING_KEY);
    }

    // JSON 序列化，避免将数据转入 rabbitmq 后， rabbitmq 无法正确接收数据
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}



/*


sendUploadMessage()
    ├─ generate ID & timestamp
    ├─ put in pendingConfirms
    └─ rabbitTemplate.convertAndSend(...,CorrelationData)

Broker 收到消息 → 持久化 → ACK → confirmCallback()

confirmCallback(ack=true) → pendingConfirms.remove(id)
或
confirmCallback(ack=false) → pendingConfirms.get(id) → 重试发送


*/
