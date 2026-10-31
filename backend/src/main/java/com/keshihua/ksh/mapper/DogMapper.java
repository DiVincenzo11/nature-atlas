package com.keshihua.ksh.mapper;

import com.keshihua.ksh.pojo.Dog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DogMapper {

    // 狗的详情页图鉴数据
    @Select("select * from dog_tujian where name=#{name};")
    Dog dogdata(String name);
}
