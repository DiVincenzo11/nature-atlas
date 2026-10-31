package com.keshihua.ksh.interceptor;

import com.keshihua.ksh.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class JwtInterceptor implements HandlerInterceptor {
    private final org.springframework.data.redis.core.StringRedisTemplate redis;
    public JwtInterceptor(org.springframework.data.redis.core.StringRedisTemplate redis){this.redis=redis;}

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //拦截器取到请求先进行判断，如果是OPTIONS请求，则放行
        if("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("token");
        String bearer=request.getHeader("Authorization");
        if(bearer!=null && bearer.startsWith("Bearer ")) token=bearer.substring(7);
        if(token==null || token.isEmpty() || !JwtUtils.verify(token)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        String userId=String.valueOf(JwtUtils.parseToken(token).get("userId"));
        if(!token.equals(redis.opsForValue().get("user:token:"+userId))){response.setStatus(401);return false;}
        String supplied=request.getParameter("userid");
        if(supplied!=null && !supplied.equals(JwtUtils.parseToken(token).get("userId").toString())) {response.setStatus(403);return false;}
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
