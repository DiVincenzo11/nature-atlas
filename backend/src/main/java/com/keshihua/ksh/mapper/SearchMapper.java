package com.keshihua.ksh.mapper;

import com.keshihua.ksh.pojo.Search;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SearchMapper {

    @Select("select name, picurl, typeName from combined_tujian where name like concat('%',#{key},'%')")
    List<Search> findByKey(String key);

    @Select("select name, picurl, typeName from combined_tujian")
    List<Search> getAll();
}
