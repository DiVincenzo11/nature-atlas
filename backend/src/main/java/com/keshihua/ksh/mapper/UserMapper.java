package com.keshihua.ksh.mapper;

import com.keshihua.ksh.pojo.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    // 用户注册
    @Insert("insert into user (username, password) values (#{user.username}, #{user.password})")
    void register(@Param("user") User user);

    // 用户登录
    @Select("select * from user where username=#{user.username} and password=#{user.password}")
    User login(@Param("user") User user);

    // 查询用户信息
    @Select("select * from user where username=#{user.username}")
    User selectUser(@Param("user") User user);

    // 更新用户nickname
    @Update("update user set nickname = #{user.nickname} where username=#{user.username}")
    void updateNickname(@Param("user") User user);

    @Update("update user set headPortrait = #{user.headPortrait} where username=#{user.username}")
    void updateHeadportrait(@Param("user") User user);

    @Update("update user set password = #{newPassword} where username=#{username} and password=#{oldPassword}")
    void updatePassword(String username, String oldPassword, String newPassword);

}
