package com.keshihua.ksh.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keshihua.ksh.mapper.BirdMapper;
import com.keshihua.ksh.mapper.DetectLogMapper;
import com.keshihua.ksh.pojo.Bird;
import com.keshihua.ksh.pojo.DetectLog;
import com.keshihua.ksh.pojo.Result;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.net.URI;
import java.util.*;

/** Local inference adapter; independent of cloud uploads and original competition models. */
@Service
public class LocalBirdRecognitionService {
    private final BirdMapper birds;
    private final DetectLogMapper logs;
    private final ObjectMapper json;
    public LocalBirdRecognitionService(BirdMapper birds, DetectLogMapper logs, ObjectMapper json) {
        this.birds=birds; this.logs=logs; this.json=json;
    }

    public Result detect(MultipartFile file, int userId) {
        if (file.isEmpty() || file.getSize()>8L*1024*1024) return Result.error("请选择不超过8MB的有效图片");
        try {
            URI endpoint=URI.create(System.getenv().getOrDefault("BIRD_LOCAL_API_URL","http://127.0.0.1:18902/predict"));
            if (!"http".equals(endpoint.getScheme()) || !Set.of("127.0.0.1","localhost","[::1]","::1").contains(endpoint.getHost()))
                return Result.error("本地识别地址必须使用回环HTTP服务");
            SimpleClientHttpRequestFactory factory=new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(3000); factory.setReadTimeout(60000);
            RestTemplate client=new RestTemplate(factory);
            HttpHeaders headers=new HttpHeaders(); headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String,Object> parts=new LinkedMultiValueMap<>();
            parts.add("file",new ByteArrayResource(file.getBytes()) {
                @Override public String getFilename() { return "upload.jpg"; }
            });
            String raw=client.postForObject(endpoint,new HttpEntity<>(parts,headers),String.class);
            Map<String,Object> response=json.readValue(raw,new TypeReference<Map<String,Object>>(){});
            String image=(String)response.get("image");
            if (image==null || !image.startsWith("data:image/jpeg;base64,")) return Result.error("本地识别服务响应不完整");
            Map<String,Object> counts=(Map<String,Object>)response.get("info");
            List<List<Object>> info=new ArrayList<>();
            for (Map.Entry<String,Object> entry:counts.entrySet()) {
                Bird bird=birds.birdpartdetail(entry.getKey());
                info.add(Arrays.asList(entry.getKey(),entry.getValue(),bird==null?"":bird.getNameLatin(),bird==null?"":bird.getPicUrl()));
            }
            Map<String,Object> output=new LinkedHashMap<>();
            output.put("detect_img_url",image); output.put("info_list",info);
            output.put("detections",response.get("detections")); output.put("model",response.get("model"));
            output.put("status",response.get("status")); output.put("elapsed_seconds",response.get("elapsed_seconds"));
            // Do not put base64 images into the historical URL column. No cloud upload is performed.
            DetectLog log=new DetectLog();log.setCategory(0);log.setUserid(userId);log.setPicurl("");
            log.setResult(String.join(";",counts.keySet()));logs.birdlog_insert(log);
            return Result.success(output);
        } catch (Exception failure) {
            return Result.error("本地两阶段识别失败，请确认模型服务已启动且图片有效；未返回模拟预测。");
        }
    }
}
