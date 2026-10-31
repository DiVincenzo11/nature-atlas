package com.keshihua.ksh.mapper;

import com.keshihua.ksh.pojo.Liangqi;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LiangqiMapper {

    // 两栖的详情页图鉴数据
    @Select("select ke,shu,shu_eng,zhong,zhong_eng,name,characterdata,natural_history_info,picurl from liangqi_tujian where zhong=#{name};")
    Liangqi liangqidata(String name);
}
