package com.keshihua.ksh.service;

import com.keshihua.ksh.pojo.Turtle;

import java.util.List;

public interface TurtleService {

    // 爬行的图鉴数据
    List<List<Object>> allTurtleData();

    // 爬行的详情页数据
    Turtle turtledata(String name);
}
