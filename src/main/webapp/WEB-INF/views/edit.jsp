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
    <title>编辑任务 · TaskMaster</title>
    <link rel="stylesheet" href="<%= contextPath %>/css/style.css">
</head>
<body class="form-page">
<div class="shell narrow">
    <a class="back-link" href="<%= contextPath %>/tasks">← 返回任务台</a>
    <header class="form-header">
        <p class="eyebrow">EDIT TASK</p>
        <h1>调整任务信息。</h1>
        <p class="muted">修改后同样使用 POST + 重定向，避免浏览器刷新导致重复写入。</p>
    </header>
    <section class="panel">
        <form method="post" action="<%= contextPath %>/tasks/update" class="task-form">
            <input type="hidden" name="_csrf" value="<%= WebUtil.escapeHtml(csrfToken) %>">
            <input type="hidden" name="id" value="<%= task.getId() %>">
            <%@ include file="_task-form-fields.jspf" %>
            <div class="form-actions">
                <a class="button button-ghost" href="<%= contextPath %>/tasks">取消</a>
                <button class="button" type="submit">保存修改</button>
            </div>
        </form>
    </section>
</div>
</body>
</html>
