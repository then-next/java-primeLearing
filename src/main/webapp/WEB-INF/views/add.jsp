<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.taskmaster.entity.Task" %>
<%@ page import="com.taskmaster.util.WebUtil" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.Map" %>
<%
    Task task = (Task) request.getAttribute("task");
    Map<String, String> errors = (Map<String, String>) request.getAttribute("errors");
    if (errors == null) {
        errors = Collections.emptyMap();
    }
    String contextPath = request.getContextPath();
    String csrfToken = WebUtil.csrfToken(request);
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>新建任务 · TaskMaster</title>
    <link rel="stylesheet" href="<%= contextPath %>/css/style.css">
</head>
<body class="form-page">
<div class="shell narrow">
    <a class="back-link" href="<%= contextPath %>/tasks">← 返回任务台</a>
    <header class="form-header">
        <p class="eyebrow">NEW TASK</p>
        <h1>新建一项任务。</h1>
        <p class="muted">把想法写成一个可执行的动作。保存后使用重定向回到列表，刷新不会重复创建。</p>
    </header>
    <section class="panel">
        <form method="post" action="<%= contextPath %>/tasks/add" class="task-form">
            <input type="hidden" name="_csrf" value="<%= WebUtil.escapeHtml(csrfToken) %>">
            <%@ include file="_task-form-fields.jspf" %>
            <div class="form-actions">
                <a class="button button-ghost" href="<%= contextPath %>/tasks">取消</a>
                <button class="button" type="submit">创建任务</button>
            </div>
        </form>
    </section>
</div>
</body>
</html>
