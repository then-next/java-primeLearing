// 第一阶段原生 javax Filter 留档，不参与 SSM 编译。
package com.taskmaster.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * 在读取任何请求参数之前设置 UTF-8，统一解决 POST 中文乱码；
 * 同时统一响应字符集，页面自身的 contentType 仍由 JSP 明确声明。
 */
public class EncodingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
        // 无初始化参数。
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // 无资源需要释放。
    }
}
