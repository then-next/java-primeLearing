package com.taskmaster.servlet;

import com.taskmaster.dao.TaskDao;
import com.taskmaster.dao.impl.TaskDaoImpl;
import com.taskmaster.entity.Task;
import com.taskmaster.entity.User;
import com.taskmaster.util.TaskFormUtil;
import com.taskmaster.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class TaskAddServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final TaskDao taskDao = new TaskDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Task task = new Task();
        task.setStatus(0);
        task.setPriority(2);
        request.setAttribute("task", task);
        request.setAttribute("formMode", "add");
        request.getRequestDispatcher("/WEB-INF/views/add.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireCsrf(request, response)) {
            return;
        }
        User user = currentUser(request);
        Task task = new Task();
        task.setUserId(user.getId());
        Map<String, String> errors = TaskFormUtil.bind(request, task);

        if (!errors.isEmpty()) {
            request.setAttribute("task", task);
            request.setAttribute("errors", errors);
            request.setAttribute("formMode", "add");
            request.getRequestDispatcher("/WEB-INF/views/add.jsp").forward(request, response);
            return;
        }

        try {
            taskDao.insert(task);
            WebUtil.flash(request, "success", "任务“" + task.getTitle() + "”已创建");
            redirect(request, response, "/tasks");
        } catch (SQLException e) {
            throw new ServletException("新增任务失败", e);
        }
    }
}
