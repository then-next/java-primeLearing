package com.taskmaster.servlet;

import com.taskmaster.dao.TaskDao;
import com.taskmaster.dao.impl.TaskDaoImpl;
import com.taskmaster.entity.User;
import com.taskmaster.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class TaskDeleteServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final TaskDao taskDao = new TaskDaoImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireCsrf(request, response)) {
            return;
        }
        Long id = WebUtil.parseLong(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "任务 ID 不合法");
            return;
        }
        User user = currentUser(request);
        try {
            if (!taskDao.delete(id, user.getId())) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "任务不存在或无权删除");
                return;
            }
            WebUtil.flash(request, "success", "任务已删除");
            redirect(request, response, "/tasks");
        } catch (SQLException e) {
            throw new ServletException("删除任务失败", e);
        }
    }
}
