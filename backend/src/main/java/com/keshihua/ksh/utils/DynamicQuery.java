package com.keshihua.ksh.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class DynamicQuery {
    public static ArrayList<ArrayList<Object>> birdchar(String mouth, String crest, String neck, String back, String leg){
        ArrayList<ArrayList<Object>> result = new ArrayList<>();
        mouth=mouth==null?"":mouth; crest=crest==null?"":crest; neck=neck==null?"":neck; back=back==null?"":back; leg=leg==null?"":leg;
        String query;
        // 构建基本的查询语句
        if(Objects.equals(mouth, "") && Objects.equals(crest, "") && Objects.equals(neck, "") && Objects.equals(back, "") && Objects.equals(leg, "")){
            query = "select t1.*, max(t2.picurl) as picurl from alldata t1 left join picurl t2 on t1.name=t2.name group by t1.name, t1.back, t1.crest, t1.leg, t1.mouth, t1.neck;";

        }else{
            StringBuilder queryBuilder = new StringBuilder("select t1.*, max(t2.picurl) as picurl from alldata t1 left join picurl t2 on t1.name=t2.name where ");
            // 使用动态 SQL 添加非空参数到查询条件中
            if (!Objects.equals(mouth, "")) {
                queryBuilder.append("mouth = ? AND ");
            }
            if (!Objects.equals(crest, "")) {
                queryBuilder.append("crest = ? AND ");
            }
            if (!Objects.equals(neck, "")) {
                queryBuilder.append("neck = ? AND ");
            }
            if (!Objects.equals(back, "")) {
                queryBuilder.append("back = ? AND ");
            }
            if (!Objects.equals(leg, "")) {
                queryBuilder.append("leg = ? AND ");
            }

            // 删除最后一个 "AND" 和多余的空格
            queryBuilder.delete(queryBuilder.length() - 5, queryBuilder.length());

            queryBuilder.append("group by t1.name, t1.back, t1.crest, t1.leg, t1.mouth, t1.neck");
            // 最终的查询语句
            query = queryBuilder.toString();
        }

        // 连接数据库并执行查询
        try (Connection connection = DriverManager.getConnection(new com.keshihua.ksh.config.MysqlConfig().getUrl(), new com.keshihua.ksh.config.MysqlConfig().getUsername(), new com.keshihua.ksh.config.MysqlConfig().getPassword());
             PreparedStatement statement = connection.prepareStatement(query)) {

            int parameterIndex = 1;

            // 设置参数值
            if (!Objects.equals(mouth, "")) {
                statement.setString(parameterIndex++, mouth);
            }
            if (!Objects.equals(crest, "")) {
                statement.setString(parameterIndex++, crest);
            }
            if (!Objects.equals(neck, "")) {
                statement.setString(parameterIndex++, neck);
            }
            if (!Objects.equals(back, "")) {
                statement.setString(parameterIndex++, back);
            }
            if (!Objects.equals(leg, "")) {
                statement.setString(parameterIndex++, leg);
            }

            // 执行查询
            ResultSet resultSet = statement.executeQuery();

            // 处理查询结果
            while (resultSet.next()) {
                ArrayList<Object> part = new ArrayList<>();
                part.add(resultSet.getString("name"));
                part.add(resultSet.getString("picurl"));
                result.add(part);
            }
            return result;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        ArrayList<ArrayList<Object>> result = birdchar("","", "","", "");
        System.out.println(result);
    }


}
