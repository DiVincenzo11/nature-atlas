package com.keshihua.ksh.utils;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.keshihua.ksh.config.BirdDetectSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.keshihua.ksh.utils.FileUploadClient.post;

public class MyBirdDetectApi {

    public static Map<String, Object> BirdDetect(String filePath) throws IOException {
        try{
            BirdDetectSource birdDetectSource = new BirdDetectSource();// 从配置读取 URL

            // 将图片以 multipart/form-data 方式发送至识别服务，得到 JSON 字符串响应
            String initResult = post(birdDetectSource.getApiSource() , filePath);

            // 使用 Gson 解析 JSON 字符串为 Map
            Gson gson = new Gson();
            LinkedTreeMap<String, Object> resultMap = gson.fromJson(initResult, new TypeToken<LinkedTreeMap<String, Object>>() {
            }.getType());
            return resultMap;
//            // 获取 result 对应的值
//            Map<String, Object> info = (Map<String, Object>) resultMap.get("info");
//
//
//            // 获取 detections 对应的列表
//            List<LinkedTreeMap<String, Object>> detections = (List<LinkedTreeMap<String, Object>>) resultMap.get("result");
//
//            ArrayList<ArrayList<Object>> finalResult = new ArrayList();
//            // 遍历 detections 列表，提取 name 和 confidence
//            for (LinkedTreeMap<String, Object> detection : detections) {
//                String name = (String) detection.get("name");
//                Double confidence = (Double) detection.get("confidence");
//                ArrayList<Object> part = new ArrayList<>();
//                part.add(name);
//                part.add(confidence);
//                // 创建 Detection 对象并添加到数组中
//                finalResult.add(part);
//            }
//
//            return finalResult;
        } catch (Exception e){
            Map<String, Object> resultMap = new HashMap<>();
            return resultMap;
        }
    }
}
