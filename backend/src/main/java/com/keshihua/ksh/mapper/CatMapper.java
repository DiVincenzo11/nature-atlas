package com.keshihua.ksh.mapper;

import com.keshihua.ksh.pojo.Cat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CatMapper {
    // 猫的详情页图鉴数据
    @Select("select * from cat_tujian where name=#{name};")
    Cat catdata(String name);
}
