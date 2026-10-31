package com.keshihua.ksh.producer;

import com.keshihua.ksh.config.RabbitMQConfig;
import com.keshihua.ksh.pojo.QiniuUploadMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;



@Component
public class QiniuCloudProducer {

    private static final Logger log = LoggerFactory.getLogger(QiniuCloudProducer.class);
    /** 最大重试次数（包含初次 NACK 之后的重试次数） */
    private static final int MAX_RETRY = 3;

    private final RabbitTemplate rabbitTemplate;

    // 用于缓存未收到 ACK 的消息
    private final ConcurrentMap<String, QiniuUploadMessage> pendingConfirms = new ConcurrentHashMap<>();
    // 记录每条消息的重试次数
    private final ConcurrentMap<String, Integer> retryCounters = new ConcurrentHashMap<>();

    @Autowired
    public QiniuCloudProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;


        // 接收来自 Broker 的 ACK（ack=true）或 NACK（ack=false）（只有当 RabbitMQ 把日志安全地落盘之后，生产者才能拿到 ack=true）
        this.rabbitTemplate.setConfirmCallback(this::confirmCallback);
    }

    public void sendUploadMessage(QiniuUploadMessage message) {
        // 1. 生成唯一 ID 作为 CorrelationData
        String messageId = UUID.randomUUID().toString();

        // 2. 把消息放到本地缓存，等 ACK 回来再清除或重试
        pendingConfirms.put(messageId, message);
        retryCounters.put(messageId, 0);

        // 3. 发送消息，设置 PERSISTENT
        sendToBroker(messageId, message);
    }

    /**
     * 实际发送到 Broker，复用以便重试时不生成新的 messageId
     */
    private void sendToBroker(String messageId, QiniuUploadMessage message) {
        CorrelationData correlationData = new CorrelationData(messageId);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.QINIU_EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                message,
                msg -> {
                    // 这里实现了持久化存储，会在后台把这条消息写到磁盘
                    msg.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    msg.getMessageProperties().setHeader("x-message-id", messageId);
                    msg.getMessageProperties().setHeader("x-retry-count", retryCounters.getOrDefault(messageId, 0));
                    return msg;
                },
                correlationData
        );
    }


    /**
     * ConfirmCallback 方法签名：
     *  correlationData: 关联数据，包含我们刚才设置的唯一 ID
     *  ack:            Broker 是否成功写入并持久化消息
     *  cause:          如果 nack，简单描述原因
     */
    private void confirmCallback(CorrelationData correlationData, boolean ack, String cause) {
        String messageId = correlationData != null ? correlationData.getId() : null;
        if (messageId == null) {
            log.warn("收到 confirm 回调但 messageId 为空，ack={}，cause={}", ack, cause);
            return;
        }

        if (ack) {
            // Broker 已经持久化该消息，删除本地缓存
            pendingConfirms.remove(messageId);
            retryCounters.remove(messageId);
            return;
        }

        // Broker 未确认，记录告警并重试/入死信
        QiniuUploadMessage failedMsg = pendingConfirms.get(messageId);
        if (failedMsg == null) {
            log.warn("NACK 收到但未找到缓存的消息，id={}，cause={}", messageId, cause);
            return;
        }

        int nextRetry = retryCounters.merge(messageId, 1, Integer::sum);
        if (nextRetry > MAX_RETRY) {
            log.error("消息 id={} 重试超过 {} 次，转入死信队列，原因：{}", messageId, MAX_RETRY, cause);
            pendingConfirms.remove(messageId);
            retryCounters.remove(messageId);
            sendToDeadLetter(messageId, failedMsg, cause);
            return;
        }

        log.warn("消息 id={} 收到 NACK，第 {} 次重试，原因：{}", messageId, nextRetry, cause);
        sendToBroker(messageId, failedMsg);
    }

    private void sendToDeadLetter(String messageId, QiniuUploadMessage message, String cause) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.DLX_EXCHANGE,
                RabbitMQConfig.DLX_ROUTING_KEY,
                message,
                msg -> {
                    msg.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    msg.getMessageProperties().setHeader("x-message-id", messageId);
                    msg.getMessageProperties().setHeader("x-dead-letter-reason", cause);
                    msg.getMessageProperties().setHeader("x-retry-count", MAX_RETRY);
                    return msg;
                }
        );
    }

//    /**
//     * 定时任务：扫描 pendingConfirms 中超过阈值的消息，进行重试或告警
//     */
//    @Scheduled(fixedDelay = 30000)

//    public void retryStaleMessages() {
//        long now = System.currentTimeMillis();
//        for (Map.Entry<String, QiniuUploadMessage> entry : pendingConfirms.entrySet()) {
//            String id = entry.getKey();
//            QiniuUploadMessage msg = entry.getValue();
//            // 这里假设 CorrelationData 的 timestamp 字段里记录了最初发送时间
//            CorrelationData cd = /* 取出你存的 CorrelationData 对象 */;
//            if (cd != null && now - /* cd timestamp */ > 60000) {
//                System.out.println("Retrying stale message id=" + id);
//                sendUploadMessage(msg);
//            }
//        }
//    }
}
