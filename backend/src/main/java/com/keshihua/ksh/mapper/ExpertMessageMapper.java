package com.keshihua.ksh.mapper;

import com.keshihua.ksh.pojo.ExpertMessage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface ExpertMessageMapper {

    /**
     * 保存一条聊天消息
     */
    @Insert("insert into expert_message (session_id, user_id, expert_user_id, sender_role, content_type, content, created_at) " +
            "values (#{sessionId}, #{userId}, #{expertUserId}, #{senderRole}, #{contentType}, #{content}, #{createdAt})")

    @Options(useGeneratedKeys = true, keyProperty = "id")
    void save(ExpertMessage message);

    /**
     * 分页查询某个会话的历史消息，按时间倒序
     */
    @Select("select * from expert_message where session_id = #{sessionId} order by created_at desc limit #{limit} offset #{offset}")
    List<ExpertMessage> pageBySession(@Param("sessionId") String sessionId,
                                      @Param("offset") int offset,
                                      @Param("limit") int limit);

}
