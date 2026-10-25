// 第一阶段原生 JDBC DAO 实现留档，不参与 MyBatis 编译。
package com.taskmaster.dao.impl;

import com.taskmaster.dao.TaskDao;
import com.taskmaster.entity.Task;
import com.taskmaster.util.DBUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TaskDaoImpl implements TaskDao {
    private static final String COLUMNS =
            "id, user_id, title, description, status, priority, due_date, created_at, updated_at";

    @Override
    public List<Task> list(long userId, String keyword, Integer status) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT " + COLUMNS + " FROM t_task WHERE user_id = ?");
        List<Object> parameters = new ArrayList<>();
        parameters.add(userId);

        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (title LIKE ? OR description LIKE ?)");
            String like = "%" + keyword + "%";
            parameters.add(like);
            parameters.add(like);
        }
        if (status != null) {
            sql.append(" AND status = ?");
            parameters.add(status);
        }
        sql.append(" ORDER BY CASE status WHEN 1 THEN 0 WHEN 0 THEN 1 ELSE 2 END,")
                .append(" priority ASC, due_date IS NULL, due_date ASC, id DESC");

        List<Task> tasks = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            bind(statement, parameters);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    tasks.add(map(resultSet));
                }
            }
        }
        return tasks;
    }

    @Override
    public Task findById(long id, long userId) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM t_task WHERE id = ? AND user_id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.setLong(2, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? map(resultSet) : null;
            }
        }
    }

    @Override
    public long insert(Task task) throws SQLException {
        String sql = "INSERT INTO t_task (user_id, title, description, status, priority, due_date) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, task.getUserId());
            statement.setString(2, task.getTitle());
            statement.setString(3, task.getDescription());
            statement.setInt(4, task.getStatus());
            statement.setInt(5, task.getPriority());
            setDate(statement, 6, task.getDueDate());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    task.setId(keys.getLong(1));
                }
            }
        }
        return task.getId() == null ? 0 : task.getId();
    }

    @Override
    public boolean update(Task task) throws SQLException {
        String sql = "UPDATE t_task SET title = ?, description = ?, status = ?, priority = ?, "
                + "due_date = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ? AND user_id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, task.getTitle());
            statement.setString(2, task.getDescription());
            statement.setInt(3, task.getStatus());
            statement.setInt(4, task.getPriority());
            setDate(statement, 5, task.getDueDate());
            statement.setLong(6, task.getId());
            statement.setLong(7, task.getUserId());
            return statement.executeUpdate() == 1;
        }
    }

    @Override
    public boolean delete(long id, long userId) throws SQLException {
        String sql = "DELETE FROM t_task WHERE id = ? AND user_id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.setLong(2, userId);
            return statement.executeUpdate() == 1;
        }
    }

    @Override
    public boolean updateStatus(long id, long userId, int status) throws SQLException {
        String sql = "UPDATE t_task SET status = ?, updated_at = CURRENT_TIMESTAMP "
                + "WHERE id = ? AND user_id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, status);
            statement.setLong(2, id);
            statement.setLong(3, userId);
            return statement.executeUpdate() == 1;
        }
    }

    @Override
    public int count(long userId, Integer status) throws SQLException {
        String sql = status == null
                ? "SELECT COUNT(*) FROM t_task WHERE user_id = ?"
                : "SELECT COUNT(*) FROM t_task WHERE user_id = ? AND status = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            if (status != null) {
                statement.setInt(2, status);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(1) : 0;
            }
        }
    }

    @Override
    public int countOverdue(long userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM t_task WHERE user_id = ? AND status <> 2 "
                + "AND due_date IS NOT NULL AND due_date < CURDATE()";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(1) : 0;
            }
        }
    }

    private static Task map(ResultSet resultSet) throws SQLException {
        Task task = new Task();
        task.setId(resultSet.getLong("id"));
        task.setUserId(resultSet.getLong("user_id"));
        task.setTitle(resultSet.getString("title"));
        task.setDescription(resultSet.getString("description"));
        task.setStatus(resultSet.getInt("status"));
        task.setPriority(resultSet.getInt("priority"));
        Date dueDate = resultSet.getDate("due_date");
        task.setDueDate(dueDate == null ? null : dueDate.toLocalDate());
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        task.setCreatedAt(createdAt == null ? null : createdAt.toLocalDateTime());
        task.setUpdatedAt(updatedAt == null ? null : updatedAt.toLocalDateTime());
        return task;
    }

    private static void bind(PreparedStatement statement, List<Object> parameters) throws SQLException {
        for (int i = 0; i < parameters.size(); i++) {
            statement.setObject(i + 1, parameters.get(i));
        }
    }

    private static void setDate(PreparedStatement statement, int index, java.time.LocalDate dueDate)
            throws SQLException {
        if (dueDate == null) {
            statement.setNull(index, java.sql.Types.DATE);
        } else {
            statement.setDate(index, Date.valueOf(dueDate));
        }
    }
}
