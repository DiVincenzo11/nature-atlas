package com.keshihua.ksh.pojo.dto;

import lombok.Data;

/**
 * 前端通过 STOMP 发送消息时的载荷
 */
@Data
public class SendExpertMessageRequest {
    /** 专家主键 ID（expert_info.expert_id） */
    private Integer expertId;
    /** 消息内容 */
    private String content;
    /** 消息类型：TEXT、IMAGE 等 */
    private String contentType;
}
