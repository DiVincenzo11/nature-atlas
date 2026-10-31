package com.keshihua.ksh.mapper;

import com.keshihua.ksh.pojo.DetectLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DetectLogMapper {

    // 保存识别记录
    @Insert("insert into detectlog (userId, picurl, result, category) values (#{log.userid}, #{log.picurl}, #{log.result}, #{log.category})")
    void insert(@Param("log")DetectLog log);

    // 查询所有识别记录
    @Select("select picurl, result, DATE_FORMAT(detectTime, '%Y-%m-%d') AS detectTime from detectlog where userId=#{userid}")
    List<DetectLog> select(int userid);

    // 查询某种识别记录
    @Select("select picurl, result, DATE_FORMAT(detectTime, '%Y-%m-%d') AS detectTime from detectlog where userId=#{userid} and category=#{category}")
    List<DetectLog> selectone(int userid, int category);

    // 删除识别记录
    @Delete("delete from detectlog where id=#{id}")
    void delete(int id);

    @Insert("insert into birdlog (userid, picurl, result) values (#{log.userid}, #{log.picurl}, #{log.result})")
    void birdlog_insert(@Param("log")DetectLog log);

    @Select("select picurl, result, DATE_FORMAT(detectTime, '%Y-%m-%d') AS detectTime from birdlog where userId=#{userid} order by id desc")
    List<DetectLog> select_birdlog(int userid);
}
