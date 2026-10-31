package com.keshihua.ksh.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 专家与用户 1v1 会话的消息体
 */
@Data
public class ExpertMessage {
    /** 自增主键 */
    private Long id;
    /** 会话唯一标识，按 userId-expertId 拼接 */
    private String sessionId;
    /** 用户侧 ID（游客/普通用户） */
    private Integer userId;
    /** 专家关联的用户 ID（用于点对点推送） */
    private Integer expertUserId;

    /** 发送方角色：USER 或 EXPERT */
    private String senderRole;
    /** 内容类型：TEXT、IMAGE 等 */
    private String contentType;
    /** 文本内容或富文本占位 */
    private String content;
    /** 发送时间 */
    private LocalDateTime createdAt;
}
