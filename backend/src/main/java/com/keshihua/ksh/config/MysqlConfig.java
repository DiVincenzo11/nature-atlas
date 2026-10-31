package com.keshihua.ksh.config;
import lombok.Data;
@Data public class MysqlConfig {
 private String url="jdbc:mysql://"+System.getenv().getOrDefault("DB_HOST","127.0.0.1")+":"+System.getenv().getOrDefault("DB_PORT","3306")+"/"+System.getenv().getOrDefault("DB_NAME","nature_atlas_demo")+"?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true";
 private String username=System.getenv().getOrDefault("DB_USER","nature_demo");
 private String password=System.getenv("DB_PASSWORD");
}
