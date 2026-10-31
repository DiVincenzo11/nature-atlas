package com.keshihua.ksh.config;

import lombok.Data;

import java.security.Principal;

/**
 * 简单实现 Principal，名字就是 jti
 */
@Data
public class StompPrincipal implements Principal {
    private final String name;
}
