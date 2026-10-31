package com.keshihua.ksh.service.serviceImpl;

import com.keshihua.ksh.mapper.VideoMapper;
import com.keshihua.ksh.pojo.video;
import com.keshihua.ksh.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class VideoServiceimpl implements VideoService {
    @Autowired
    private VideoMapper videoMapper;
    public List<video> getvideo(){
        return videoMapper.getvideo();
    }
}
