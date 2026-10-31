package com.keshihua.ksh.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FileNameGenerator {

    public static String generateUniqueFileName() {
        // 格式化当前时间戳
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String timestamp = sdf.format(new Date());

        // 生成随机字符串作为文件名后缀，可以根据需要自定义
        String randomString = Long.toString((long) (Math.random() * 10000));

        // 拼接时间戳和随机字符串作为文件名
        String fileName = "pic_" + timestamp + "_" + randomString;

        return fileName;
    }



}
