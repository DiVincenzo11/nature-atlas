package com.keshihua.ksh.strategy;

/**
 * 检测类型枚举，统一维护不同识别路径与类型的映射。
 */
public enum DetectionType {
    BIRD("birdpic"),
    PLANT("plantpic");

    private final String code;

    DetectionType(String code) {
        this.code = code;
    }

    /**
     * 获取当前枚举对应的路径标识。
     */
    public String getCode() {
        return code;
    }

    /**
     * 根据请求路径片段解析出识别类型。
     */
    public static DetectionType fromCode(String code) {
        for (DetectionType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("不支持的识别类型：" + code);
    }
}
