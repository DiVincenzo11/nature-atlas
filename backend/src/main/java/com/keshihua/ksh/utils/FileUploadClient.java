package com.keshihua.ksh.utils;

import java.io.File;
import kong.unirest.json.JSONObject;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;



public class FileUploadClient {

    // 用来向任意 URL 以 multipart 方式上传文件，并返回响应字符串
    public static String post(String url, String filePath) {
        File file = new File(filePath);
        String result = null;

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost uploadFile = new HttpPost(url);

            // Use MultipartEntityBuilder to create the multipart request
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("image_file", file);

            // Build the HttpEntity and set it to the HttpPost
            HttpEntity multipart = builder.build();
            uploadFile.setEntity(multipart);

            // Execute the request and get the response
            try (CloseableHttpResponse response = httpclient.execute(uploadFile)) {
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    // Convert response entity to string
                    String responseString = EntityUtils.toString(responseEntity);

                    // Parse the response string as JSON
                    JSONObject jsonResponse = new JSONObject(responseString);
                    result = jsonResponse.toString(); // Or process the JSON as needed
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
