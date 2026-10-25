package com.taskmaster.servlet;

import com.taskmaster.entity.Task;
import com.taskmaster.entity.User;
import com.taskmaster.service.TaskService;
import com.taskmaster.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TaskEditServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private TaskService taskService;

    @Override
    public void init() throws ServletException {
        taskService = springBean("taskService", TaskService.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long id = WebUtil.parseLong(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "任务 ID 不合法");
            return;
        }
        User user = currentUser(request);
        Task task = taskService.findById(id, user.getId());
        if (task == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "任务不存在");
            return;
        }
        request.setAttribute("task", task);
        request.setAttribute("formMode", "edit");
        request.getRequestDispatcher("/WEB-INF/views/edit.jsp").forward(request, response);
    }
}
