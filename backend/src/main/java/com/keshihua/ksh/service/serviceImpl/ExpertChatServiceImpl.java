package com.keshihua.ksh.service.serviceImpl;

import com.keshihua.ksh.mapper.ExpertInfoMapper;
import com.keshihua.ksh.mapper.ExpertMessageMapper;
import com.keshihua.ksh.pojo.ExpertInfo;
import com.keshihua.ksh.pojo.ExpertMessage;
import com.keshihua.ksh.service.ExpertChatService;
import com.keshihua.ksh.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import java.util.Objects;

@Service
public class ExpertChatServiceImpl implements ExpertChatService {

    @Autowired
    private ExpertInfoMapper expertInfoMapper;
    @Autowired
    private ExpertMessageMapper expertMessageMapper;
    @Autowired
    private StringRedisTemplate redis;

    @Override
    public List<ExpertInfo> listExperts() {
        return expertInfoMapper.listAll();
    }

    @Override
    public List<ExpertMessage> listMessages(Integer userId, Integer expertUserId, int page, int size) {
        String sessionId = buildSessionId(userId, expertUserId);

        int offset = (Math.max(page, 1) - 1) * size;
        return expertMessageMapper.pageBySession(sessionId, offset, size);
    }

    @Override
    public ExpertMessage persistMessage(Integer userId, Integer expertUserId, String senderRole, String contentType, String content) {
        ExpertMessage message = new ExpertMessage();
        message.setSessionId(buildSessionId(userId, expertUserId));
        message.setUserId(userId);
        message.setExpertUserId(expertUserId);

        message.setSenderRole(senderRole);
        message.setContentType(contentType);
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        expertMessageMapper.save(message);
        return message;
    }

    @Override
    public String buildSessionId(Integer userId, Integer expertUserId) {
        // 统一生成规则，保证双方顺序一致
        if (userId == null || expertUserId == null) {
            throw new IllegalArgumentException("userId 或 expertId 不能为空");
        }
        // 为避免顺序导致的重复，会话 ID 总是按大小排序
        int min = Math.min(userId, expertUserId);
        int max = Math.max(userId, expertUserId);

        return min + "-" + max;
    }

    @Override
    public Integer findUserIdByJti(String jti) {
        if (jti == null) {
            return null;
        }
        String key = "jti:user:" + jti;
        String val = redis.opsForValue().get(key);
        if (val == null) {
            return null;
        }
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String findJtiByUserId(Integer userId) {
        if (userId == null) {
            return null;
        }
        String token = redis.opsForValue().get("user:token:" + userId);
        if (token == null) {
            return null;
        }
        return Objects.toString(JwtUtils.parseToken(token).get("jti"), null);
    }
}
