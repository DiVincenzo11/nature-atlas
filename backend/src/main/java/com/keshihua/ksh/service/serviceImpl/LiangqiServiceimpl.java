package com.keshihua.ksh.service.serviceImpl;

import com.keshihua.ksh.config.MysqlConfig;
import com.keshihua.ksh.mapper.LiangqiMapper;
import com.keshihua.ksh.pojo.Liangqi;
import com.keshihua.ksh.service.LiangqiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class LiangqiServiceimpl implements LiangqiService {

    @Autowired
    private LiangqiMapper liangqiMapper;
    public List<List<Object>> allLiangqiData(){
        List<List<Object>> tree = new ArrayList<>();

        // 数据库连接信息
        MysqlConfig sqlconfig = new MysqlConfig();
        String url = sqlconfig.getUrl();
        String username = sqlconfig.getUsername();
        String password = sqlconfig.getPassword();

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String sql = "SELECT * FROM liangqi_tujian";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String ke = rs.getString(1).split(" ")[0];
                        String shu = rs.getString(2).split(" ")[0];
                        String name = rs.getString(4);
                        String urlValue = rs.getString(6);

                        List<Object> row = new ArrayList<>();
                        row.add(ke);
                        row.add(shu);
                        row.add(name);
                        row.add(urlValue);

                        // 构建树结构
                        List<Object> keNode = tree.stream().filter(node -> node.get(0).equals(ke)).findFirst()
                                .orElse(null);
                        if (keNode == null) {
                            keNode = new ArrayList<>();
                            keNode.add(ke);
                            keNode.add(new ArrayList<>());
                            tree.add(keNode);
                        }

                        List<Object> shuNode = ((List<List<Object>>) keNode.get(1)).stream()
                                .filter(node -> node.get(0).equals(shu)).findFirst().orElse(null);
                        if (shuNode == null) {
                            shuNode = new ArrayList<>();
                            shuNode.add(shu);
                            shuNode.add(new ArrayList<>());
                            ((List<List<Object>>) keNode.get(1)).add(shuNode);
                        }

                        ((List<List<Object>>) shuNode.get(1)).add(row);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // 处理异常
        }

        return tree;

    }

    // 两栖的详情页数据
    public Liangqi liangqidata(String name){
        return liangqiMapper.liangqidata(name);
    }
}
