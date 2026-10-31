package com.keshihua.ksh.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.Map;

public class JwtUtils {

    private static final String KEY = java.util.Objects.requireNonNull(System.getenv("JWT_SECRET"), "JWT_SECRET required"); //密钥
    private static final long EXPIRATION_TIME = 1000*60*60;

    //接收业务数据，生成token并返回
    public static String genToken(Map<String, Object> claims){
        return JWT.create()
                .withClaim("claims", claims)
                .withExpiresAt(new Date(System.currentTimeMillis()+EXPIRATION_TIME))//过期时间
                .sign(Algorithm.HMAC256(KEY));
    }

    // 解析token，验证token并返回验证结果
    public static Map<String, Object> parseToken(String token){
        return JWT.require(Algorithm.HMAC256(KEY))
                .build()
                .verify(token)
                .getClaim("claims")
                .asMap();
    }

    public static boolean verify(String token){
        try{
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(KEY)).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            if(isTokenExpired(token)){
                return false;
            }
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public static boolean isTokenExpired(String token) {
        try{
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(KEY)).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getExpiresAt().before(new Date());
        } catch (JWTVerificationException e) {
            return true;
        }
    }

    /**
     * 计算 token 距离过期还有多少毫秒
     */
    public static long getRemainingMillis(String token) {
        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC256(KEY))
                    .build()
                    .verify(token);
            Date exp = jwt.getExpiresAt();
            long now = System.currentTimeMillis();
            return Math.max(exp.getTime() - now, 0);
        } catch (JWTVerificationException e) {
            return 0;
        }
    }
}
