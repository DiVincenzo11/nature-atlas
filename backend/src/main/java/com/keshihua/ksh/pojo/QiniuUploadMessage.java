package com.keshihua.ksh.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class QiniuUploadMessage implements Serializable {
    private String filePath;
    private int userId;
}
