package com.keshihua.ksh.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * WebSocket 传输用的专家聊天消息 DTO
 */
@Data
public class ExpertChatPayload {
    private Integer expertId;
    private Integer userId;
    private String sessionId;
    private String senderRole;
    private String contentType;
    private String content;
    private LocalDateTime createdAt;
}
