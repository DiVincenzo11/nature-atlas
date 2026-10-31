package com.keshihua.ksh.mapper;

import com.keshihua.ksh.pojo.Plant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PlantMapper {

    @Select("select * from plant2 where name like concat(#{name}, '%')")
    Plant plantdata(String name);
}
