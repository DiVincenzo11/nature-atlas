package com.keshihua.ksh.strategy;

import com.keshihua.ksh.config.domainConfig;
import com.keshihua.ksh.mapper.BirdMapper;
import com.keshihua.ksh.mapper.DetectLogMapper;
import com.keshihua.ksh.pojo.Bird;
import com.keshihua.ksh.pojo.DetectLog;
import com.keshihua.ksh.pojo.QiniuUploadMessage;
import com.keshihua.ksh.pojo.Result;
import com.keshihua.ksh.producer.QiniuCloudProducer;
import com.keshihua.ksh.utils.Base64Utils;
import com.keshihua.ksh.utils.FileNameGenerator;
import com.keshihua.ksh.utils.MyBirdDetectApi;
import com.keshihua.ksh.utils.QiniuCloudUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * 鸟类识别策略，将原先控制器内的鸟类识别逻辑独立出来，方便按类型动态分发。
 */
@Service
public class BirdDetectionStrategy implements DetectionStrategy {

    @Autowired
    private BirdMapper birdMapper;
    @Autowired
    private DetectLogMapper detectLogMapper;
    @Autowired
    private QiniuCloudProducer qiniuCloudProducer;

    private final String domain = new domainConfig().getDomain();

    @Override
    public DetectionType getType() {
        return DetectionType.BIRD;
    }

    @Override
    public Result detect(MultipartFile file, int userId) {
        if (file.isEmpty()) {
            return Result.error("Please select a file to upload");
        }
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> finalresult = new HashMap<>();
        String resultStr = "";
        try {
            String fileName = FileNameGenerator.generateUniqueFileName();
            String filePath = domain + fileName + ".jpg";

            File targetFile = new File(filePath);
            File parentDir = targetFile.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            file.transferTo(new File(filePath));
            File f = new File(filePath);

            QiniuUploadMessage message = new QiniuUploadMessage();
            message.setFilePath(filePath);
            message.setUserId(userId);
            qiniuCloudProducer.sendUploadMessage(message);

            result = MyBirdDetectApi.BirdDetect(filePath);
            String imageBase64 = (String) result.get("image");
            String processImgBase64 = imageBase64.split(",")[1];
            String changeImageLocation = Base64Utils.imageToBase64(processImgBase64);
            File tempFile = new File(changeImageLocation);
            String detectImgUrl = QiniuCloudUtil.upload(tempFile, ".jpg");
            finalresult.put("detect_img_url", detectImgUrl);

            Map<String, Double> info = (Map<String, Double>) result.get("info");
            ArrayList<ArrayList<Object>> infoList = new ArrayList<>();
            for (Map.Entry<String, Double> entry : info.entrySet()) {
                ArrayList<Object> part = new ArrayList<>();
                String name = entry.getKey();
                resultStr = resultStr + name + ";";
                Double num = entry.getValue();
                try {
                    Bird bird = birdMapper.birdpartdetail(name);
                    String nameLatin = "";
                    String birdpicurl = "";
                    if (bird != null) {
                        nameLatin = bird.getNameLatin();
                        birdpicurl = bird.getPicUrl();
                    }
                    part.add(name);
                    part.add(num);
                    part.add(nameLatin);
                    part.add(birdpicurl);
                    infoList.add(part);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            finalresult.put("info_list", infoList);
            f.delete();

            DetectLog log = new DetectLog();
            log.setCategory(0);
            log.setPicurl(detectImgUrl);
            log.setUserid(userId);
            log.setResult(resultStr);
            detectLogMapper.birdlog_insert(log);
            return Result.success(finalresult);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("File upload failed");
        }
    }
}
