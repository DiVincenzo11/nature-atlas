package com.keshihua.ksh.comsumer;

import com.keshihua.ksh.config.RabbitMQConfig;
import com.keshihua.ksh.pojo.QiniuUploadMessage;
import com.keshihua.ksh.utils.QiniuCloudUtil;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

@Component
public class QiniuCloudConsumer {
    @Autowired
    private StringRedisTemplate redis;
    private static final Logger log = LoggerFactory.getLogger(QiniuCloudConsumer.class);

    @RabbitListener(
            queues = RabbitMQConfig.QINIU_UPLOAD_QUEUE,
            containerFactory = "manualAckContainerFactory"
    )
    // channel 是底层 RabbitMQ 客户端通道
    public void handleUpload(
            QiniuUploadMessage message,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long tag
    ) throws IOException {
        String userId = String.valueOf(message.getUserId());
        String key = "rate_limit:" + userId;

        // 原子地在 Redis 中设置 key，如果不存在则返回 true；如果已经存在则返回 false
        Boolean allowed = redis.opsForValue()
                .setIfAbsent(key, "detecting", Duration.ofSeconds(3));
        if (Boolean.FALSE.equals(allowed)) {
            // 超过频率，直接 ACK 丢弃，不处理业务
            log.warn("用户 {} 3s 内重复请求，已限流", userId);
            channel.basicAck(tag, false);
            return;
        }

        try {
            // —— 处理业务 ——
            File file = new File(message.getFilePath());
            if (!file.exists()) {
                log.error("文件不存在: {}", message.getFilePath());
                // 如果是无法恢复的错误，可以直接拒绝并丢弃
                channel.basicReject(tag, false);
                return;
            }
            String picUrl = QiniuCloudUtil.upload(file, ".jpg");
            log.info("文件上传成功: {}", picUrl);

            // —— 业务成功后，手动 ACK ——
            // 确认单条或多条消息已成功处理
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("处理失败，准备重入队", e);
            try {
                // 第二个参数 requeue=true 表示把消息重新放回队列
                // 消息处理失败，直接重新入队
                channel.basicNack(tag, false, true);
            } catch (IOException ioEx) {
                log.error("basicNack 也失败了", ioEx);
            }
        }
    }
}
