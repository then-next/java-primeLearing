<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.taskmaster.util.WebUtil" %>
<%
    String contextPath = request.getContextPath();
    String csrfToken = (String) request.getAttribute("csrfToken");
    String username = (String) request.getAttribute("username");
    String error = (String) request.getAttribute("error");
    boolean loggedOut = Boolean.TRUE.equals(request.getAttribute("loggedOut"));
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>登录 · TaskMaster</title>
    <link rel="stylesheet" href="<%= contextPath %>/css/style.css">
</head>
<body class="auth-page">
<main class="auth-shell">
    <section class="auth-story">
        <p class="eyebrow">TASKMASTER / MINIMAL SYSTEM</p>
        <h1>少一点噪音，<br>多一点完成。</h1>
        <p class="auth-lead">一个只保留必要环节的任务台：输入、存储、检索、展示、变更。背后是 Servlet、JSP 与 JDBC 的完整闭环。</p>
        <div class="loop-points">
            <span><b>01</b> 输入</span>
            <span><b>02</b> 存储</span>
            <span><b>03</b> 检索</span>
            <span><b>04</b> 展示</span>
            <span><b>05</b> 变更</span>
        </div>
        <p class="auth-footnote">原始技术形态不是终点，而是观察重复、耦合与配置问题的放大镜。</p>
    </section>

    <section class="auth-card">
        <div class="brand-line">
            <span class="brand-mark">TM</span>
            <span>TaskMaster</span>
        </div>
        <p class="eyebrow">WELCOME BACK</p>
        <h2>登录任务台</h2>
        <p class="muted auth-intro">用演示账号进入，查看完整的新增、编辑、删除与状态变更链路。</p>

        <% if (loggedOut) { %>
            <div class="alert alert-success">你已安全退出。</div>
        <% } %>
        <% if (error != null) { %>
            <div class="alert alert-error"><%= WebUtil.escapeHtml(error) %></div>
        <% } %>

        <form method="post" action="<%= contextPath %>/login" class="auth-form">
            <input type="hidden" name="_csrf" value="<%= WebUtil.escapeHtml(csrfToken) %>">
            <label class="field">
                <span>用户名</span>
                <input type="text" name="username" maxlength="50"
                       value="<%= WebUtil.escapeHtml(username == null ? "" : username) %>"
                       autocomplete="username" autofocus required>
            </label>
            <label class="field">
                <span>密码</span>
                <input type="password" name="password" maxlength="100"
                       autocomplete="current-password" required>
            </label>
            <button class="button button-block" type="submit">进入任务台 <span>→</span></button>
        </form>

        <div class="demo-account">
            <span>演示账号</span>
            <code>admin</code>
            <span>/</span>
            <code>admin123</code>
        </div>
    </section>
</main>
</body>
</html>
