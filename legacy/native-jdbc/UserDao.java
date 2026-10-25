// 第一阶段原生 JDBC DAO 接口留档，不参与 MyBatis 编译。
package com.taskmaster.dao;

import com.taskmaster.entity.User;

import java.sql.SQLException;

public interface UserDao {
    User findByUsername(String username) throws SQLException;
}
