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

public class TaskStatusServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final TaskDao taskDao = new TaskDaoImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireCsrf(request, response)) {
            return;
        }
        Long id = WebUtil.parseLong(request.getParameter("id"));
        Integer status = WebUtil.parseInteger(request.getParameter("status"));
        if (id == null || status == null || status < 0 || status > 2) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "任务 ID 或状态不合法");
            return;
        }
        User user = currentUser(request);
        try {
            if (!taskDao.updateStatus(id, user.getId(), status)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "任务不存在或无权修改");
                return;
            }
            String[] messages = {"任务已移回待办", "任务已开始进行", "任务已完成"};
            WebUtil.flash(request, "success", messages[status]);
            redirect(request, response, "/tasks");
        } catch (SQLException e) {
            throw new ServletException("更新任务状态失败", e);
        }
    }
}
