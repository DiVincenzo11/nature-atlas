package com.keshihua.ksh.service.serviceImpl;
import com.keshihua.ksh.utils.DynamicQuery;
import com.keshihua.ksh.service.BirdCharService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class BirdCharServiceimpl implements BirdCharService {
    public ArrayList<ArrayList<Object>> allChardata(String mouth, String crest, String neck, String back, String leg){
        return DynamicQuery.birdchar(mouth, crest, neck, back, leg);
    }
}
