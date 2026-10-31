package com.keshihua.ksh.service;

import com.keshihua.ksh.pojo.Search;

import java.util.List;

public interface SearchService {
    List<Search> findByKey(String key);

    List<Search> getAll();
}
