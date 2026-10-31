package com.keshihua.ksh.mapper;

import com.keshihua.ksh.pojo.Turtle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TurtleMapper {
    // 爬行的详情页图鉴数据
    @Select("select size,name,image_url, knowledge, introduction, history, owner, notice, nextname from turtle_tujian where name=#{name};")
    Turtle turtledata(String name);

}
