package com.keshihua.ksh.controller;

import com.keshihua.ksh.pojo.Liangqi;
import com.keshihua.ksh.pojo.Result;
import com.keshihua.ksh.pojo.Turtle;
import com.keshihua.ksh.service.LiangqiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/liangqi")
public class LiangqiController {

    @Autowired
    private LiangqiService liangqiService;

    // 图鉴数据
    @GetMapping("/allliangqidata")
    public Result allturtledata(){
        try {
            return Result.success(liangqiService.allLiangqiData());
        }catch (Exception e){
            return Result.error(e.toString());
        }

    }

    // 详情页数据
    @GetMapping("/liangqidata")
    public Result turtledata(@RequestParam("name") String name){
        try{
            return Result.success(liangqiService.liangqidata(name));

        }catch (Exception e){
            return Result.error(e.toString());
        }
    }
}
