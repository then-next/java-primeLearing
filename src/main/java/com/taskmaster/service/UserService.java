package com.taskmaster.service;

import com.taskmaster.entity.User;

public interface UserService {
    User authenticate(String username, String rawPassword);
}
