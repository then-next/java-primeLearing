// 第一阶段原生 JDBC DAO 接口留档，不参与 MyBatis 编译。
package com.taskmaster.dao;

import com.taskmaster.entity.Task;

import java.sql.SQLException;
import java.util.List;

/**
 * 任务持久化接口。Servlet 只依赖该接口，不关心 SQL 与 JDBC 细节。
 */
public interface TaskDao {
    List<Task> list(long userId, String keyword, Integer status) throws SQLException;

    Task findById(long id, long userId) throws SQLException;

    long insert(Task task) throws SQLException;

    boolean update(Task task) throws SQLException;

    boolean delete(long id, long userId) throws SQLException;

    boolean updateStatus(long id, long userId, int status) throws SQLException;

    int count(long userId, Integer status) throws SQLException;

    int countOverdue(long userId) throws SQLException;
}
