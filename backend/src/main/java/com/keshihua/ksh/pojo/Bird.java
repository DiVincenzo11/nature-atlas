package com.keshihua.ksh.pojo;

import lombok.Data;

import java.util.List;

@Data
public class Bird {

    private String mu;
    private String muEng;
    private String ke;
    private String keEng;
    private String name;
    private String nameLatin;
    private String picUrl;
    private String voiceURL;
    private String introduction;
    private String GrowthAndDistribution;
    private String characteristic;
    private String LivingHabit;
    private List<String> picurls;
}
