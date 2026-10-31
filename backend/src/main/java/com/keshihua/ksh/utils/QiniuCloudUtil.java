package com.keshihua.ksh.utils;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.apache.commons.lang.StringUtils;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 七牛云文件上传工具
 */
public class QiniuCloudUtil {

    // 设置AK和SK
    private static final String ACCESS_KEY = System.getenv().getOrDefault("QINIU_ACCESS_KEY", "");
    private static final String SECRET_KEY = System.getenv().getOrDefault("QINIU_SECRET_KEY", "");
    // 要上传的空间名（存储空间）
    private static final String bucketname = System.getenv().getOrDefault("QINIU_BUCKET", "");
    //外链域名
    private static final String domain = System.getenv().getOrDefault("QINIU_DOMAIN", "");
    //指定文件存储目录，没有的话可以写成空字符串
    private static final String route="";

    // 构建密钥
    private static final Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
    /**
     * 文件上传
     * @param file (File对象)
     * @return 文件路径
     */
    public static String upload(File file, String type) throws IOException {
        // 创建上传对象，Region代表存储空间所在地区
        Configuration configuration = new Configuration(Region.huanan());
        UploadManager uploadManager = new UploadManager(configuration);
        try {
            // 构建token
            String token = auth.uploadToken(bucketname);
            if(StringUtils.isEmpty(token))
            {
                System.out.println("获取到token失败！");
                return "";
            }
            //构建文件名
            // 其中UUID.randomUUID()用于生成唯一的文件名
            String imageName = route+UUID.randomUUID()+type;
            //上传操作（这个操作最耗时间！！！！！！）
            Response res = uploadManager.put(file,imageName,token);
            //校验返回结果
            if (res.statusCode ==200)
            {
                return domain+imageName;
            }
            else
            {
                return "";
            }

        }
        catch (QiniuException e)
        {
            Response r = e.response;
            // 请求失败时打印的异常的信息
            e.printStackTrace();
            System.out.println("error "+r.toString());
            try {
                // 响应的文本信息
                System.out.println(r.bodyString());
            }
            catch (QiniuException e1)
            {
                System.out.println("error "+e1.error());
            }
            return "";
        }
    }



}
