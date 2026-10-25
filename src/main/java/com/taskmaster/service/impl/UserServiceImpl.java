package com.taskmaster.service.impl;

import com.taskmaster.entity.User;
import com.taskmaster.mapper.UserMapper;
import com.taskmaster.service.UserService;
import com.taskmaster.util.PasswordUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 * Setter 注入示例：applicationContext.xml 通过 <property name="userMapper"> 注入依赖。
 * 与 TaskServiceImpl 的构造器注入相比，Setter 更适合可选/可替换依赖，但对象创建后
 * 依赖不是 final，必须保证容器完成注入后才能使用。
 */
public class UserServiceImpl implements UserService {
    private UserMapper userMapper;

    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public User authenticate(String username, String rawPassword) {
        User user = userMapper.selectByUsername(username);
        if (user == null || !PasswordUtil.verify(rawPassword, user.getSalt(), user.getPasswordHash())) {
            return null;
        }
        user.setPasswordHash(null);
        user.setSalt(null);
        return user;
    }
}
