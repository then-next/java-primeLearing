<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.taskmaster.entity.Task" %>
<%
    List<Task> tasks = (List<Task>) request.getAttribute("tasks");
    String userName = (String) request.getAttribute("userName");
    if (userName == null) {
        userName = "任务管理员";
    }
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TaskMaster · 任务列表</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="shell">
    <header class="topbar">
        <div>
            <p class="eyebrow">TASKMASTER / STAGE 1</p>
            <h1>把待办放回它该在的位置。</h1>
            <p class="muted">你好，<%= userName %>。这里记录任务，也记录原生技术栈的摩擦点。</p>
        </div>
        <a class="button button-ghost" href="${pageContext.request.contextPath}/tasks">刷新列表</a>
    </header>

    <section class="stat-row">
        <div class="stat"><span><%= tasks == null ? 0 : tasks.size() %></span><small>当前任务</small></div>
        <div class="stat"><span>4</span><small>核心操作</small></div>
        <div class="stat"><span>1</span><small>条数据通路</small></div>
    </section>

    <section class="panel">
        <div class="panel-heading">
            <div>
                <p class="eyebrow">YOUR BOARD</p>
                <h2>全部任务</h2>
            </div>
        </div>

        <% if (tasks == null || tasks.isEmpty()) { %>
            <div class="empty">
                <strong>还没有任务。</strong>
                <p>下一步将从“新增任务”开始，观察 POST 与重定向。</p>
            </div>
        <% } else { %>
            <div class="task-list">
                <% for (Task task : tasks) { %>
                    <article class="task-item">
                        <div class="task-main">
                            <div class="task-title-row">
                                <span class="status-dot status-<%= task.getStatus() %>"></span>
                                <h3><%= task.getTitle() %></h3>
                                <span class="pill priority-<%= task.getPriority() %>">优先级 <%= task.getPriorityText() %></span>
                            </div>
                            <p><%= task.getDescription() == null ? "暂无描述" : task.getDescription() %></p>
                        </div>
                        <div class="task-meta">
                            <span><%= task.getStatusText() %></span>
                            <span><%= task.getDueDate() == null ? "无截止日期" : task.getDueDate().toString() %></span>
                            <% if (task.isOverdue()) { %><span class="danger">已逾期</span><% } %>
                        </div>
                    </article>
                <% } %>
            </div>
        <% } %>
    </section>

    <footer class="footer">Servlet + JSP + JDBC · 数据来自 MySQL <code>taskmaster.t_task</code></footer>
</div>
</body>
</html>
