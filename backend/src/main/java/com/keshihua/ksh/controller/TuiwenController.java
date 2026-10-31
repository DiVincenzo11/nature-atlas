package com.keshihua.ksh.controller;

import com.keshihua.ksh.pojo.Result;
import com.keshihua.ksh.service.TuiwenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TuiwenController {

    @Autowired
    private TuiwenService tuiwenService;

    @GetMapping("/tuiwen")
    public Result gettuiwen(){
        return Result.success(tuiwenService.gettuiwen());
    }
}
