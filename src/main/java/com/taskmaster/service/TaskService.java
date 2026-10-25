package com.taskmaster.service;

import com.taskmaster.entity.Task;

import java.util.List;

public interface TaskService {
    List<Task> list(long userId, String keyword, Integer status);

    Task findById(long id, long userId);

    long create(Task task);

    boolean update(Task task);

    boolean delete(long id, long userId);

    boolean updateStatus(long id, long userId, int status);

    int count(long userId, Integer status);

    int countOverdue(long userId);
}
