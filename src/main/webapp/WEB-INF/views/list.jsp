<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.taskmaster.entity.Task" %>
<%@ page import="com.taskmaster.entity.User" %>
<%@ page import="com.taskmaster.util.WebUtil" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%
    List<Task> tasks = (List<Task>) request.getAttribute("tasks");
    User user = (User) request.getAttribute("currentUser");
    String contextPath = request.getContextPath();
    String keyword = (String) request.getAttribute("keyword");
    if (keyword == null) {
        keyword = "";
    }
    Integer statusFilter = (Integer) request.getAttribute("statusFilter");
    String flashMessage = (String) request.getAttribute("flashMessage");
    String flashType = (String) request.getAttribute("flashType");
    String csrfToken = WebUtil.csrfToken(request);
    int totalCount = request.getAttribute("totalCount") == null ? 0 : (Integer) request.getAttribute("totalCount");
    int todoCount = request.getAttribute("todoCount") == null ? 0 : (Integer) request.getAttribute("todoCount");
    int doingCount = request.getAttribute("doingCount") == null ? 0 : (Integer) request.getAttribute("doingCount");
    int doneCount = request.getAttribute("doneCount") == null ? 0 : (Integer) request.getAttribute("doneCount");
    int overdueCount = request.getAttribute("overdueCount") == null ? 0 : (Integer) request.getAttribute("overdueCount");
    DateTimeFormatter shortTime = DateTimeFormatter.ofPattern("MM-dd HH:mm");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TaskMaster · 任务台</title>
    <link rel="stylesheet" href="<%= contextPath %>/css/style.css">
</head>
<body>
<div class="shell">
    <header class="topbar app-topbar">
        <a class="brand-line" href="<%= contextPath %>/tasks">
            <span class="brand-mark">TM</span>
            <span>TaskMaster</span>
        </a>
        <div class="user-area">
            <span class="avatar"><%= WebUtil.escapeHtml(user.getInitial()) %></span>
            <div class="user-copy">
                <strong><%= WebUtil.escapeHtml(user.getDisplayName()) %></strong>
                <small>@<%= WebUtil.escapeHtml(user.getUsername()) %></small>
            </div>
            <form method="post" action="<%= contextPath %>/logout" class="inline-form">
                <input type="hidden" name="_csrf" value="<%= WebUtil.escapeHtml(csrfToken) %>">
                <button class="link-button" type="submit">退出</button>
            </form>
        </div>
    </header>

    <section class="hero-row">
        <div>
            <p class="eyebrow">YOUR WORK, IN FOCUS</p>
            <h1>今天，先完成<br>最重要的一件事。</h1>
            <p class="muted">任务按照“进行中 → 待办 → 已完成”的顺序排列，逾期项会单独标记。</p>
        </div>
        <a class="button" href="<%= contextPath %>/tasks/add">＋ 新建任务</a>
    </section>

    <section class="stat-row four">
        <div class="stat"><span><%= totalCount %></span><small>全部任务</small></div>
        <div class="stat"><span><%= todoCount %></span><small>待办</small></div>
        <div class="stat"><span><%= doingCount %></span><small>进行中</small></div>
        <div class="stat <%= overdueCount > 0 ? "stat-warn" : "" %>"><span><%= overdueCount %></span><small>已逾期</small></div>
    </section>

    <section class="panel">
        <div class="panel-heading list-heading">
            <div>
                <p class="eyebrow">TASK BOARD</p>
                <h2>任务清单</h2>
                <p class="muted heading-note"><%= doneCount %> 项已完成，继续保持节奏。</p>
            </div>
            <form method="get" action="<%= contextPath %>/tasks" class="filter-form">
                <input type="search" name="keyword" maxlength="100"
                       value="<%= WebUtil.escapeHtml(keyword) %>" placeholder="搜索标题或描述">
                <select name="status" aria-label="按状态筛选">
                    <option value="">全部状态</option>
                    <option value="0" <%= statusFilter != null && statusFilter == 0 ? "selected" : "" %>>待办</option>
                    <option value="1" <%= statusFilter != null && statusFilter == 1 ? "selected" : "" %>>进行中</option>
                    <option value="2" <%= statusFilter != null && statusFilter == 2 ? "selected" : "" %>>已完成</option>
                </select>
                <button class="button button-small" type="submit">筛选</button>
                <% if (!keyword.isEmpty() || statusFilter != null) { %>
                    <a class="clear-link" href="<%= contextPath %>/tasks">清除</a>
                <% } %>
            </form>
        </div>

        <% if (flashMessage != null) { %>
            <div class="alert alert-<%= "error".equals(flashType) ? "error" : "success" %>">
                <%= WebUtil.escapeHtml(flashMessage) %>
            </div>
        <% } %>

        <% if (tasks == null || tasks.isEmpty()) { %>
            <div class="empty">
                <span class="empty-mark">○</span>
                <strong><%= keyword.isEmpty() && statusFilter == null ? "还没有任务。" : "没有匹配的任务。" %></strong>
                <p><%= keyword.isEmpty() && statusFilter == null ? "从一件具体的小事开始，把空白变成进度。" : "换一个关键词或状态再试一次。" %></p>
                <% if (keyword.isEmpty() && statusFilter == null) { %>
                    <a class="button button-ghost" href="<%= contextPath %>/tasks/add">创建第一项任务</a>
                <% } %>
            </div>
        <% } else { %>
            <div class="task-list">
                <% for (Task task : tasks) { %>
                    <article class="task-item task-status-<%= task.getStatus() %>">
                        <div class="task-check status-<%= task.getStatus() %>" aria-hidden="true">
                            <%= task.getStatus() == 2 ? "✓" : "" %>
                        </div>
                        <div class="task-main">
                            <div class="task-title-row">
                                <h3><%= WebUtil.escapeHtml(task.getTitle()) %></h3>
                                <span class="pill priority-<%= task.getPriority() %>">优先级 <%= task.getPriorityText() %></span>
                                <% if (task.isOverdue()) { %><span class="pill overdue-pill">逾期</span><% } %>
                            </div>
                            <p class="task-description"><%= task.getDescription() == null ? "暂无描述" : WebUtil.escapeHtml(task.getDescription()) %></p>
                            <div class="task-meta">
                                <span class="status-text"><%= task.getStatusText() %></span>
                                <span><%= task.getDueDate() == null ? "无截止日期" : "截止 " + task.getDueDate() %></span>
                                <span><%= task.getCreatedAt() == null ? "" : "创建于 " + task.getCreatedAt().format(shortTime) %></span>
                            </div>
                        </div>
                        <div class="task-actions">
                            <% if (task.getStatus() == 0) { %>
                                <form method="post" action="<%= contextPath %>/tasks/status" class="inline-form">
                                    <input type="hidden" name="_csrf" value="<%= WebUtil.escapeHtml(csrfToken) %>">
                                    <input type="hidden" name="id" value="<%= task.getId() %>">
                                    <input type="hidden" name="status" value="1">
                                    <button class="button button-small button-ghost" type="submit">开始</button>
                                </form>
                            <% } %>
                            <% if (task.getStatus() != 2) { %>
                                <form method="post" action="<%= contextPath %>/tasks/status" class="inline-form">
                                    <input type="hidden" name="_csrf" value="<%= WebUtil.escapeHtml(csrfToken) %>">
                                    <input type="hidden" name="id" value="<%= task.getId() %>">
                                    <input type="hidden" name="status" value="2">
                                    <button class="button button-small" type="submit">完成</button>
                                </form>
                            <% } else { %>
                                <form method="post" action="<%= contextPath %>/tasks/status" class="inline-form">
                                    <input type="hidden" name="_csrf" value="<%= WebUtil.escapeHtml(csrfToken) %>">
                                    <input type="hidden" name="id" value="<%= task.getId() %>">
                                    <input type="hidden" name="status" value="0">
                                    <button class="button button-small button-ghost" type="submit">重开</button>
                                </form>
                            <% } %>
                            <a class="button button-small button-ghost" href="<%= contextPath %>/tasks/edit?id=<%= task.getId() %>">编辑</a>
                            <form method="post" action="<%= contextPath %>/tasks/delete" class="inline-form"
                                  onsubmit="return confirm('确定删除这项任务吗？该操作不可撤销。');">
                                <input type="hidden" name="_csrf" value="<%= WebUtil.escapeHtml(csrfToken) %>">
                                <input type="hidden" name="id" value="<%= task.getId() %>">
                                <button class="button button-small button-danger" type="submit">删除</button>
                            </form>
                        </div>
                    </article>
                <% } %>
            </div>
        <% } %>
    </section>

    <footer class="footer">Servlet + JSP + JDBC · 所有写操作均使用 POST + PRG · 数据表 <code>taskmaster.t_task</code></footer>
</div>
</body>
</html>
