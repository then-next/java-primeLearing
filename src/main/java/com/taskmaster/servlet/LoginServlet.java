package com.taskmaster.servlet;

import com.taskmaster.entity.User;
import com.taskmaster.service.UserService;
import com.taskmaster.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = springBean("userService", UserService.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (currentUser(request) != null) {
            redirect(request, response, "/tasks");
            return;
        }
        HttpSession session = request.getSession();
        request.setAttribute("csrfToken", WebUtil.ensureCsrfToken(session));
        request.setAttribute("loggedOut", "1".equals(request.getParameter("logout")));
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireCsrf(request, response)) {
            return;
        }

        String username = WebUtil.trimToNull(request.getParameter("username"));
        String password = request.getParameter("password");
        request.setAttribute("username", username == null ? "" : username);
        request.setAttribute("csrfToken", WebUtil.csrfToken(request));

        if (username == null || password == null || password.isEmpty()) {
            request.setAttribute("error", "请输入用户名和密码");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        User user = userService.authenticate(username, password);
        if (user == null) {
            request.setAttribute("error", "用户名或密码不正确");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession();
        request.changeSessionId();
        session.setAttribute(WebUtil.SESSION_USER, user);
        WebUtil.rotateCsrfToken(session);
        redirect(request, response, "/tasks");
    }
}
