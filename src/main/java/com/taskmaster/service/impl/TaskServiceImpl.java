package com.taskmaster.service.impl;

import com.taskmaster.entity.Task;
import com.taskmaster.mapper.TaskMapper;
import com.taskmaster.service.TaskService;

import java.util.List;

/**
 * 构造器注入示例：final 字段在构造时获得 MyBatis Mapper 代理。
 * 该实现由 applicationContext.xml 创建，而不是由 Controller new 出来。
 */
public class TaskServiceImpl implements TaskService {
    private final TaskMapper taskMapper;

    public TaskServiceImpl(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    @Override
    public List<Task> list(long userId, String keyword, Integer status) {
        return taskMapper.selectByUser(userId, keyword, status);
    }

    @Override
    public Task findById(long id, long userId) {
        return taskMapper.selectById(id, userId);
    }

    @Override
    public long create(Task task) {
        taskMapper.insert(task);
        return task.getId() == null ? 0 : task.getId();
    }

    @Override
    public boolean update(Task task) {
        return taskMapper.update(task) == 1;
    }

    @Override
    public boolean delete(long id, long userId) {
        return taskMapper.delete(id, userId) == 1;
    }

    @Override
    public boolean updateStatus(long id, long userId, int status) {
        return taskMapper.updateStatus(id, userId, status) == 1;
    }

    @Override
    public int count(long userId, Integer status) {
        return taskMapper.countByUser(userId, status);
    }

    @Override
    public int countOverdue(long userId) {
        return taskMapper.countOverdue(userId);
    }
}
