package com.keshihua.ksh.controller;

import com.keshihua.ksh.pojo.Bird;
import com.keshihua.ksh.pojo.BirdChar;
import com.keshihua.ksh.pojo.Result;
import com.keshihua.ksh.service.BirdCharService;
import com.keshihua.ksh.service.BirdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/bird")
public class BirdController {

    @Autowired
    private BirdService birdService;

    @Autowired
    private BirdCharService birdCharService; // 特征识别

    @GetMapping("/allbirddata")
    public Result allbirddata(){
        try{
            List<Object[]> result = birdService.allbirddata();
            return Result.success(result);
        }catch (Exception e){
            return Result.error(e.toString());
        }

    }

    @GetMapping("/birdDetail")
    public Result birdDetail(@RequestParam String name){
        try{
            Bird result = birdService.birdDetail(name);
            return Result.success(result);
        }catch (Exception e){
            return Result.error(e.toString());
        }
    }

    @PostMapping("/birdchar")
    public Result birdchar(@RequestBody BirdChar birdChar){
        try{
            ArrayList<ArrayList<Object>> result = birdCharService.allChardata(birdChar.getMouth(), birdChar.getCrest(),birdChar.getNeck(),birdChar.getBack(),birdChar.getLeg());
            return Result.success(result);
        }catch (Exception e){
            return Result.error(e.toString());
        }
    }
}
