package com.keshihua.ksh.utils;
import java.util.ArrayList;
import java.util.List;

public class strchange {

    public static String[][] change(String originalString) {
        // 去掉外层的方括号
        String trimmedString = originalString.substring(1, originalString.length() - 1);

        // 切分字符串以获取每个数组部分
        String[] arrayStrings = trimmedString.split("],\\s*\\[");

        // 创建一个列表来保存最终的二维数组
        List<String[]> arrayList = new ArrayList<>();

        // 处理每个数组部分
        for (String arrayString : arrayStrings) {
            // 去掉数组部分的方括号
            arrayString = arrayString.replaceAll("^\\[|\\]$", "");

            // 分割字符串以获取数组中的元素
            String[] elements = arrayString.split(",\\s*");

            // 添加到列表中
            arrayList.add(elements);
        }

        // 将列表转换为二维数组并返回
        return arrayList.toArray(new String[arrayList.size()][]);

    }

}
