package com.keshihua.ksh.controller;

import com.keshihua.ksh.mapper.ExpertInfoMapper;
import com.keshihua.ksh.pojo.ExpertInfo;
import com.keshihua.ksh.pojo.ExpertMessage;
import com.keshihua.ksh.pojo.Result;
import com.keshihua.ksh.pojo.dto.ExpertChatPayload;
import com.keshihua.ksh.pojo.dto.SendExpertMessageRequest;
import com.keshihua.ksh.service.ExpertChatService;
import com.keshihua.ksh.utils.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;

import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

import java.util.Objects;

/**
 * 专家 1v1 聊天控制器，含 STOMP 和 REST 能力
 */
@RestController
@RequestMapping("/expert")
public class ExpertChatController {

    @Autowired
    private ExpertChatService expertChatService;
    @Autowired
    private ExpertInfoMapper expertInfoMapper;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    /**
     * 专家列表，用于前端展示/选择
     */
    @GetMapping("/list")
    public Result<List<ExpertInfo>> listExperts() {
        List<ExpertInfo> experts = expertChatService.listExperts();
        return Result.<List<ExpertInfo>>success(experts);

    }


    /**
     * 拉取与某专家的历史消息
     */
    @SuppressWarnings("unchecked")
    @GetMapping("/messages")
    public Result<List<ExpertMessage>> listMessages(@RequestParam Integer expertId,

                               @RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "20") Integer size,
                               HttpServletRequest request) {

        Integer userId = resolveUserIdFromHttp(request);
        if (userId == null) {
            return Result.error("未登录或 token 无效");
        }
        ExpertInfo expert = expertInfoMapper.findById(expertId);
        if (expert == null) {
            return Result.error("专家不存在");
        }
        List<ExpertMessage> messages = expertChatService.listMessages(userId, expert.getUserId(), page, size);
        return Result.<List<ExpertMessage>>success(messages);

    }

    /**
     * STOMP 入口：发送单条消息给专家
     */
    @MessageMapping("/expert/send")
    public void sendToExpert(@Payload SendExpertMessageRequest payload, Principal principal) {
        if (principal == null) {
            return;
        }
        Integer senderUserId = expertChatService.findUserIdByJti(principal.getName());
        if (senderUserId == null) {
            return;
        }
        if (payload == null || payload.getExpertId() == null || !StringUtils.hasText(payload.getContent())) {
            return;
        }
        ExpertInfo expert = expertInfoMapper.findById(payload.getExpertId());
        if (expert == null || expert.getUserId() == null) {
            return;
        }

        String senderRole = Objects.equals(senderUserId, expert.getUserId()) ? "EXPERT" : "USER";
        String contentType = StringUtils.hasText(payload.getContentType()) ? payload.getContentType() : "TEXT";

        // 持久化
        ExpertMessage saved = expertChatService.persistMessage(senderUserId, expert.getUserId(), senderRole, contentType, payload.getContent());

        // 构建推送载荷
        ExpertChatPayload outbound = new ExpertChatPayload();
        BeanUtils.copyProperties(saved, outbound);
        outbound.setExpertId(payload.getExpertId());
        outbound.setUserId(senderUserId);

        // 推给自己
        messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/expert/chat", outbound);

        // 推给专家（若在线）
        String expertJti = expertChatService.findJtiByUserId(expert.getUserId());
        if (expertJti != null && !expertJti.equals(principal.getName())) {
            messagingTemplate.convertAndSendToUser(expertJti, "/queue/expert/chat", outbound);
        }
    }

    /**
     * HTTP 请求中解析 userId（Authorization: Bearer xxx）
     */
    private Integer resolveUserIdFromHttp(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        String token = header.substring(7);
        try {
            return Integer.parseInt(JwtUtils.parseToken(token).get("userId").toString());
        } catch (Exception e) {
            return null;
        }
    }
}
