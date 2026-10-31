package com.keshihua.ksh.service.serviceImpl;

import com.keshihua.ksh.mapper.SearchMapper;
import com.keshihua.ksh.pojo.Search;
import com.keshihua.ksh.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchServiceimpl implements SearchService {

    @Autowired
    private SearchMapper searchMapper;

    @Override
    public List<Search> findByKey(String key) {
        return searchMapper.findByKey(key);
    }

    @Override
    public List<Search> getAll() {
        return searchMapper.getAll();
    }
}
