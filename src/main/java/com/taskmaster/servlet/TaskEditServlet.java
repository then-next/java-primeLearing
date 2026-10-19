package com.taskmaster.servlet;

import com.taskmaster.dao.TaskDao;
import com.taskmaster.dao.impl.TaskDaoImpl;
import com.taskmaster.entity.Task;
import com.taskmaster.entity.User;
import com.taskmaster.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class TaskEditServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final TaskDao taskDao = new TaskDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long id = WebUtil.parseLong(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "任务 ID 不合法");
            return;
        }
        User user = currentUser(request);
        try {
            Task task = taskDao.findById(id, user.getId());
            if (task == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "任务不存在");
                return;
            }
            request.setAttribute("task", task);
            request.setAttribute("formMode", "edit");
            request.getRequestDispatcher("/WEB-INF/views/edit.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("读取任务失败", e);
        }
    }
}
