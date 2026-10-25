// 第一阶段原生 JDBC DAO 实现留档，不参与 MyBatis 编译。
package com.taskmaster.dao.impl;

import com.taskmaster.dao.UserDao;
import com.taskmaster.entity.User;
import com.taskmaster.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class UserDaoImpl implements UserDao {
    @Override
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, display_name, password_hash, salt, created_at "
                + "FROM t_user WHERE username = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setUsername(resultSet.getString("username"));
                user.setDisplayName(resultSet.getString("display_name"));
                user.setPasswordHash(resultSet.getString("password_hash"));
                user.setSalt(resultSet.getString("salt"));
                Timestamp createdAt = resultSet.getTimestamp("created_at");
                user.setCreatedAt(createdAt == null ? null : createdAt.toLocalDateTime());
                return user;
            }
        }
    }
}
