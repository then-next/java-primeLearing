package com.taskmaster.controller;

import com.taskmaster.service.ServiceException;
import com.taskmaster.util.WebUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalControllerAdvice {
    @ModelAttribute("csrfToken")
    public String csrfToken(HttpServletRequest request) {
        return WebUtil.csrfToken(request);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ModelAndView handleStatus(ResponseStatusException exception) {
        ModelAndView view = new ModelAndView("error");
        view.setStatus(exception.getStatusCode());
        view.addObject("statusCode", exception.getStatusCode().value());
        view.addObject("message", exception.getReason());
        return view;
    }

    @ExceptionHandler(ServiceException.class)
    public ModelAndView handleService(ServiceException exception) {
        ModelAndView view = new ModelAndView("error");
        view.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        view.addObject("statusCode", 500);
        view.addObject("message", "业务处理失败，请查看服务端日志");
        return view;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleOther(Exception exception) {
        ModelAndView view = new ModelAndView("error");
        view.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        view.addObject("statusCode", 500);
        view.addObject("message", "请求处理失败，请查看服务端日志");
        return view;
    }
}
