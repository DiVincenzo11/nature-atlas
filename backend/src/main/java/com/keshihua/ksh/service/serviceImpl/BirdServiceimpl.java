package com.keshihua.ksh.service.serviceImpl;

import com.keshihua.ksh.mapper.BirdMapper;
import com.keshihua.ksh.pojo.Bird;
import com.keshihua.ksh.service.BirdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BirdServiceimpl implements BirdService {

    @Autowired
    private BirdMapper birdMapper;
    public List<Object[]> allbirddata() {

        List<Bird> result = birdMapper.allbirddata();

        List<Object[]> tree = new ArrayList<>();
        for (Bird row : result) {
            String mu = row.getMu();
            String ke = row.getKe();
            String name = row.getName();
            String url = row.getPicUrl();

            List<Object[]> muNode = null;
            for (Object[] node : tree) {
                if (node[0].equals(mu)) {
                    muNode = (List<Object[]>) node[1];
                    break;
                }
            }
            if (muNode == null) {
                muNode = new ArrayList<>();
                Object[] muArr = {mu, muNode};
                tree.add(muArr);
            }

            List<Object[]> keNode = null;
            for (Object[] node : muNode) {
                if ((node[0]).equals(ke)) {
                    keNode = (List<Object[]>) node[1];
                    break;
                }
            }
            if (keNode == null) {
                keNode = new ArrayList<>();
                Object[] keArr = {ke, keNode};
                muNode.add(keArr);
            }

            Object[] nameUrlArr = {name, url};
            keNode.add(nameUrlArr);
        }

        tree.sort((a, b) -> Integer.compare(((List<Object[]>) b[1]).size(), ((List<Object[]>) a[1]).size()));

        return tree;
    }

    // 鸟类详情页
    public Bird birdDetail(String name){

        Bird bird = birdMapper.birdDetail(name);
        List<String> picurls = birdMapper.getpicurl(name);
        if(picurls.isEmpty()){
            List<String> a = new ArrayList<>();
            a.add(bird.getPicUrl());
            bird.setPicurls(a);
        }
        if(!picurls.isEmpty()) bird.setPicurls(picurls);
        return bird;
    }


}
