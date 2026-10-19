package com.taskmaster.dao;

import com.taskmaster.entity.User;

import java.sql.SQLException;

public interface UserDao {
    User findByUsername(String username) throws SQLException;
}
