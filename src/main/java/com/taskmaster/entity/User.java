package com.taskmaster.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录用户实体。密码摘要与盐值只在 DAO/登录校验时使用，
 * 写入 Session 前会清空，避免把敏感字段带到页面。
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String displayName;
    private String passwordHash;
    private String salt;
    private LocalDateTime createdAt;

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getInitial() {
        if (displayName == null || displayName.isEmpty()) {
            return "T";
        }
        return displayName.substring(0, 1);
    }
}
