package com.keshihua.ksh.service;

import com.keshihua.ksh.pojo.Result;
import com.keshihua.ksh.pojo.User;

import java.util.List;


public interface UserService {

    // 新用户注册
    int register(User user);

    // 用户登录
    User login(User user);

    // 更新用户nickname
    void updateNickname(User user);

    // 更新用户头像
    void updateHeadPortrait(User user);
}
