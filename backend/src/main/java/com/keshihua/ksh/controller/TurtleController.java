package com.keshihua.ksh.controller;

import com.keshihua.ksh.pojo.Result;
import com.keshihua.ksh.pojo.Turtle;
import com.keshihua.ksh.service.TurtleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/turtle")
public class TurtleController {

    @Autowired
    private TurtleService turtleService;

    // 图鉴数据
    @GetMapping("/allturtledata")
    public Result allturtledata(){
        try {
            return Result.success(turtleService.allTurtleData());
        }catch (Exception e){
            return Result.error(e.toString());
        }

    }

    // 详情页数据
    @GetMapping("/turtledata")
    public Result turtledata(@RequestParam String name){
        try{
            return Result.success(turtleService.turtledata(name));

        }catch (Exception e){
            return Result.error(e.toString());
        }
    }
}
