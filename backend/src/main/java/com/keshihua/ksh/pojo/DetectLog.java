package com.keshihua.ksh.pojo;

import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

@Data
public class DetectLog {

    private int id;
    private int userid;
    private String picurl;
    private String result;
    private Timestamp detectTime;
    private int category; // 0表示鸟类，1表示植物
    private String[][] finallist;

}
