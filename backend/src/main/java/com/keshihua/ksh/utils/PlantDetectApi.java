package com.keshihua.ksh.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlantDetectApi {
    public static ArrayList<ArrayList<Object>> plantdetectAPI(String imgUrl) {
        String accessToken = getAccessToken();
        String requestUrl = "https://aip.baidubce.com/rest/2.0/image-classify/v1/plant?access_token=" + accessToken;

        try {
            HttpResponse<String> response = Unirest.post(requestUrl)
                    .field("url", imgUrl)
                    .asString();

            String initresult = response.getBody();

            // 使用 Gson 解析 JSON 字符串为 Map
            Gson gson = new Gson();
            Map<String, Object> resultMap = gson.fromJson(initresult, new TypeToken<Map<String, Object>>() {}.getType());

            // 获取 result 对应的值
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) resultMap.get("result");

            ArrayList<ArrayList<Object>> result = new ArrayList<>();
            for(Map<String, Object> item: resultList){
                Double score = (Double) item.get("score");
                String name = (String) item.get("name");
                ArrayList<Object> part = new ArrayList<>();
                part.add(name);
                part.add(score);
                result.add(part);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    // 获取百度API的访问令牌
    public static String getAccessToken() {

        String url = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id="+System.getenv("BAIDU_API_KEY")+"&client_secret="+System.getenv("BAIDU_SECRET_KEY");
        try {
            HttpResponse<JsonNode> response = Unirest.post(url)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .asJson();
            JsonNode responseBody = response.getBody();
            String accessToken = responseBody.getObject().getString("access_token");
            return accessToken;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        // 测试示例
        String imgUrl = "https://th.bing.com/th/id/R.ed4430fae2203ba2a478887cc549d215?rik=K5ZBr9%2bfKuoTeg&riu=http%3a%2f%2fpic1.huashichang.com%2f2017%2f0321%2f21%2f58d12e8dbcf83.jpg&ehk=kan3BaAC5Qjk3F2lJN3szhSghzQNzZcrZB8DAAbnpE8%3d&risl=&pid=ImgRaw&r=0";
        ArrayList<ArrayList<Object>> detectionResult = plantdetectAPI(imgUrl);
        System.out.println(detectionResult);
    }

}
