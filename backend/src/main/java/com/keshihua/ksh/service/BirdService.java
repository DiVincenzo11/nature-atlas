package com.keshihua.ksh.service;

import com.keshihua.ksh.pojo.Bird;

import java.util.List;

public interface BirdService {

    // 查询所有鸟类信息
    List<Object[]> allbirddata();

    // 鸟类详情页
    Bird birdDetail(String name);
}
