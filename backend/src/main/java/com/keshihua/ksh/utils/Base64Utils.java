package com.keshihua.ksh.utils;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Base64;

public class Base64Utils {

    public static String imageToBase64(String base64String) {
        try {
            String domain = "D:/Program Files (x86)/zhuomian/pic/";
//            String domain = "/home/ubuntu/images/";
            // 解码Base64字符串为字节数组
            byte[] imageBytes = Base64.getDecoder().decode(base64String);

            // 将字节数组转换为BufferedImage对象
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(bis);

            // 获取当前时间戳
            long timestamp = System.currentTimeMillis();

            // 使用时间戳命名图片文件
            String fileName = domain + "image_" + timestamp + ".jpg";

            // 将图片保存到文件
            try (OutputStream os = new FileOutputStream(fileName)) {
                ImageIO.write(image, "png", os);
            }

            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void main(String[] args) {
        try {
            // Base64编码的字符串
            String base64Data = "iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==";

            // 解码Base64字符串为字节数组
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);

            // 将字节数组转换为BufferedImage对象
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(bis);

            // 将图片保存到文件
            try (OutputStream os = new FileOutputStream("output_image.png")) {
                ImageIO.write(image, "png", os);
            }

            System.out.println("图片已成功保存为 output_image.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
