// 第一阶段原生 javax Servlet 留档，不参与 SSM 编译。
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

public class TaskUpdateServlet extends BaseServlet {
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
        User user = currentUser(request);
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "任务 ID 不合法");
            return;
        }

        Task task = taskService.findById(id, user.getId());
        if (task == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "任务不存在");
            return;
        }
        Map<String, String> errors = TaskFormUtil.bind(request, task);
        if (!errors.isEmpty()) {
            request.setAttribute("task", task);
            request.setAttribute("errors", errors);
            request.setAttribute("formMode", "edit");
            request.getRequestDispatcher("/WEB-INF/views/edit.jsp").forward(request, response);
            return;
        }
        if (!taskService.update(task)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "任务不存在或无权修改");
            return;
        }
        WebUtil.flash(request, "success", "任务“" + task.getTitle() + "”已更新");
        redirect(request, response, "/tasks");
    }
}
