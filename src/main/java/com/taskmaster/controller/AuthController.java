package com.taskmaster.controller;

import com.taskmaster.entity.User;
import com.taskmaster.service.UserService;
import com.taskmaster.util.WebUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginForm(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(WebUtil.SESSION_USER) != null) {
            return "redirect:/tasks";
        }
        model.addAttribute("loggedOut", "1".equals(request.getParameter("logout")));
        model.addAttribute("username", "");
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam(value = "username", required = false) String username,
                        @RequestParam(value = "password", required = false) String password,
                        HttpServletRequest request,
                        Model model) {
        String normalizedUsername = WebUtil.trimToNull(username);
        model.addAttribute("username", normalizedUsername == null ? "" : normalizedUsername);

        if (normalizedUsername == null || password == null || password.isEmpty()) {
            model.addAttribute("error", "请输入用户名和密码");
            return "login";
        }

        User user = userService.authenticate(normalizedUsername, password);
        if (user == null) {
            model.addAttribute("error", "用户名或密码不正确");
            return "login";
        }

        HttpSession session = request.getSession();
        request.changeSessionId();
        session.setAttribute(WebUtil.SESSION_USER, user);
        WebUtil.rotateCsrfToken(session);
        return "redirect:/tasks";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login?logout=1";
    }
}
