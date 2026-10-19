package com.taskmaster.servlet;

import com.taskmaster.entity.User;
import com.taskmaster.util.WebUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet 公共能力：取当前用户、校验 CSRF、统一重定向。
 */
public abstract class BaseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected User currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (User) session.getAttribute(WebUtil.SESSION_USER);
    }

    protected boolean requireCsrf(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (!WebUtil.verifyCsrf(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF 令牌无效或已过期");
            return false;
        }
        return true;
    }

    protected void redirect(HttpServletRequest request, HttpServletResponse response, String path)
            throws IOException {
        response.sendRedirect(request.getContextPath() + path);
    }
}
