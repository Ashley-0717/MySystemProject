package com.micro.util;

import com.alibaba.druid.pool.DruidDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DruidUtil {
    private static DruidDataSource dataSource;

    static {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/micro_headline?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&characterEncoding=utf8");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setInitialSize(5);
        dataSource.setMaxActive(20);
        dataSource.setMinIdle(5);
        dataSource.setMaxWait(60000);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void close(AutoCloseable... closeables) {
        try {
            for (AutoCloseable c : closeables) {
                if (c != null) c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
