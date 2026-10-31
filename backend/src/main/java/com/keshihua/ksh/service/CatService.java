package com.keshihua.ksh.service;

import com.keshihua.ksh.pojo.Cat;
import com.keshihua.ksh.pojo.Dog;

import java.util.List;

public interface CatService {

    // 猫的图鉴数据
    List<List<Object>> allCatData();

    // 猫的详情页数据
    Cat catdata(String name);
}
