-- 仅用于本次机器上已存在旧版 t_task 的情况。
-- 新环境直接执行 schema.sql 即可，不需要运行本文件。
-- 该迁移只新增列与索引，不删除原有任务记录。

USE taskmaster;

ALTER TABLE t_task
    ADD COLUMN user_id BIGINT NULL AFTER id,
    ADD COLUMN description VARCHAR(1000) NULL AFTER title,
    ADD COLUMN created_at DATETIME NULL AFTER due_date,
    ADD COLUMN updated_at DATETIME NULL AFTER created_at;

UPDATE t_task
SET user_id = 1
WHERE user_id IS NULL;

UPDATE t_task
SET created_at = COALESCE(TIMESTAMP(create_date), CURRENT_TIMESTAMP),
    updated_at = COALESCE(TIMESTAMP(create_date), CURRENT_TIMESTAMP);

ALTER TABLE t_task
    MODIFY COLUMN user_id BIGINT NOT NULL,
    ADD INDEX idx_t_task_user_status (user_id, status),
    ADD INDEX idx_t_task_due_date (due_date),
    ADD CONSTRAINT fk_t_task_user FOREIGN KEY (user_id) REFERENCES t_user (id);
