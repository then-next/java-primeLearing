package com.taskmaster.service.impl;

import com.taskmaster.dao.TaskDao;
import com.taskmaster.entity.Task;
import com.taskmaster.service.ServiceException;
import com.taskmaster.service.TaskService;

import java.sql.SQLException;
import java.util.List;

/**
 * 构造器注入示例：final 字段在构造时获得 TaskDao，对象创建后依赖不可替换。
 * 该实现由 applicationContext.xml 创建，而不是由 Servlet new 出来。
 */
public class TaskServiceImpl implements TaskService {
    private final TaskDao taskDao;

    public TaskServiceImpl(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    public List<Task> list(long userId, String keyword, Integer status) {
        try {
            return taskDao.list(userId, keyword, status);
        } catch (SQLException e) {
            throw new ServiceException("查询任务列表失败", e);
        }
    }

    @Override
    public Task findById(long id, long userId) {
        try {
            return taskDao.findById(id, userId);
        } catch (SQLException e) {
            throw new ServiceException("查询任务失败", e);
        }
    }

    @Override
    public long create(Task task) {
        try {
            return taskDao.insert(task);
        } catch (SQLException e) {
            throw new ServiceException("创建任务失败", e);
        }
    }

    @Override
    public boolean update(Task task) {
        try {
            return taskDao.update(task);
        } catch (SQLException e) {
            throw new ServiceException("更新任务失败", e);
        }
    }

    @Override
    public boolean delete(long id, long userId) {
        try {
            return taskDao.delete(id, userId);
        } catch (SQLException e) {
            throw new ServiceException("删除任务失败", e);
        }
    }

    @Override
    public boolean updateStatus(long id, long userId, int status) {
        try {
            return taskDao.updateStatus(id, userId, status);
        } catch (SQLException e) {
            throw new ServiceException("更新任务状态失败", e);
        }
    }

    @Override
    public int count(long userId, Integer status) {
        try {
            return taskDao.count(userId, status);
        } catch (SQLException e) {
            throw new ServiceException("统计任务失败", e);
        }
    }

    @Override
    public int countOverdue(long userId) {
        try {
            return taskDao.countOverdue(userId);
        } catch (SQLException e) {
            throw new ServiceException("统计逾期任务失败", e);
        }
    }
}
