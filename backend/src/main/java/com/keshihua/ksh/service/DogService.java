package com.keshihua.ksh.service;

import com.keshihua.ksh.pojo.Dog;

import java.util.List;

public interface DogService {

    // 狗的图鉴数据
    List<List<Object>> allDogData();

    // 狗的详情页数据
    Dog dogdata(String name);
}
