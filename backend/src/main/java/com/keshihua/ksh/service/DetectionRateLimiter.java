package com.keshihua.ksh.service;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * 识别接口限流服务：
 * 1) 基于 Bucket4j + Redis 的全局/按路径令牌桶（容量10，速率5 token/s）。
 * 2) 基于 Redis setIfAbsent 的用户级 3 秒冷却。
 */
@Service
public class DetectionRateLimiter {

    private static final String DETECT_BUCKET_KEY_PREFIX = "detect:bucket:";
    private static final String DETECT_USER_COOLDOWN_PREFIX = "detect:user:cooldown:";

    private final ProxyManager<byte[]> proxyManager;
    private final Supplier<BucketConfiguration> detectBucketConfig;
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public DetectionRateLimiter(ProxyManager<byte[]> proxyManager,
                                Supplier<BucketConfiguration> detectBucketConfig,
                                StringRedisTemplate redisTemplate) {
        this.proxyManager = proxyManager;
        this.detectBucketConfig = detectBucketConfig;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 尝试为指定路径消耗 1 个令牌。
     */
    public ConsumptionProbe tryConsume(String typeCode) {
        String key = DETECT_BUCKET_KEY_PREFIX + typeCode;
        Bucket bucket = proxyManager.builder().build(key.getBytes(), detectBucketConfig);
        return bucket.tryConsumeAndReturnRemaining(1);
    }

    /**
     * 检查用户 3 秒内是否重复提交。
     */
    public boolean allowUserOnceIn3Seconds(int userId) {
        String key = DETECT_USER_COOLDOWN_PREFIX + userId;
        Boolean allowed = redisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofSeconds(3));
        return Boolean.TRUE.equals(allowed);
    }
}
