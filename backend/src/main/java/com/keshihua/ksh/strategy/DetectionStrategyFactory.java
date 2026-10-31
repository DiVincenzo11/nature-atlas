package com.keshihua.ksh.strategy;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 策略工厂：在构造函数中收集所有实现了 {@link DetectionStrategy} 的 Bean，
 * 按照类型建立映射，供控制器按需获取。
 */
@Component
public class DetectionStrategyFactory {

    private final Map<DetectionType, DetectionStrategy> strategies = new HashMap<>();

    public DetectionStrategyFactory(List<DetectionStrategy> strategies) {
        for (DetectionStrategy strategy : strategies) {
            this.strategies.put(strategy.getType(), strategy);
        }
    }

    /**
     * 根据路径片段获取具体策略。
     *
     * @param typeCode 前端传入的路径片段，例如 birdpic、plantpic
     * @return 匹配的识别策略
     */
    public DetectionStrategy getStrategy(String typeCode) {
        DetectionType type = DetectionType.fromCode(typeCode);
        DetectionStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("未找到识别策略：" + typeCode);
        }
        return strategy;
    }
}
