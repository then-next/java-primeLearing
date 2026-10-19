package com.taskmaster.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * JDBC 连接工具。连接参数优先级：环境变量 > classpath:db.properties > 本地默认值。
 * 密码不写进仓库；部署时复制 db.properties.example 并填写，或通过环境变量注入。
 */
public final class DBUtil {
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                PROPERTIES.load(new InputStreamReader(input, StandardCharsets.UTF_8));
            }
            Class.forName(read("TASKMASTER_DB_DRIVER", "jdbc.driver", "com.mysql.cj.jdbc.Driver"));
        } catch (IOException | ClassNotFoundException e) {
            throw new ExceptionInInitializerError("数据库驱动或配置初始化失败: " + e.getMessage());
        }
    }

    private DBUtil() {
    }

    public static Connection getConnection() throws SQLException {
        String url = read("TASKMASTER_DB_URL", "jdbc.url",
                "jdbc:mysql://127.0.0.1:3306/taskmaster?useUnicode=true&characterEncoding=UTF-8"
                        + "&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true");
        String user = read("TASKMASTER_DB_USER", "jdbc.username", "taskmaster_app");
        String password = read("TASKMASTER_DB_PASSWORD", "jdbc.password", "");
        if (password.isEmpty()) {
            throw new SQLException("数据库密码未配置：请设置 TASKMASTER_DB_PASSWORD 或 db.properties");
        }
        return DriverManager.getConnection(url, user, password);
    }

    public static void closeQuietly(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception ignored) {
                    // 关闭失败不覆盖原始业务异常。
                }
            }
        }
    }

    private static String read(String envName, String propertyName, String defaultValue) {
        String env = System.getenv(envName);
        if (env != null && !env.trim().isEmpty()) {
            return env.trim();
        }
        String property = PROPERTIES.getProperty(propertyName);
        return property == null ? defaultValue : property.trim();
    }
}
