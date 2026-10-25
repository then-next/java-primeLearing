package com.taskmaster.servlet;

import com.taskmaster.entity.User;
import com.taskmaster.service.TaskService;
import com.taskmaster.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TaskDeleteServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private TaskService taskService;

    @Override
    public void init() throws ServletException {
        taskService = springBean("taskService", TaskService.class);
    }

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
        if (!taskService.delete(id, user.getId())) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "任务不存在或无权删除");
            return;
        }
        WebUtil.flash(request, "success", "任务已删除");
        redirect(request, response, "/tasks");
    }
}
