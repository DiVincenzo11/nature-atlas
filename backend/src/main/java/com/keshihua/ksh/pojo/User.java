package com.keshihua.ksh.pojo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class User {

    private int userId;
    private String username;
    private String password;
    private String nickname;
    private Timestamp registerDate;
    private String headPortrait;
}
