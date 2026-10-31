package com.keshihua.ksh.pojo;

import lombok.Data;

/**
 * 专家基本信息，用于展示和会话匹配
 */
@Data
public class ExpertInfo {
    /** 主键 ID */
    private Integer expertId;
    /** 关联的系统用户 ID（用于登录/鉴权） */
    private Integer userId;
    /** 姓名/昵称 */
    private String name;
    /** 头像地址 */
    private String avatar;
    /** 擅长领域，例如“鸟类识别” */
    private String specialty;
    /** 头衔，例如“高级研究员” */
    private String title;
    /** 简介 */
    private String description;
    /** 联系方式（可选） */
    private String contact;
    /** 在线状态标记，由会话事件维护 */
    private Boolean online;
}
