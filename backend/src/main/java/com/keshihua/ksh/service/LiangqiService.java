package com.keshihua.ksh.service;

import com.keshihua.ksh.pojo.Liangqi;
import com.keshihua.ksh.pojo.Turtle;

import java.util.List;

public interface LiangqiService {

    // 两栖的图鉴数据
    List<List<Object>> allLiangqiData();

    // 两栖的详情页数据
    Liangqi liangqidata(String name);
}
