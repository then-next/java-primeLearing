package com.taskmaster.servlet;

import com.taskmaster.entity.User;
import com.taskmaster.service.TaskService;
import com.taskmaster.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TaskStatusServlet extends BaseServlet {
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
        Integer status = WebUtil.parseInteger(request.getParameter("status"));
        if (id == null || status == null || status < 0 || status > 2) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "任务 ID 或状态不合法");
            return;
        }
        User user = currentUser(request);
        if (!taskService.updateStatus(id, user.getId(), status)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "任务不存在或无权修改");
            return;
        }
        String[] messages = {"任务已移回待办", "任务已开始进行", "任务已完成"};
        WebUtil.flash(request, "success", messages[status]);
        redirect(request, response, "/tasks");
    }
}
