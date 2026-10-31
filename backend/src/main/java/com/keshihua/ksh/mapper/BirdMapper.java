package com.keshihua.ksh.mapper;

import com.keshihua.ksh.pojo.Bird;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BirdMapper {

    // 查询所有鸟类图鉴数据
    @Select("select t1.* from tujiandata_final t1")
    List<Bird> allbirddata();

    // 详情页查询（包括音频数据）
    @Select("select t1.*, t2.voiceURL, t3.introduction, t3.GrowthAndDistribution, t3.characteristic, t3.LivingHabit from tujiandata_final t1 left join voicedata t2 on t1.name=t2.Name left join chinesebird t3 on t1.name=t3.name where t1.name=#{name};")
    Bird birdDetail(@Param("name") String name);

    // 鸟类图片识别处理
    @Select("select name,name_latin,picurl from tujiandata_final where name=#{name} limit 1")
    Bird birdpartdetail(String name);

    @Select("select picurl from picurl where name=#{name}")
    List<String> getpicurl(String name);
}
