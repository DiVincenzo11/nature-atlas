package com.keshihua.ksh.strategy;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.keshihua.ksh.config.domainConfig;
import com.keshihua.ksh.mapper.PlantMapper;
import com.keshihua.ksh.pojo.Plant;
import com.keshihua.ksh.pojo.Result;
import com.keshihua.ksh.utils.FileNameGenerator;
import com.keshihua.ksh.utils.PlantDetectApi;
import com.keshihua.ksh.utils.QiniuCloudUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

/**
 * 植物识别策略，将原先控制器内的植物识别逻辑独立封装，便于按类型动态选择。
 */
@Service
public class PlantDetectionStrategy implements DetectionStrategy {

    @Autowired
    private PlantMapper plantMapper;

    private final String domain = new domainConfig().getDomain();

    @Override
    public DetectionType getType() {
        return DetectionType.PLANT;
    }

    @Override
    public Result detect(MultipartFile file, int userId) {
        if (file.isEmpty()) {
            return Result.error("Please select a file to upload");
        }
        ArrayList<ArrayList<Object>> result = new ArrayList<>();
        try {
            String fileName = FileNameGenerator.generateUniqueFileName();
            String filePath = domain + fileName + ".jpg";
            file.transferTo(new File(filePath));
            File f = new File(filePath);
            String picurl = QiniuCloudUtil.upload(f, ".jpg");
            result = PlantDetectApi.plantdetectAPI(picurl);
            if (!result.isEmpty()) {
                for (ArrayList<Object> item : result) {
                    String name = (String) item.get(0);
                    Plant plant = plantMapper.plantdata(name);
                    if (plant != null) {
                        item.add(plant.getPicurl());
                        String introduction = plant.getIntroduction();
                        JsonObject jsonObject = new Gson().fromJson(introduction, JsonObject.class);
                        Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
                        Map<String, Object> introductionMap = new Gson().fromJson(jsonObject, mapType);
                        item.add(introductionMap);
                    } else {
                        item.add(picurl);
                        item.add("");
                    }
                }
            }
            f.delete();
            return Result.success(result);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("File upload failed");
        }
    }
}
