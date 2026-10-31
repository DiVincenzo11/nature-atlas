package com.keshihua.ksh.controller;

import com.keshihua.ksh.pojo.Cat;
import com.keshihua.ksh.pojo.Result;
import com.keshihua.ksh.service.CatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cat")
public class CatController {

    @Autowired
    private CatService catService;

    // 图鉴数据
    @GetMapping("/allcatdata")
    public Result allcatdata(){
        try {
            return Result.success(catService.allCatData());
        }catch (Exception e){
            return Result.error(e.toString());
        }

    }

    // 详情页数据
    @GetMapping("catdata")
    public Result catdata(@RequestParam String name){
        try{
            return Result.success(catService.catdata(name));

        }catch (Exception e){
            return Result.error(e.toString());
        }
    }
}
