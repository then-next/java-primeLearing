package com.taskmaster.filter;

import com.taskmaster.entity.User;
import com.taskmaster.util.WebUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 登录拦截与页面安全响应头。静态资源、登录页放行；
 * 其他页面必须在 Session 中存在 loginUser。
 */
public class AuthFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
        // 无初始化参数。
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String contextPath = request.getContextPath();
        String path = request.getRequestURI().substring(contextPath.length());
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute(WebUtil.SESSION_USER);
        if (user == null) {
            response.sendRedirect(contextPath + "/login");
            return;
        }

        WebUtil.ensureCsrfToken(session);
        request.setAttribute("currentUser", user);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        chain.doFilter(request, response);
    }

    private static boolean isPublicPath(String path) {
        return "/login".equals(path)
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || "/favicon.ico".equals(path);
    }

    @Override
    public void destroy() {
        // 无资源需要释放。
    }
}
