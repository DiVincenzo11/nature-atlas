package com.keshihua.ksh.controller;

import com.keshihua.ksh.pojo.Dog;
import com.keshihua.ksh.pojo.Result;
import com.keshihua.ksh.service.DogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dog")
public class DogController {
    @Autowired
    private DogService dogService;

    // 图鉴数据
    @GetMapping("/alldogdata")
    public Result alldogdata(){
        try {
            return Result.success(dogService.allDogData());
        }catch (Exception e){
            return Result.error(e.toString());
        }

    }

    // 详情页数据
    @GetMapping("dogdata")
    public Result dogdata(@RequestParam String name){
        try{
            return Result.success(dogService.dogdata(name));

        }catch (Exception e){
            return Result.error(e.toString());
        }
    }
}
