package com.keshihua.ksh.utils;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class BirdDetectApi {
    public static ArrayList<ArrayList<Object>> BirdDetect(String picurl) throws IOException {

        HttpResponse<String> response = Unirest.post("https://ai.forestrycat.com/ai/gsbh/dggs/aidetect?client_id=apifox&timestamp=1688138537000&nonce=abcdefabcdefabcdefabcdef&signature=")
                .header("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                .field("position", "nltk")
                .field("image_url", picurl)
                .asString();
        String initresult = response.getBody();

        // 使用 Gson 解析 JSON 字符串为 Map
        Gson gson = new Gson();
        LinkedTreeMap<String, Object> resultMap = gson.fromJson(initresult, new TypeToken<LinkedTreeMap<String, Object>>() {
        }.getType());

        // 获取 result 对应的值
        LinkedTreeMap<String, Object> result = (LinkedTreeMap<String, Object>) resultMap.get("result");

        // 获取 detections 对应的列表
        List<LinkedTreeMap<String, Object>> detections = (List<LinkedTreeMap<String, Object>>) result.get("detections");

        ArrayList<ArrayList<Object>> finalResult = new ArrayList();
        // 遍历 detections 列表，提取 name 和 confidence
        for (LinkedTreeMap<String, Object> detection : detections) {
            String name = (String) detection.get("name");
            Double confidence = (Double) detection.get("confidence");
            ArrayList<Object> part = new ArrayList<>();
            part.add(name);
            part.add(confidence);
            // 创建 Detection 对象并添加到数组中
            finalResult.add(part);
        }

        return finalResult;
    }




}
