package com.taskmaster.interceptor;

import com.taskmaster.util.WebUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * 所有 POST 请求统一校验 CSRF，避免在每个 Controller 重复。
 */
public class CsrfInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        if ("POST".equalsIgnoreCase(request.getMethod()) && !WebUtil.verifyCsrf(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF 令牌无效或已过期");
            return false;
        }
        return true;
    }
}
