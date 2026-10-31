package com.keshihua.ksh.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GlobalCorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                // 放行所有接口，包括 /ws-chat/info、/ws-chat/xhr-streaming 等 SockJS 握手路径
                .addMapping("/**")
                // 允许前端页面所在的域名，可以替换为具体地址或用通配符
                .allowedOriginPatterns("http://127.0.0.1:18901")
                // 允许携带凭证（cookie、Authorization header 等）
                .allowCredentials(true)
                // 允许的 HTTP 方法
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
                // 预检请求的缓存时间（秒）
                .maxAge(3600);
    }
}
