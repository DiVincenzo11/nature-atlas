package com.keshihua.ksh.config;
import lombok.Data;
@Data public class domainConfig {private String domain=java.nio.file.Paths.get(System.getenv().getOrDefault("UPLOAD_PATH","./data/uploads")).toAbsolutePath().toString()+java.io.File.separator;}
