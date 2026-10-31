package com.keshihua.ksh.mapper;

import com.keshihua.ksh.pojo.Tuiwen;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TuiwenMapper {

    @Select("select * from tuiwen;")
    List<Tuiwen> gettuiwen();
}
