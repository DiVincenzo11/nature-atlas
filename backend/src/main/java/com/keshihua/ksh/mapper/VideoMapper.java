package com.keshihua.ksh.mapper;

import com.keshihua.ksh.pojo.video;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VideoMapper {

    @Select("select * from video;")
    List<video> getvideo();
}
