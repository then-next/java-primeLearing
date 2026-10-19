<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ page import="com.taskmaster.util.WebUtil" %>
<%
    Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
    if (statusCode == null) {
        statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
    }
    String message = (String) request.getAttribute("javax.servlet.error.message");
    if (message == null) {
        message = (String) request.getAttribute("jakarta.servlet.error.message");
    }
    String contextPath = request.getContextPath();
    String title = statusCode != null && statusCode == 404 ? "页面没有找到" : "请求没有完成";
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= title %> · TaskMaster</title>
    <link rel="stylesheet" href="<%= contextPath %>/css/style.css">
</head>
<body class="error-page">
<main class="error-card">
    <span class="error-code"><%= statusCode == null ? "500" : statusCode %></span>
    <p class="eyebrow">SOMETHING WENT OFF TRACK</p>
    <h1><%= title %></h1>
    <p class="muted"><%= WebUtil.escapeHtml(message == null || message.isEmpty() ? "请返回任务台重试，并查看 Tomcat 日志中的异常栈。" : message) %></p>
    <div class="error-actions">
        <a class="button" href="<%= contextPath %>/tasks">返回任务台</a>
        <a class="button button-ghost" href="<%= contextPath %>/login">重新登录</a>
    </div>
</main>
</body>
</html>
