package com.keshihua.ksh.strategy;

import com.keshihua.ksh.pojo.Result;
import org.springframework.web.multipart.MultipartFile;

/**
 * 识别策略接口，提供统一的识别入口与类型标识。
 */
public interface DetectionStrategy {

    /**
     * 返回当前策略支持的识别类型，用于工厂匹配。
     */
    DetectionType getType();

    /**
     * 执行识别逻辑。
     *
     * @param file   上传的文件
     * @param userId 用户ID
     * @return 识别结果
     */
    Result detect(MultipartFile file, int userId);
}
