package com.taskmaster.mapper;

import com.taskmaster.entity.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    User selectByUsername(@Param("username") String username);
}
