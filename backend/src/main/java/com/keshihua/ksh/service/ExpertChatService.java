package com.keshihua.ksh.service;

import com.keshihua.ksh.pojo.ExpertInfo;
import com.keshihua.ksh.pojo.ExpertMessage;

import java.util.List;

public interface ExpertChatService {
    List<ExpertInfo> listExperts();

    List<ExpertMessage> listMessages(Integer userId, Integer expertUserId, int page, int size);

    ExpertMessage persistMessage(Integer userId, Integer expertUserId, String senderRole, String contentType, String content);

    String buildSessionId(Integer userId, Integer expertUserId);


    Integer findUserIdByJti(String jti);

    String findJtiByUserId(Integer userId);
}
