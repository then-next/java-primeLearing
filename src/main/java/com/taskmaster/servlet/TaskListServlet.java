package com.taskmaster.servlet;

import com.taskmaster.entity.Task;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 阶段一：用最直接的方式跑通“浏览器 -> Servlet -> MySQL -> JSP”。
 * 这段代码故意保留了重复连接、SQL/映射耦合等原始问题，
 * 阶段二会通过 DAO、DBUtil、Filter 重构。
 */
public class TaskListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 阶段一痛点：连接参数散落在 Servlet 中，环境变化就要改源码。
        String url = System.getenv().getOrDefault(
                "TASKMASTER_DB_URL",
                "jdbc:mysql://127.0.0.1:3306/taskmaster?useUnicode=true&characterEncoding=UTF-8"
                        + "&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true");
        String username = System.getenv().getOrDefault("TASKMASTER_DB_USER", "root");
        String password = System.getenv().getOrDefault("TASKMASTER_DB_PASSWORD", "");

        List<Task> tasks = new ArrayList<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 Statement statement = connection.createStatement();
                 ResultSet rs = statement.executeQuery(
                         "SELECT id, user_id, title, description, status, priority, due_date, "
                                 + "created_at, updated_at FROM t_task WHERE user_id = 1 "
                                 + "ORDER BY status ASC, priority ASC, due_date IS NULL, due_date ASC, id DESC")) {
                while (rs.next()) {
                    Task task = new Task();
                    task.setId(rs.getLong("id"));
                    task.setUserId(rs.getLong("user_id"));
                    task.setTitle(rs.getString("title"));
                    task.setDescription(rs.getString("description"));
                    task.setStatus(rs.getInt("status"));
                    task.setPriority(rs.getInt("priority"));
                    java.sql.Date dueDate = rs.getDate("due_date");
                    task.setDueDate(dueDate == null ? null : dueDate.toLocalDate());
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    Timestamp updatedAt = rs.getTimestamp("updated_at");
                    task.setCreatedAt(createdAt == null ? null : createdAt.toLocalDateTime());
                    task.setUpdatedAt(updatedAt == null ? null : updatedAt.toLocalDateTime());
                    tasks.add(task);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new ServletException("任务列表查询失败", e);
        }

        request.setAttribute("tasks", tasks);
        request.setAttribute("userName", "任务管理员");
        request.getRequestDispatcher("/WEB-INF/views/list.jsp").forward(request, response);
    }
}
