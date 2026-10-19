CREATE DATABASE IF NOT EXISTS taskmaster
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE taskmaster;

CREATE TABLE IF NOT EXISTS t_user (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    username     VARCHAR(50)  NOT NULL,
    display_name VARCHAR(50)  NOT NULL,
    password_hash CHAR(64)    NOT NULL,
    salt         VARCHAR(64)  NOT NULL,
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_t_user_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS t_task (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    user_id     BIGINT       NOT NULL,
    title       VARCHAR(100) NOT NULL,
    description VARCHAR(1000) NULL,
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '0待办 1进行中 2已完成',
    priority    TINYINT      NOT NULL DEFAULT 2 COMMENT '1高 2中 3低',
    due_date    DATE         NULL,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_t_task_user_status (user_id, status),
    KEY idx_t_task_due_date (due_date),
    CONSTRAINT fk_t_task_user FOREIGN KEY (user_id) REFERENCES t_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 演示账号：admin / admin123
-- 口令摘要 = SHA-256("admin123:tm-2026-demo")，仅用于教学演示。
INSERT INTO t_user (id, username, display_name, password_hash, salt)
VALUES (1, 'admin', '任务管理员',
        'be122d4fcee440a96e4f73a65f796670ade7b208b957ef759c4537ecd601e2ec',
        'tm-2026-demo')
ON DUPLICATE KEY UPDATE display_name = VALUES(display_name);

INSERT INTO t_task (user_id, title, description, status, priority, due_date)
SELECT 1, '熟悉 Servlet 生命周期', '观察 init、service、destroy 的调用时机。', 1, 1, DATE_ADD(CURDATE(), INTERVAL 2 DAY)
WHERE NOT EXISTS (SELECT 1 FROM t_task WHERE user_id = 1 AND title = '熟悉 Servlet 生命周期');

INSERT INTO t_task (user_id, title, description, status, priority, due_date)
SELECT 1, '跑通浏览器到 MySQL 的通路', '检查请求参数、JDBC 查询与 JSP 展示。', 2, 2, CURDATE()
WHERE NOT EXISTS (SELECT 1 FROM t_task WHERE user_id = 1 AND title = '跑通浏览器到 MySQL 的通路');

INSERT INTO t_task (user_id, title, description, status, priority, due_date)
SELECT 1, '整理原生开发痛点', '按重复、耦合、配置三类归档。', 0, 3, DATE_ADD(CURDATE(), INTERVAL 7 DAY)
WHERE NOT EXISTS (SELECT 1 FROM t_task WHERE user_id = 1 AND title = '整理原生开发痛点');
