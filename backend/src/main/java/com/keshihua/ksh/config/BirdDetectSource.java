package com.keshihua.ksh.config;
import lombok.Data;
@Data public class BirdDetectSource {private String apiSource=System.getenv().getOrDefault("BIRD_API_URL","");}
