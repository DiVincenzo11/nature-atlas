package com.keshihua.ksh.service.serviceImpl;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.keshihua.ksh.config.MysqlConfig;
import com.keshihua.ksh.mapper.TurtleMapper;
import com.keshihua.ksh.pojo.Turtle;
import com.keshihua.ksh.service.TurtleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class TurtleServiceimpl implements TurtleService {
    @Autowired
    private TurtleMapper turtleMapper;
    // 爬行的图鉴数据
    public List<List<Object>> allTurtleData(){
        List<List<Object>> treeFinal = new ArrayList<>();

        // 数据库连接信息
        MysqlConfig sqlconfig = new MysqlConfig();
        String url = sqlconfig.getUrl();
        String username = sqlconfig.getUsername();
        String password = sqlconfig.getPassword();

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String sql = "SELECT size, name, image_url FROM turtle_tujian";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    List<List<Object>> tree = new ArrayList<>();
                    while (rs.next()) {
                        String size = rs.getString("size");
                        String name = rs.getString("name");
                        String imageUrl = rs.getString("image_url");
                        String longValue = "turtle";

                        List<Object> row = new ArrayList<>();
                        row.add(size);
                        row.add(name);
                        row.add(imageUrl);

                        // 构建树结构
                        List<Object> sizeNode = tree.stream().filter(node -> node.get(0).equals(size)).findFirst()
                                .orElse(null);
                        if (sizeNode == null) {
                            sizeNode = new ArrayList<>();
                            sizeNode.add(size);
                            sizeNode.add(new ArrayList<>());
                            tree.add(sizeNode);
                        }

                        List<Object> longNode = ((List<List<Object>>) sizeNode.get(1)).stream()
                                .filter(node -> node.get(0).equals(longValue)).findFirst().orElse(null);
                        if (longNode == null) {
                            longNode = new ArrayList<>();
                            longNode.add(longValue);
                            longNode.add(new ArrayList<>());
                            ((List<List<Object>>) sizeNode.get(1)).add(longNode);
                        }

                        ((List<List<Object>>) longNode.get(1)).add(row);
                    }

                    // 重新排序树结构
                    for (String size : new String[]{"大型", "中型", "小型"}) {
                        List<Object> sizeNode = tree.stream().filter(node -> node.get(0).equals(size)).findFirst()
                                .orElse(null);
                        if (sizeNode != null) {
                            treeFinal.add(sizeNode);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // 处理异常
        }

        return treeFinal;
    }


    // 爬行的详情页数据
    public Turtle turtledata(String name){
        return turtleMapper.turtledata(name);
    }
}
