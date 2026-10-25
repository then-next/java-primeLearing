package com.taskmaster.servlet;

import com.taskmaster.entity.Task;
import com.taskmaster.entity.User;
import com.taskmaster.service.TaskService;
import com.taskmaster.util.TaskFormUtil;
import com.taskmaster.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class TaskAddServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private TaskService taskService;

    @Override
    public void init() throws ServletException {
        taskService = springBean("taskService", TaskService.class);
    }

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

        taskService.create(task);
        WebUtil.flash(request, "success", "任务“" + task.getTitle() + "”已创建");
        redirect(request, response, "/tasks");
    }
}
