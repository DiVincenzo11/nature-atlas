package com.keshihua.ksh.controller;

import com.keshihua.ksh.pojo.Result;
import com.keshihua.ksh.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping("/video")
    public Result getvideo(){
        return Result.success(videoService.getvideo());
    }

}
