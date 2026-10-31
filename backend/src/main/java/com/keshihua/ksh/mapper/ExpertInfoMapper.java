package com.keshihua.ksh.mapper;

import com.keshihua.ksh.pojo.ExpertInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ExpertInfoMapper {

    /**
     * 查询所有专家信息，用于前端列表展示
     */
    @Select("select * from expert_info order by expert_id asc")
    List<ExpertInfo> listAll();

    /**
     * 根据主键查单条
     */
    @Select("select * from expert_info where expert_id = #{expertId}")
    ExpertInfo findById(Integer expertId);
}
