package com.keshihua.ksh.config;

import com.keshihua.ksh.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @org.springframework.beans.factory.annotation.Autowired private org.springframework.data.redis.core.StringRedisTemplate redis;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        JwtInterceptor jwtInterceptor = new JwtInterceptor(redis);
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/detect/**", "/user/update*", "/expert/messages");
    }
}
