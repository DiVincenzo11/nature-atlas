package com.keshihua.ksh.controller;

import com.keshihua.ksh.config.domainConfig;
import com.keshihua.ksh.mapper.UserMapper;
import com.keshihua.ksh.pojo.Result;
import com.keshihua.ksh.pojo.User;
import com.keshihua.ksh.service.UserService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.DigestUtils;
import com.keshihua.ksh.utils.FileNameGenerator;
import com.keshihua.ksh.utils.JwtUtils;
import com.keshihua.ksh.utils.QiniuCloudUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.Duration;
import java.util.*;

@RestController
@RequestMapping("/user")
public class UserController {
    domainConfig domainConfig = new domainConfig();
    private final String domain = domainConfig.getDomain();
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate redis;
    @Autowired
    private SimpMessagingTemplate wsTemplate;
    @Autowired
    private UserMapper userMapper;

    private boolean owns(String username,jakarta.servlet.http.HttpServletRequest request) {
        String token=request.getHeader("token"); String auth=request.getHeader("Authorization");
        if(auth!=null && auth.startsWith("Bearer ")) token=auth.substring(7);
        User query=new User();query.setUsername(username);User target=userMapper.selectUser(query);
        return target!=null && String.valueOf(target.getUserId()).equals(String.valueOf(JwtUtils.parseToken(token).get("userId")));
    }
    @PostMapping("/register")
    public Result register(@RequestBody User user){
        try {
            String salt_md5pwd=user.getPassword()+"njfk"; //后端加盐操作
            String final_md5pwd= DigestUtils.md5DigestAsHex(salt_md5pwd.getBytes());// 最终的 MD5 密码
            user.setPassword(final_md5pwd);
            int result = userService.register(user); // 1是成功注册，0是已有帐号
            return Result.success(result);
        }catch (Exception e){
            return Result.error(e.toString());
        }


    }

    @PostMapping("/login")
    public Result login(@RequestBody User user){
        try{
            String salt_md5pwd=user.getPassword()+"njfk"; //后端加盐操作
            String final_md5pwd= DigestUtils.md5DigestAsHex(salt_md5pwd.getBytes());// 最终的 MD5 密码
            user.setPassword(final_md5pwd);
            User result = userService.login(user);
            if(result==null) return Result.error("账号或密码错误");
            result.setPassword(null);

            Map<String, Object> claims = Map.of(
                    "userId", result.getUserId(),
                    "jti", UUID.randomUUID().toString()   // 必带一个唯一 ID
            );
            String newToken = JwtUtils.genToken(claims);

            // 3. Redis Key：每个用户只保存一个 token
            String redisKey = "user:token:" + result.getUserId();
            // 取出旧 token
            String oldToken = redis.opsForValue().get(redisKey);
            if (oldToken != null) {
                // 通过 WebSocket 通知旧端下线
                String oldJti = JwtUtils.parseToken(oldToken).get("jti").toString();
                // 点对点推给那条旧会话
                wsTemplate.convertAndSendToUser(oldJti, "/queue/offline",
                        "您的帐号已在另一端登录，您被迫下线");
                // 清理旧 jti 映射
                redis.delete("jti:user:" + oldJti);
            }

            long ttlMillis = JwtUtils.getRemainingMillis(newToken);
            // 4. 存新 token 到 Redis，存活期同 JWT 本身
            redis.opsForValue().set(redisKey, newToken,
                    Duration.ofMillis(ttlMillis));
            // 4.1 额外记录 jti -> userId 映射，便于 WebSocket 点对点查找
            redis.opsForValue().set("jti:user:" + claims.get("jti"), String.valueOf(result.getUserId()),
                    Duration.ofMillis(ttlMillis));

            // 5. 返回
            return Result.success(List.of(result, newToken));
        }catch (Exception e){
            return Result.error(e.toString());
        }
    }

    @PostMapping("/updateNickname")
    public Result updateNickname(@RequestBody User user, jakarta.servlet.http.HttpServletRequest request){
        try{
            if(!owns(user.getUsername(),request)) return Result.error("无权修改其他账号");
            userService.updateNickname(user); // 1是更新成功
            return Result.success(1);
        }catch (Exception e){
            return Result.error(e.toString());
        }
    }

    @PostMapping("/updateHeadportrait")
    public Result updateHeadportrait(@RequestParam("file")MultipartFile file, @RequestParam("username")String username, jakarta.servlet.http.HttpServletRequest request){
        try{
            if(!owns(username,request)) return Result.error("无权修改其他账号");
            if(!"true".equalsIgnoreCase(System.getenv("CLOUD_UPLOAD_ENABLED"))) return Result.error("云头像上传未配置");
            // 获取上传文件的原始名称
            String fileName = FileNameGenerator.generateUniqueFileName();
            // 设置文件保存路径，这里假设保存在当前工程的根目录下
            String filePath = domain + fileName + ".jpg";
            // 将文件保存到指定路径
            file.transferTo(new File(filePath));
            File f = new File(filePath);
            String picurl = QiniuCloudUtil.upload(f, ".jpg");
            User user = new User();
            user.setUsername(username);
            user.setHeadPortrait(picurl);
            f.delete();
            userService.updateHeadPortrait(user); // 1是更新成功
            return Result.success(picurl);
        }catch (Exception e){
            return Result.error(e.toString());
        }
    }

    @PostMapping("/updatePassword")
    public Result updatePassword(@RequestParam("username") String username, @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, jakarta.servlet.http.HttpServletRequest request){
        try{
            if(!owns(username,request)) return Result.error("无权修改其他账号");
            User user = new User();

            String old_salt_md5pwd=oldPassword+"njfk"; //后端加盐操作
            String old_final_md5pwd= DigestUtils.md5DigestAsHex(old_salt_md5pwd.getBytes());// 最终的 MD5 密码
            String new_salt_md5pwd=newPassword+"njfk"; //后端加盐操作
            String new_final_md5pwd= DigestUtils.md5DigestAsHex(new_salt_md5pwd.getBytes());// 最终的 MD5 密码
            user.setUsername(username);
            user.setPassword(old_final_md5pwd);
            if(userMapper.login(user)==null){
                return Result.success(0);
            }
            userMapper.updatePassword(username, old_final_md5pwd, new_final_md5pwd); // 1是更新成功
            return Result.success(1);
        }catch (Exception e){
            return Result.error(e.toString());
        }
    }
}
