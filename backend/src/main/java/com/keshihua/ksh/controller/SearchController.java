package com.keshihua.ksh.controller;

import com.keshihua.ksh.pojo.Result;
import com.keshihua.ksh.pojo.Search;
import com.keshihua.ksh.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/search")
    public Result search(String key) {
        try {
            List<Search> result = searchService.findByKey(key);
            return Result.success(result);
        }catch (Exception e){
            return Result.error(e.toString());
        }
    }

    @GetMapping("/getAll")
    public Result getAll() {
        try {
            List<Search> result = searchService.getAll();
            return Result.success(result);
        }catch (Exception e){
            return Result.error(e.toString());
        }
    }
}
