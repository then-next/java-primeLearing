package com.taskmaster.servlet;

import com.taskmaster.entity.Task;
import com.taskmaster.entity.User;
import com.taskmaster.service.TaskService;
import com.taskmaster.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 任务列表控制器：只负责解析查询条件、调用 DAO、准备视图数据。
 * JDBC 连接、SQL 与 ResultSet 映射已经下沉到 DAO。
 */
public class TaskListServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private TaskService taskService;

    @Override
    public void init() throws ServletException {
        taskService = springBean("taskService", TaskService.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = currentUser(request);
        String keyword = WebUtil.trimToNull(request.getParameter("keyword"));
        if (keyword != null && keyword.length() > 100) {
            keyword = keyword.substring(0, 100);
        }
        String rawStatus = request.getParameter("status");
        Integer status = null;
        if ("0".equals(rawStatus) || "1".equals(rawStatus) || "2".equals(rawStatus)) {
            status = Integer.valueOf(rawStatus);
        }

        List<Task> tasks = taskService.list(user.getId(), keyword, status);
        request.setAttribute("tasks", tasks);
        request.setAttribute("totalCount", taskService.count(user.getId(), null));
        request.setAttribute("todoCount", taskService.count(user.getId(), 0));
        request.setAttribute("doingCount", taskService.count(user.getId(), 1));
        request.setAttribute("doneCount", taskService.count(user.getId(), 2));
        request.setAttribute("overdueCount", taskService.countOverdue(user.getId()));

        request.setAttribute("keyword", keyword == null ? "" : keyword);
        request.setAttribute("statusFilter", status);
        WebUtil.moveFlashToRequest(request);
        request.getRequestDispatcher("/WEB-INF/views/list.jsp").forward(request, response);
    }
}
