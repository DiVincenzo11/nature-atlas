package com.keshihua.ksh.controller;
import com.keshihua.ksh.mapper.DetectLogMapper;
import com.keshihua.ksh.pojo.BirdChar;
import com.keshihua.ksh.pojo.DetectLog;
import com.keshihua.ksh.pojo.Result;
import com.keshihua.ksh.service.DetectionRateLimiter;
import com.keshihua.ksh.service.LocalBirdRecognitionService;
import com.keshihua.ksh.strategy.DetectionStrategy;
import com.keshihua.ksh.strategy.DetectionStrategyFactory;
import com.keshihua.ksh.utils.DynamicQuery;
import com.keshihua.ksh.utils.strchange;
import io.github.bucket4j.ConsumptionProbe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/detect")
public class DetectController {
    @Autowired
    private DetectLogMapper detectLogMapper;

    @Autowired
    private DetectionStrategyFactory detectionStrategyFactory;

    @Autowired
    private DetectionRateLimiter detectionRateLimiter;

    @Autowired
    private LocalBirdRecognitionService localBirdRecognitionService;

    /**
     * 通用图片识别入口，基于路径片段动态选择具体策略，并进行全局与用户级限流。
     */
    @PostMapping("/{type}")
    public Result detect(@PathVariable("type") String type,
                         @RequestParam("file") MultipartFile file,
                         @RequestParam("userid") int userid) {
        try {
            boolean localBird="birdpic".equals(type) && "true".equalsIgnoreCase(System.getenv("BIRD_LOCAL_ENABLED"));
            if (!localBird && !"true".equalsIgnoreCase(System.getenv("CLOUD_DETECTION_ENABLED"))) return Result.error("图片识别服务未配置；本地演示支持图鉴和特征检索，不返回模拟识别结果。");
            // 1) 全局/按类型令牌桶限流
            ConsumptionProbe probe = detectionRateLimiter.tryConsume(type);
            if (!probe.isConsumed()) {
                long waitSeconds = (long) Math.ceil(probe.getNanosToWaitForRefill() / 1_000_000_000.0);
                return Result.error("请求过于频繁，请" + waitSeconds + "秒后再试");
            }
            // 2) 单用户 3 秒一次
            if (!detectionRateLimiter.allowUserOnceIn3Seconds(userid)) {
                return Result.error("3秒内仅允许识别一次，请稍后重试");
            }
            if (localBird) return localBirdRecognitionService.detect(file,userid);
            DetectionStrategy strategy = detectionStrategyFactory.getStrategy(type);
            return strategy.detect(file, userid);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    // 鸟类特征识别
    @GetMapping ("/birdchar")
    public Result birdchar(@RequestBody BirdChar birdChar){
        try{
            ArrayList<ArrayList<Object>> result = DynamicQuery.birdchar(birdChar.getMouth(), birdChar.getCrest(), birdChar.getNeck(), birdChar.getBack(), birdChar.getLeg());
            return Result.success(result);
        }catch (Exception e){
            return Result.error(e.toString());
        }

    }

    // 识别记录查询
    @GetMapping("/log")
    public Result log(@RequestParam("userid") int userid){
        try{
            List<DetectLog> result = detectLogMapper.select_birdlog(userid);
            return Result.success(result);
        }catch (Exception e){
            return Result.error(e.toString());
        }
    }

    // 识别记录查询
    @GetMapping("/birdlog")
    public Result birdlog(@RequestParam("userid") int userid, @RequestParam("category") int category){
        try{
            List<DetectLog> result = detectLogMapper.selectone(userid, category);
            for(DetectLog item: result){
                String[][] resultStr = strchange.change(item.getResult());
                item.setFinallist(resultStr);
            }
            Map<String, List<DetectLog>> groupedByDate = result.stream()
                    .collect(Collectors.groupingBy(detectLog -> {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        return sdf.format(detectLog.getDetectTime());
                    }));
            return Result.success(groupedByDate);
        }catch (Exception e){
            return Result.error(e.toString());
        }
    }
}

