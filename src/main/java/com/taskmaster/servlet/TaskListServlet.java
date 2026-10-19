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
import java.util.List;

/**
 * 任务列表控制器：只负责解析查询条件、调用 DAO、准备视图数据。
 * JDBC 连接、SQL 与 ResultSet 映射已经下沉到 DAO。
 */
public class TaskListServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final TaskDao taskDao = new TaskDaoImpl();

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

        try {
            List<Task> tasks = taskDao.list(user.getId(), keyword, status);
            request.setAttribute("tasks", tasks);
            request.setAttribute("totalCount", taskDao.count(user.getId(), null));
            request.setAttribute("todoCount", taskDao.count(user.getId(), 0));
            request.setAttribute("doingCount", taskDao.count(user.getId(), 1));
            request.setAttribute("doneCount", taskDao.count(user.getId(), 2));
            request.setAttribute("overdueCount", taskDao.countOverdue(user.getId()));
        } catch (SQLException e) {
            throw new ServletException("任务列表查询失败", e);
        }

        request.setAttribute("keyword", keyword == null ? "" : keyword);
        request.setAttribute("statusFilter", status);
        WebUtil.moveFlashToRequest(request);
        request.getRequestDispatcher("/WEB-INF/views/list.jsp").forward(request, response);
    }
}
